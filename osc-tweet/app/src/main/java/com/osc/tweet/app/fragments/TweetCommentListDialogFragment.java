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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.ScreenSize;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.CommentListAdapter;
import com.osc4j.OscApi;
import com.osc4j.ds.comment.Comments;
import com.osc4j.ds.tweet.TweetListItem;
import com.osc4j.exceptions.OscTweetException;

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

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLoadingIndicatorV = view.findViewById(R.id.loading_pb);

		mRv = (RecyclerView) view.findViewById(R.id.comments_list_rv);
		mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new CommentListAdapter(null));

		mLoadingIndicatorV.setVisibility(View.VISIBLE);
		getCommentsList();

		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color_pocket_1, R.color.color_pocket_2, R.color.color_pocket_3, R.color.color_pocket_4);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getCommentsList();
			}
		});

		TweetListItem item = getTweetItem();
		TextView originalTextTv = (TextView) view.findViewById(R.id.tweet_content_et);
		if (item != null) {
			com.osc.tweet.utils.Utils.showTweetListItem(App.Instance, originalTextTv, item);
		} else {
			originalTextTv.setVisibility(View.GONE);
		}
		ScreenSize sz = DeviceUtils.getScreenSize(getActivity().getApplication());
		view.findViewById(R.id.root_v).setLayoutParams(new FrameLayout.LayoutParams(sz.Width, sz.Height));

		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.lbl_comments);
	}

	/**
	 * Load all comments.
	 */
	private void getCommentsList() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, Object, Comments>() {


			@Override
			protected Comments doInBackground(Object... params) {
				try {
					return OscApi.tweetCommentList(App.Instance, getTweetItem());
				} catch (IOException e) {
					return null;
				} catch (OscTweetException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Comments comments) {
				super.onPostExecute(comments);
				mAdp.setData(comments.getComments());
				mAdp.notifyDataSetChanged();
				mLoadingIndicatorV.setVisibility(View.INVISIBLE);
				mSwipeRefreshLayout.setRefreshing(false);
			}
		});
	}

	/**
	 *
	 * @return The tweet object to comment.
	 */
	private TweetListItem getTweetItem() {
		return (TweetListItem) getArguments().getSerializable(
				EXTRAS_TWEET_ITEM);
	}
}
