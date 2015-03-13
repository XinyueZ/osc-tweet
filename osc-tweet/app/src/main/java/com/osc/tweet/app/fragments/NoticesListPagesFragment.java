package com.osc.tweet.app.fragments;


import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.NoticesListViewPagerAdapter;
import com.osc.tweet.events.ClearAtMeNoticesEvent;
import com.osc.tweet.events.ClearCommentsEvent;
import com.osc.tweet.events.ClearNoticeEvent;
import com.osc.tweet.events.GetMyInformationEvent;
import com.osc.tweet.events.OperatingEvent;
import com.osc.tweet.events.RefreshMyInfoEvent;
import com.osc.tweet.utils.Prefs;
import com.osc4j.OscApi;
import com.osc4j.ds.common.NoticeType;
import com.osc4j.ds.common.StatusResult;
import com.osc4j.ds.personal.MyInformation;
import com.osc4j.exceptions.OscTweetException;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

import static com.osc4j.ds.common.NoticeType.AtMe;
import static com.osc4j.ds.common.NoticeType.Comments;

/**
 * A container shows all my new notices.
 *
 * @author Xinyue Zhao
 */
public final class NoticesListPagesFragment extends BaseFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_notices_list_pages;
	/**
	 * Tabs.
	 */
	private PagerSlidingTabStrip mTabs;

	/**
	 * The pagers
	 */
	private ViewPager mViewPager;

	/**
	 * My personal information .
	 */
	private MyInformation myInfo;

	/**
	 * Main {@link MenuItem}.
	 */
	private MenuItem mClearMi;
	/**
	 * The {@link MenuItem} to clear all new notices of new "@Me".
	 */
	private MenuItem mClearAtMi;
	/**
	 * The {@link MenuItem} to clear all new notices of new comments.
	 */
	private MenuItem mClearCommentsMi;
	/**
	 * The {@link MenuItem} to get my newest personal information.
	 */
	private MenuItem mRefreshMi;
	/**
	 * Progress indicator.
	 */
	private SmoothProgressBar mPbV;

	/**
	 * Network action in progress if {@code true}.
	 */
	private boolean mInProgress;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.osc.tweet.events.GetMyInformationEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.GetMyInformationEvent}.
	 */
	public void onEvent(GetMyInformationEvent e) {
		myInfo = e.getMyInformation();
		showMyNotices();
	}

	/**
	 * Handler for {@link com.osc.tweet.events.ClearAtMeNoticesEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.ClearAtMeNoticesEvent}.
	 */
	public void onEvent(ClearAtMeNoticesEvent e) {
		clearAtMe();
	}


	/**
	 * Handler for {@link com.osc.tweet.events.ClearCommentsEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.ClearCommentsEvent}.
	 */
	public void onEvent(ClearCommentsEvent e) {
		clearNewComments();
	}


	//------------------------------------------------


	/**
	 * Initialize an {@link  NoticesListPagesFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link NoticesListPagesFragment}.
	 */
	public static NoticesListPagesFragment newInstance(Context context) {
		return (NoticesListPagesFragment) Fragment.instantiate(context, NoticesListPagesFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("myinfo", myInfo);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mViewPager = (ViewPager) view.findViewById(R.id.my_notices_vp);
		// Bind the tabs to the ViewPager
		mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.my_notices_tabs);
		mTabs.setIndicatorColorResource(R.color.common_white);

		if (savedInstanceState != null) {
			myInfo = (MyInformation) savedInstanceState.getSerializable("myinfo");
		}
		mPbV = (SmoothProgressBar) view.findViewById(R.id.clear_pb);
		mPbV.setSmoothProgressDrawableBackgroundDrawable(SmoothProgressBarUtils.generateDrawableWithColors(
				getResources().getIntArray(R.array.pocket_background_colors),
				((SmoothProgressDrawable) mPbV.getIndeterminateDrawable()).getStrokeWidth()));
		mPbV.progressiveStart();


		Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
		toolbar.inflateMenu(R.menu.menu_notices);
		Menu menu = toolbar.getMenu();
		mClearMi = menu.findItem(R.id.action_clear_notices);
		mClearAtMi = menu.findItem(R.id.action_clear_at_me);
		mClearAtMi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				mPbV.setVisibility(View.VISIBLE);
				mClearMi.setEnabled(false);
				clearAtMe();
				return true;
			}
		});
		mClearCommentsMi = menu.findItem(R.id.action_clear_comments);
		mClearCommentsMi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				mPbV.setVisibility(View.VISIBLE);
				mClearMi.setEnabled(false);
				clearNewComments();
				return true;
			}
		});
		mRefreshMi = menu.findItem(R.id.action_refresh);
		mRefreshMi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				mPbV.setVisibility(View.VISIBLE);
				mRefreshMi.setEnabled(false);
				EventBus.getDefault().post(new RefreshMyInfoEvent());
				return true;
			}
		});
		showMyNotices();

	}

	/**
	 * Show all my current new notices.
	 */
	private void showMyNotices() {
		mPbV.setVisibility(View.GONE);
		mPbV.progressiveStop();

		if (myInfo != null && myInfo.getAm() != null) {
			mViewPager.setAdapter(new NoticesListViewPagerAdapter(App.Instance, getChildFragmentManager(), myInfo));
			mTabs.setViewPager(mViewPager);


			//Menu shows dynamically according to the count of list-items.
			int atMeCount = myInfo.getNotices() == null ? 0 : myInfo.getNotices().size();
			int cmmCount = myInfo.getComments() == null ? 0 : myInfo.getComments().size();

			mClearMi.setVisible(atMeCount != 0 || cmmCount != 0);
			mClearAtMi.setVisible(atMeCount > 0);
			mClearCommentsMi.setVisible(cmmCount > 0);

			if (atMeCount < cmmCount) {
				mViewPager.setCurrentItem(1);
			}
		} else {
			mViewPager.setAdapter(new NoticesListViewPagerAdapter(App.Instance, getChildFragmentManager(), null));
			mTabs.setViewPager(mViewPager);


			//Menu shows dynamically according to the count of list-items.
			mClearMi.setVisible(false);
			mClearAtMi.setVisible(false);
			mClearCommentsMi.setVisible(false);
		}

		mRefreshMi.setEnabled(true);

	}


	/**
	 * Clear all new "@me".
	 */
	private void clearAtMe() {
		clear(AtMe);
	}

	/**
	 * Clear all new comments.
	 */
	private void clearNewComments() {
		clear(Comments);
	}

	/**
	 * Clear different notices.
	 *
	 * @param type
	 * 		{@link NoticeType} The type of notice.
	 */
	private void clear(NoticeType type) {
		if (!mInProgress) {
			AsyncTaskCompat.executeParallel(new AsyncTask<NoticeType, Void, StatusResult>() {
				NoticeType mNoticeType;

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mInProgress = true;
					mPbV.setVisibility(View.VISIBLE);
				}

				@Override
				protected StatusResult doInBackground(NoticeType... params) {
					mNoticeType = params[0];
					try {
						return OscApi.clearNotice(App.Instance, mNoticeType);
					} catch (IOException e) {
						return null;
					} catch (OscTweetException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(StatusResult res) {
					super.onPostExecute(res);
					try {
						OperatingEvent event = new OperatingEvent(
								res != null && res.getResult() != null && Integer.valueOf(res.getResult().getCode()) ==
										com.osc4j.ds.common.Status.STATUS_OK);
						EventBus.getDefault().post(event);
						if (event.isSuccess()) {
							EventBus.getDefault().post(new ClearNoticeEvent(mNoticeType));
							switch (mNoticeType) {
							case AtMe:
								mClearAtMi.setVisible(false);
								break;
							case Comments:
								mClearCommentsMi.setVisible(false);
								break;
							}
							if (!mClearAtMi.isVisible() && !mClearCommentsMi.isVisible()) {
								mClearMi.setVisible(false);
							}
						}
					} catch (IllegalStateException e) {
						//Activity has been destroyed
					}
					mPbV.setVisibility(View.GONE);
					mPbV.progressiveStop();
					mClearMi.setEnabled(true);
					mInProgress = false;
				}
			}, type);
		}
	}


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
