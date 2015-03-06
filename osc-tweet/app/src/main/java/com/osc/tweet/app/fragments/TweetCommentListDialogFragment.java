package com.osc.tweet.app.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.bus.ReloadEvent;
import com.chopping.net.TaskHelper;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.ScreenSize;
import com.chopping.utils.Utils;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.CommentListAdapter;
import com.osc.tweet.events.CommentTweetEvent;
import com.osc.tweet.events.LoadEvent;
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc4j.OscApi;
import com.osc4j.ds.comment.Comments;
import com.osc4j.ds.tweet.TweetListItem;
import com.osc4j.exceptions.OscTweetException;

import de.greenrobot.event.EventBus;

/**
 * The list of comments of a {@link com.osc4j.ds.tweet.TweetListItem}.
 *
 * @author Xinyue Zhao
 */
public final class TweetCommentListDialogFragment extends DialogFragment {
	private static final String EXTRAS_TWEET_ITEM =
			TweetCommentListDialogFragment.class.getName() + ".EXTRAS.tweetItem";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_dialog_tweet_comment_list;

	/**
	 * The menu of "actionbar".
	 */
	private static final int MENU = R.menu.menu_comments_list;
	/**
	 * Adapter for {@link #mRv} to show all comments.
	 */
	private CommentListAdapter mAdp;
	/**
	 * List container for showing all comments.
	 */
	private RecyclerView mRv;
	/**
	 * Indicator of loading.
	 */
	private View mLoadingIndicatorV;
	/**
	 * Pull to load.
	 */
	private SwipeRefreshLayout mSwipeRefreshLayout;

	private LinearLayoutManager mLayoutManager;

	/**
	 * The page of tweets to load.
	 */
	private int mPage = DEFAULT_PAGE;

	private static final int DEFAULT_PAGE = 1;
	/**
	 * On the bottom of all records.
	 */
	private boolean mBottom;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.osc.tweet.events.LoadEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.LoadEvent}.
	 */
	public void onEvent(LoadEvent e) {
		prepareGetCommentsList();
		getCommentsList();
	}


	/**
	 * Handler for {@link com.chopping.bus.ReloadEvent}.
	 *
	 * @param e
	 * 		Event {@link com.chopping.bus.ReloadEvent}.
	 */
	public void onEvent(ReloadEvent e) {
		prepareGetCommentsList();
		getCommentsList();
	}



	//------------------------------------------------

	/**
	 * Create an instance of {@link com.osc.tweet.app.fragments.TweetCommentListDialogFragment}.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param tweetListItem
	 * 		{@link TweetListItem} that provides comment-list.
	 *
	 * @return An instance of {@link com.osc.tweet.app.fragments.TweetCommentListDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context, TweetListItem tweetListItem) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_TWEET_ITEM, tweetListItem);
		return (DialogFragment) Fragment.instantiate(context, TweetCommentListDialogFragment.class.getName(), args);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Transparent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	private int mVisibleItemCount;
	private int mPastVisibleItems;
	private int mTotalItemCount;
	private boolean mLoading = true;


	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		EventBus.getDefault().register(this);
		mLoadingIndicatorV = view.findViewById(R.id.loading_pb);

		mRv = (RecyclerView) view.findViewById(R.id.comments_list_rv);
		mRv.setLayoutManager(mLayoutManager = new LinearLayoutManager(getActivity()));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new CommentListAdapter(getTweetItem(), null));
		mRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

				mVisibleItemCount = mLayoutManager.getChildCount();
				mTotalItemCount = mLayoutManager.getItemCount();
				mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

				if (mLoading) {
					if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount ) {
						mLoading = false;
						if( !mBottom) {
							mLoadingIndicatorV.setVisibility(View.VISIBLE);
							getMoreCommentsList();
						}
					}
				}
			}
		});

		mLoadingIndicatorV.setVisibility(View.VISIBLE);
		getCommentsList();

		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color_pocket_1, R.color.color_pocket_2,
				R.color.color_pocket_3, R.color.color_pocket_4);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mPage = DEFAULT_PAGE;
				mBottom = false;
				getCommentsList();
			}
		});

		//Show original tweet information, photo of editor and content of tweet.
		TweetListItem item = getTweetItem();
		TextView originalTextTv = (TextView) view.findViewById(R.id.tweet_content_et);
		com.osc.tweet.utils.Utils.showTweetListItem(App.Instance, originalTextTv, item);
		NetworkImageView photoIv = (NetworkImageView) view.findViewById(R.id.portrait_iv);
		photoIv.setDefaultImageResId(R.drawable.ic_not_loaded);
		photoIv.setImageUrl(item.getPortrait(), TaskHelper.getImageLoader());
		photoIv.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				TweetListItem item = getTweetItem();
				EventBus.getDefault().post(new ShowUserInformationEvent(item.getAuthorId()));
			}
		});


		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.lbl_comments);
		toolbar.inflateMenu(MENU);
		toolbar.getMenu().findItem(R.id.action_reply).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				EventBus.getDefault().post(new CommentTweetEvent(getTweetItem(), null));
				return true;
			}
		});
		toolbar.getMenu().findItem(R.id.action_top).setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (mAdp != null && mAdp.getItemCount() > 0) {
					mLayoutManager.scrollToPositionWithOffset(0, 0);
					Utils.showShortToast(App.Instance, R.string.msg_move_to_top);
				}
				return true;
			}
		});

		ScreenSize sz = DeviceUtils.getScreenSize(getActivity().getApplication());
		view.findViewById(R.id.root_v).setLayoutParams(new FrameLayout.LayoutParams(sz.Width,
				LayoutParams.MATCH_PARENT));

	}

	@Override
	public void onDestroyView() {
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

	/**
	 * Load all comments.
	 */
	private void getCommentsList() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, Object, Comments>() {


			@Override
			protected Comments doInBackground(Object... params) {
				try {
					return OscApi.tweetCommentList(App.Instance, getTweetItem(), mPage);
				} catch (IOException e) {
					return null;
				} catch (OscTweetException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Comments comments) {
				super.onPostExecute(comments);
				mAdp.setData(getTweetItem(), comments.getComments());
				finishLoading();
				mLayoutManager.scrollToPositionWithOffset(0, 0);
			}
		});
	}

	/**
	 * Load more comments.
	 */
	private void getMoreCommentsList() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, Object, Comments>() {


			@Override
			protected Comments doInBackground(Object... params) {
				try {
					return OscApi.tweetCommentList(App.Instance, getTweetItem(), mPage);
				} catch (IOException e) {
					return null;
				} catch (OscTweetException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Comments comments) {
				super.onPostExecute(comments);
				if(comments.getComments() != null) {
					mAdp.getData().addAll(comments.getComments());
				} else {
					mBottom = true;
				}
				finishLoading();
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
		mLoadingIndicatorV.setVisibility(View.INVISIBLE);
		mSwipeRefreshLayout.setRefreshing(false);
	}

	/**
	 * Reset all data before loading new feeds.
	 */
	private void prepareGetCommentsList() {
		mPage = DEFAULT_PAGE;
		mLoadingIndicatorV.setVisibility(View.VISIBLE);
		mBottom = false;
	}


	/**
	 * @return The tweet object to comment.
	 */
	private TweetListItem getTweetItem() {
		return (TweetListItem) getArguments().getSerializable(EXTRAS_TWEET_ITEM);
	}

}
