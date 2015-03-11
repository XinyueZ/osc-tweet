package com.osc.tweet.app.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.chopping.net.TaskHelper;
import com.chopping.utils.Utils;
import com.nineoldandroids.animation.ObjectAnimator;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.activities.SettingActivity;
import com.osc.tweet.app.adapters.NoticesListsViewPagerAdapter;
import com.osc.tweet.events.ClearNoticeEvent;
import com.osc.tweet.events.OperatingEvent;
import com.osc.tweet.utils.Prefs;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc.tweet.views.RoundedNetworkImageView;
import com.osc4j.OscApi;
import com.osc4j.ds.common.NoticeType;
import com.osc4j.ds.common.StatusResult;
import com.osc4j.ds.personal.Am;
import com.osc4j.ds.personal.MyInformation;
import com.osc4j.exceptions.OscTweetException;

import de.greenrobot.event.EventBus;

import static com.osc4j.ds.common.NoticeType.AtMe;
import static com.osc4j.ds.common.NoticeType.Comments;

/**
 * Show my information.
 *
 * @author Xinyue Zhao
 */
public final class MyInfoFragment extends BaseFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_my_info;
	/**
	 * Menu-resource of the popup.
	 */
	private static final int MENU_RES = R.menu.menu_my_info;
	/**
	 * My photo.
	 */
	private RoundedNetworkImageView mUserPhotoIv;
	/**
	 * My name.
	 */
	private TextView mUserNameTv;
	/**
	 * My information personal.
	 */
	private MyInformation myInfo;
	/**
	 * Click to refresh my-info.
	 */
	private View mRefreshV;
	/**
	 * The pagers
	 */
	private ViewPager mViewPager;
	/**
	 * The adapter to {@link #mViewPager}.
	 */
	private NoticesListsViewPagerAdapter mAdp;
	/**
	 * Tabs.
	 */
	private PagerSlidingTabStrip mTabs;
	/**
	 * Root of all views of this {@link Fragment}.
	 */
	private View mRootV;

	/**
	 * The popup-menu to clear all list.
	 */
	private PopupMenu mPopupMenu;
	/**
	 * Progress indicator for clearing.
	 */
	private View mClearPb;

	/**
	 * Open the popup of clearing lists.
	 */
	private View mClearListV;

	/**
	 * Initialize an {@link  MyInfoFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link MyInfoFragment}.
	 */
	public static MyInfoFragment newInstance(Context context) {
		return (MyInfoFragment) Fragment.instantiate(context, MyInfoFragment.class.getName());
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRootV = view.findViewById(R.id.root_ll);
		mUserPhotoIv = (RoundedNetworkImageView) view.findViewById(R.id.user_photo_iv);
		mUserNameTv = (TextView) view.findViewById(R.id.user_name_tv);
		mRefreshV = view.findViewById(R.id.refresh_btn);
		getMyInformation();
		mRefreshV.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				getMyInformation();
			}
		});


		mViewPager = (ViewPager) view.findViewById(R.id.vp);
		// Bind the tabs to the ViewPager
		mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		mTabs.setIndicatorColorResource(R.color.common_white);


		mClearListV = view.findViewById(R.id.clean_list_btn);
		mPopupMenu = new PopupMenu(getActivity(), mClearListV);
		mPopupMenu.inflate(MENU_RES);
		mClearListV.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				mPopupMenu.show();
			}
		});
		mPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				mClearListV.setVisibility(View.INVISIBLE);
				mClearPb.setVisibility(View.VISIBLE);
				switch (menuItem.getItemId()) {
				case R.id.action_clear_at_me:
					clearAtMe();
					break;
				case R.id.action_clear_comments:
					clearNewComments();
					break;
				}
				return true;
			}
		});

		mClearPb = view.findViewById(R.id.clear_pb);

		view.findViewById(R.id.settings_btn).setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				SettingActivity.showInstance(getActivity());
			}
		});
	}


	/**
	 * Get my personal information.
	 */
	private void getMyInformation() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, MyInformation, MyInformation>() {
			ObjectAnimator objectAnimator;


			@Override
			protected void onPreExecute() {
				mRefreshV.setEnabled(false);
				objectAnimator = ObjectAnimator.ofFloat(mRefreshV, "rotation", 0, 360f);
				objectAnimator.setDuration(800);
				objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
				objectAnimator.start();
				super.onPreExecute();
			}

			@Override
			protected MyInformation doInBackground(Object... params) {
				try {
					return OscApi.myInformation(App.Instance);
				} catch (IOException e) {
					return null;
				} catch (OscTweetException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(MyInformation myInfo) {
				super.onPostExecute(myInfo);
				try {
					if (myInfo != null && myInfo.getAm() != null) {
						MyInfoFragment.this.myInfo = myInfo;
						Am am = MyInfoFragment.this.myInfo.getAm();
						mUserPhotoIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
						mUserPhotoIv.setImageUrl(am.getPortrait(), TaskHelper.getImageLoader());
						mUserNameTv.setText(am.getName());

						mViewPager.setAdapter(mAdp = new NoticesListsViewPagerAdapter(App.Instance,
								getChildFragmentManager(), myInfo));
						mTabs.setViewPager(mViewPager);

						int atMeCount = myInfo.getNotices() == null ? 0 : myInfo.getNotices().size();
						int cmmCount = myInfo.getComments() == null ? 0 : myInfo.getComments().size();
						Utils.showShortToast(App.Instance, String.format(getString(R.string.msg_update_my_info),
								atMeCount, cmmCount));

						//Menu shows dynamically according to the count of list-items.
						mClearListV.setVisibility(atMeCount == 0 && cmmCount == 0 ? View.INVISIBLE : View.VISIBLE);
						mPopupMenu.getMenu().findItem(R.id.action_clear_at_me).setVisible(atMeCount > 0);
						mPopupMenu.getMenu().findItem(R.id.action_clear_comments).setVisible(cmmCount > 0);

						com.osc.tweet.utils.Utils.vibrationFeedback(App.Instance);
					} else {
						mClearListV.setVisibility(View.INVISIBLE);

						mViewPager.setAdapter(mAdp = new NoticesListsViewPagerAdapter(App.Instance,
								getChildFragmentManager(), null));
						mTabs.setViewPager(mViewPager);
					}
					mRootV.setVisibility(View.VISIBLE);
					objectAnimator.cancel();
					mRefreshV.setEnabled(true);
				} catch (IllegalStateException e) {
					//Activity has been destroyed
				}
			}
		});
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
		AsyncTaskCompat.executeParallel(new AsyncTask<NoticeType, Void, StatusResult>() {
			NoticeType mNoticeType;

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
					mClearPb.setVisibility(View.GONE);
					mClearListV.setVisibility(View.VISIBLE);

					OperatingEvent event = new OperatingEvent(res != null && res.getResult() != null && Integer.valueOf(
							res.getResult().getCode()) == com.osc4j.ds.common.Status.STATUS_OK);
					EventBus.getDefault().post(event);
					if (event.isSuccess()) {
						if (mAdp != null) {
							EventBus.getDefault().post(new ClearNoticeEvent(mNoticeType));
						}

						MenuItem atMi = mPopupMenu.getMenu().findItem(R.id.action_clear_at_me);
						MenuItem cmmMi =  mPopupMenu.getMenu().findItem(R.id.action_clear_comments);
						switch (mNoticeType) {
						case AtMe:
							atMi.setVisible(false);
							break;
						case Comments:
							cmmMi.setVisible(false);
							break;
						}
						if(!atMi.isVisible() && !cmmMi.isVisible()) {
							mClearListV.setVisibility(View.INVISIBLE);
						}
					}
				} catch (IllegalStateException e) {
					//Activity has been destroyed
				}
			}
		}, type);
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

}
