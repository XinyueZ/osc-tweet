package com.osc.tweet.app.fragments;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.NoticesListAdapter;
import com.osc.tweet.events.ClearNoticeEvent;
import com.osc.tweet.events.GetMyInformationEvent;
import com.osc.tweet.events.RefreshMyInfoEvent;
import com.osc.tweet.utils.Prefs;
import com.osc4j.ds.common.NoticeType;
import com.osc4j.ds.personal.Notice;
import com.osc4j.ds.personal.Notices;

import de.greenrobot.event.EventBus;

/**
 * Show list of notice.
 */
public final class NoticesListPageFragment extends BaseFragment {
	private static final String EXTRAS_NOTICES = NoticesListPageFragment.class.getName() + ".EXTRAS.notices";
	private static final String EXTRAS_TYPE = NoticesListPageFragment.class.getName() + ".EXTRAS.type";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_notices_list_page;
	/**
	 * Adapter for {@link #mRv} to show all notices.
	 */
	private NoticesListAdapter mAdp;
	/**
	 * List container for showing all notices.
	 */
	private RecyclerView mRv;
	/**
	 * Show when list is empty.
	 */
	private View mEmptyV;
	/**
	 * Pull to load.
	 */
	private SwipeRefreshLayout mSwipeRefreshLayout;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.osc.tweet.events.ClearNoticeEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.ClearNoticeEvent}.
	 */
	public void onEvent(ClearNoticeEvent e) {
		clearList(e.getType());
	}

	/**
	 * Handler for {@link com.osc.tweet.events.GetMyInformationEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.GetMyInformationEvent}.
	 */
	public void onEvent(GetMyInformationEvent e) {
		mSwipeRefreshLayout.setRefreshing(false);
	}

	//------------------------------------------------

	/**
	 * Init a {@link NoticesListPageFragment}.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param notices
	 * 		{@link Notices}, that contains objects to show.
	 * @param type
	 * 		{@link NoticeType}, type of notices.
	 *
	 * @return {@link NoticesListPageFragment}.
	 */
	public static Fragment newInstance(Context context, @Nullable Notices notices, NoticeType type) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_NOTICES, notices);
		args.putSerializable(EXTRAS_TYPE, type);
		return NoticesListPageFragment.instantiate(context, NoticesListPageFragment.class.getName(), args);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRv = (RecyclerView) view.findViewById(R.id.child_view);
		mEmptyV = view.findViewById(R.id.empty_notices_iv);
		View errorV = view.findViewById(R.id.errors_iv);


		mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRv.addItemDecoration(new SpacesItemDecoration((int) com.osc4j.utils.Utils.convertPixelsToDp(App.Instance, 5)));
		mRv.setItemAnimator(new DefaultItemAnimator());
		mRv.setHasFixedSize(false);


		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color_pocket_1, R.color.color_pocket_2,
				R.color.color_pocket_3, R.color.color_pocket_4);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				EventBus.getDefault().post(new RefreshMyInfoEvent());
			}
		});


		if (getType() == NoticeType.Null || getNotices() == null) {
			errorV.setVisibility(View.VISIBLE);
			mRv.setVisibility(View.INVISIBLE);
			mEmptyV.setVisibility(View.INVISIBLE);
		} else {
			List<Notice> notices = getNotices().getNotices();
			if (notices != null && notices.size() > 0) {
				mRv.setAdapter(mAdp = new NoticesListAdapter(notices, getType()));
				mRv.setVisibility(View.VISIBLE);
				mEmptyV.setVisibility(View.INVISIBLE);
			} else {
				mRv.setVisibility(View.INVISIBLE);
				mEmptyV.setVisibility(View.VISIBLE);
			}
		}


	}

	/**
	 * @return For which type of notice.
	 */
	private NoticeType getType() {
		return (NoticeType) getArguments().getSerializable(EXTRAS_TYPE);
	}

	/**
	 * Clear the list.
	 *
	 * @param type
	 * 		{@link NoticeType}. The type of notices of this list.
	 */
	private void clearList(NoticeType type) {
		if (getType() != type) {
			return;
		}
		if (mAdp != null) {
			mAdp.setData(null);
			mAdp.notifyDataSetChanged();
		}
		mRv.setVisibility(View.INVISIBLE);
		mEmptyV.setVisibility(View.VISIBLE);
	}

	/**
	 * @return {@link Notice}s to show.
	 */
	private
	@Nullable
	Notices getNotices() {
		if (getArguments().getSerializable(EXTRAS_NOTICES) instanceof Notices) {
			return (Notices) getArguments().getSerializable(EXTRAS_NOTICES);
		} else {
			return null;
		}
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	/**
	 * Walk around to make padding or margin of every line on the {@link RecyclerView}.
	 */
	public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
		private int space;

		public SpacesItemDecoration(int space) {
			this.space = space;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			outRect.left = space;
			outRect.right = space;
			outRect.bottom = space;

			// Add top margin only for the first item to avoid double space between items
			if (parent.getChildPosition(view) == 0) {
				outRect.top = space * 2;
			}
		}
	}
}
