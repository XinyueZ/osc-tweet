package com.osc.tweet.app.fragments;

import java.io.IOException;

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
import com.chopping.fragments.BaseFragment;
import com.chopping.utils.Utils;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.TweetListAdapter;
import com.osc.tweet.events.LoadEvent;
import com.osc.tweet.events.ShowingLoadingEvent;
import com.osc.tweet.utils.Prefs;
import com.osc4j.OscApi;
import com.osc4j.OscTweetException;
import com.osc4j.ds.tweet.TweetList;

import de.greenrobot.event.EventBus;

/**
 * A list shows all tweets.
 */
public final class TweetListFragment extends BaseFragment {
	private static final String EXTRAS_MY_TWEETS = "com.osc.tweet.app.fragments.MY_TWEETS";
	private static final String EXTRAS_HOTSPOT = "com.osc.tweet.app.fragments.HOTSPOT";
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


	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link LoadEvent}.
	 *
	 * @param e
	 * 		Event {@link LoadEvent}.
	 */
	public void onEvent(LoadEvent e) {
		getTweetList();
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
	 * @param myTweets
	 *
	 * @return An instance of {@link com.osc.tweet.app.fragments.TweetListFragment}.
	 */
	public static Fragment newInstance(Context context, boolean myTweets, boolean hotspot) {
		Bundle args = new Bundle();
		args.putBoolean(EXTRAS_MY_TWEETS, myTweets);
		args.putBoolean(EXTRAS_HOTSPOT, hotspot);
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
		mNotLoadedIndicatorV = view.findViewById(R.id.not_loaded_ll);
		mRv = (RecyclerView) view.findViewById(R.id.tweet_list_rv);
		mRv.setLayoutManager(mLayoutManager = new LinearLayoutManager(getActivity()));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new TweetListAdapter(null));

		mRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

				mVisibleItemCount = mLayoutManager.getChildCount();
				mTotalItemCount = mLayoutManager.getItemCount();
				mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

				if (mLoading) {
					if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
						mLoading = false;
						showLoadingIndicator();
						getMoreTweetList();
					}
				}
			}
		});

		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color_1, R.color.color_2, R.color.color_3, R.color.color_4);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mPage = DEFAULT_PAGE;
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
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, TweetList, TweetList>() {
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
				if (tweetList != null) {
					mAdp.setData(tweetList.getTweets());
					finishLoading();
				}
			}
		});
	}

	/**
	 * Load more and more tweets on to list.
	 */
	private void getMoreTweetList() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, TweetList, TweetList>() {
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
				if (tweetList != null) {
					mAdp.getData().addAll(tweetList.getTweets());
					finishLoading();
				}
			}
		});
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


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
