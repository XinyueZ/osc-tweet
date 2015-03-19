package com.osc.tweet.app.fragments;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chopping.application.BasicPrefs;
import com.chopping.bus.ReloadEvent;
import com.chopping.fragments.BaseFragment;
import com.chopping.utils.Utils;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.TweetListAdapter;
import com.osc.tweet.events.AddFavEvent;
import com.osc.tweet.events.DeletedFavEvent;
import com.osc.tweet.events.ShowingLoadingEvent;
import com.osc.tweet.utils.Prefs;
import com.osc4j.OscApi;
import com.osc4j.ds.favorite.TweetFavoritesList;
import com.osc4j.ds.tweet.TweetList;
import com.osc4j.ds.tweet.TweetListItem;
import com.osc4j.exceptions.OscTweetException;

import de.greenrobot.event.EventBus;

/**
 * A list shows all tweets.
 */
public final class TweetListFragment extends BaseFragment {
	private static final String EXTRAS_MY_TWEETS = "com.osc.tweet.app.fragments.MY_TWEETS";
	private static final String EXTRAS_HOTSPOT = "com.osc.tweet.app.fragments.HOTSPOT";
	private static final String EXTRAS_FAV = "com.osc.tweet.app.fragments.fav";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_tweet_list;
	/**
	 * Adapter for {@link #mRv} to show all tweets.
	 */
	private TweetListAdapter mAdp;
	/**
	 * List container for showing all tweets.
	 */
	private RecyclerView mRv;

	/**
	 * The page of tweets to load.
	 */
	private int mPage = DEFAULT_PAGE;

	private static final int DEFAULT_PAGE = 1;
	/**
	 * Pull to load.
	 */
	private SwipeRefreshLayout mSwipeRefreshLayout;
	/**
	 * Some fun indicator when data not loaded.
	 */
	private View mNotLoadedIndicatorV;

	/**
	 * On the bottom of all records.
	 */
	private boolean mBottom;
	/**
	 * Network action in progress if {@code true}.
	 */
	private boolean mInProgress;
	/**
	 * Indicator for empty feeds.
	 */
	private View mEmptyV;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.chopping.bus.ReloadEvent}.
	 *
	 * @param e
	 * 		Event {@link com.chopping.bus.ReloadEvent}.
	 */
	public void onEvent(ReloadEvent e) {
		mPage = DEFAULT_PAGE;
		showLoadingIndicator();
		mBottom = false;
		getTweetList();
	}

	/**
	 * Handler for {@link com.osc.tweet.events.AddFavEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.AddFavEvent}.
	 */
	public void onEvent(AddFavEvent e) {
		notifyWhenFavChanged();
	}


	/**
	 * Handler for {@link com.osc.tweet.events.DeletedFavEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.DeletedFavEvent}.
	 */
	public void onEvent(DeletedFavEvent e) {
		notifyWhenFavChanged();
	}

	//------------------------------------------------

	/**
	 * Create an instance of {@link com.osc.tweet.app.fragments.TweetListFragment}.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param myTweets
	 * 		If <code>true</code> then show all my tweets, it works only when <code>hotspot</code> is <code>false</code>.
	 * @param hotspot
	 * 		If <code>true</code> then show all hotspot and ignore value of <code>myTweets</code>.
	 * @param fav
	 * 		If <code>true</code> then show all hotspot and ignore value of <code>myTweets</code>.
	 * @param myTweets
	 *
	 * @return An instance of {@link com.osc.tweet.app.fragments.TweetListFragment}.
	 */
	public static Fragment newInstance(Context context, boolean myTweets, boolean hotspot, boolean fav) {
		Bundle args = new Bundle();
		args.putBoolean(EXTRAS_MY_TWEETS, myTweets);
		args.putBoolean(EXTRAS_HOTSPOT, hotspot);
		args.putBoolean(EXTRAS_FAV, fav);
		return TweetListFragment.instantiate(context, TweetListFragment.class.getName(), args);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_top:
			if (mAdp != null && mAdp.getItemCount() > 0) {
				mLayoutManager.scrollToPositionWithOffset(0, 0);
				Utils.showShortToast(App.Instance, R.string.msg_move_to_top);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private int mVisibleItemCount;
	private int mPastVisibleItems;
	private int mTotalItemCount;
	private boolean mLoading = true;
	LinearLayoutManager mLayoutManager;


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(true);
		mEmptyV = view.findViewById(R.id.empty_ll);
		mNotLoadedIndicatorV = view.findViewById(R.id.not_loaded_ll);
		mRv = (RecyclerView) view.findViewById(R.id.tweet_list_rv);
		mRv.setLayoutManager(mLayoutManager = new LinearLayoutManager(getActivity()));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new TweetListAdapter(null));
		if (!getArguments().getBoolean(EXTRAS_FAV, false)) {
			mRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

					mVisibleItemCount = mLayoutManager.getChildCount();
					mTotalItemCount = mLayoutManager.getItemCount();
					mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

					if (mLoading) {
						if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
							mLoading = false;
							if (!mBottom) {
								showLoadingIndicator();
								getMoreTweetList();
							}
						}
					}
				}
			});
		}

		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color_pocket_1, R.color.color_pocket_2,
				R.color.color_pocket_3, R.color.color_pocket_4);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mPage = DEFAULT_PAGE;
				mBottom = false;
				getTweetList();
			}
		});

		getTweetList();
	}


	/**
	 * Show progress when loading.
	 */
	private void showLoadingIndicator() {
		EventBus.getDefault().post(new ShowingLoadingEvent(true));
	}

	/**
	 * Dismiss progress when load.
	 */
	private void dismissLoadingIndicator() {
		EventBus.getDefault().post(new ShowingLoadingEvent(false));
	}

	/**
	 * Get tweets data and showing on list.
	 */
	private void getTweetList() {
		if (!mInProgress) {

			AsyncTaskCompat.executeParallel(new AsyncTask<Object, List<TweetListItem>, List<TweetListItem>>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mInProgress = true;
				}

				@Override
				protected List<TweetListItem> doInBackground(Object... params) {
					try {
						if (getArguments().getBoolean(EXTRAS_FAV, false)) {
							if (App.Instance.getTweetFavoritesList() == null) {
								TweetFavoritesList result = OscApi.tweetFavoritesList(App.Instance);
								if (result.getStatus() == com.osc4j.ds.common.Status.STATUS_OK) {
									App.Instance.setTweetFavoritesList(result.getTweets());
								}
							}
							return App.Instance.getTweetFavoritesList();
						} else {
							return OscApi.tweetList(App.Instance, mPage, getArguments().getBoolean(EXTRAS_MY_TWEETS,
									false), getArguments().getBoolean(EXTRAS_HOTSPOT, false)).getTweets();
						}
					} catch (IOException e) {
						return null;
					} catch (OscTweetException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(List<TweetListItem> tweetList) {
					super.onPostExecute(tweetList);
					try {
						if (tweetList != null) {
							if( tweetList.size() > 0) {
								if (getArguments().getBoolean(EXTRAS_FAV, false)) {
									App.Instance.setTweetFavoritesList(tweetList);
								}
								mAdp.setData(tweetList);
								mEmptyV.setVisibility(View.GONE);
								mNotLoadedIndicatorV.setVisibility(View.GONE);
							} else {
								mEmptyV.setVisibility(View.VISIBLE);
								mNotLoadedIndicatorV.setVisibility(View.GONE);
							}
							finishLoading();
							mLayoutManager.scrollToPositionWithOffset(0, 0);
						} else {
							mEmptyV.setVisibility(View.GONE);
							mNotLoadedIndicatorV.setVisibility(View.VISIBLE);
						}
					} catch (IllegalStateException e) {
						//Activity has been destroyed
					}
					mInProgress = false;
				}
			});
		}
	}

	/**
	 * Load more and more tweets on to list.
	 */
	private void getMoreTweetList() {
		if (!mInProgress) {
			AsyncTaskCompat.executeParallel(new AsyncTask<Object, TweetList, TweetList>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mInProgress = true;
				}

				@Override
				protected TweetList doInBackground(Object... params) {
					try {
						return OscApi.tweetList(App.Instance, mPage, getArguments().getBoolean(EXTRAS_MY_TWEETS, false),
								getArguments().getBoolean(EXTRAS_HOTSPOT, false));
					} catch (IOException e) {
						return null;
					} catch (OscTweetException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(TweetList tweetList) {
					super.onPostExecute(tweetList);
					try {
						if (tweetList != null && tweetList.getTweets() != null) {
							mAdp.getData().addAll(tweetList.getTweets());
						} else {
							if (tweetList != null) {
								mPage--;
								if (mPage < 0) {
									mPage = 0;
								}
							} else {
								mBottom = true;
							}
						}
						finishLoading();
					} catch (IllegalStateException e) {
						//Activity has been destroyed
					}
					mInProgress = false;
				}
			});
		}
	}

	/**
	 * Calls when loading data has been done.
	 */
	private void finishLoading() {
		mAdp.notifyDataSetChanged();
		mPage++;
		mLoading = true;
		dismissLoadingIndicator();
		mSwipeRefreshLayout.setRefreshing(false);
		mNotLoadedIndicatorV.setVisibility(View.GONE);
	}

	/**
	 * Fav-list has been changed.
	 */
	private void notifyWhenFavChanged() {
		if (getArguments().getBoolean(EXTRAS_FAV, false)) {
			getTweetList();
		} else {
			if (mAdp != null) {
				mAdp.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
