package com.osc.tweet.app.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chopping.application.BasicPrefs;
import com.chopping.bus.CloseDrawerEvent;
import com.chopping.fragments.BaseFragment;
import com.chopping.net.TaskHelper;
import com.chopping.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.activities.SettingActivity;
import com.osc.tweet.app.activities.WebViewActivity;
import com.osc.tweet.events.ClearNoticeEvent;
import com.osc.tweet.events.GetMyInformationEvent;
import com.osc.tweet.events.OpenMyNoticesDrawerEvent;
import com.osc.tweet.events.OpenedDrawerEvent;
import com.osc.tweet.events.OperatingEvent;
import com.osc.tweet.events.RefreshMyInfoEvent;
import com.osc.tweet.utils.Prefs;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc.tweet.views.RoundedNetworkImageView;
import com.osc4j.OscApi;
import com.osc4j.ds.personal.Am;
import com.osc4j.ds.personal.MyInformation;
import com.osc4j.exceptions.OscTweetException;

import de.greenrobot.event.EventBus;

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
	 * My photo.
	 */
	private RoundedNetworkImageView mUserPhotoIv;
	/**
	 * My name.
	 */
	private TextView mUserNameTv;
	/**
	 * My personal information .
	 */
	private MyInformation myInfo;
	/**
	 * Click to refresh my-info.
	 */
	private View mRefreshV;

	/**
	 * Give count of all notices.
	 */
	private TextView mNoticesTv;
	/**
	 * Count of "@me" notices.
	 */
	private int mAtCount;
	/**
	 * Count of notices of new comments.
	 */
	private int mCommentsCount;
	/**
	 * Count of fans.
	 */
	private int mFansCount;
	/**
	 * Count of followed.
	 */
	private int mFollowedCount;
	/**
	 * The view that contains hello, count of notices.
	 */
	private View myNoticesLl;
	/**
	 * The view that contains hometown, friends.
	 */
	private View myHomeFriendsLl;
	/**
	 * Show count of friends.
	 */
	private TextView mFriendsTv;
	/**
	 * Show hometown
	 */
	private TextView mHomeTv;
	/**
	 * To open my info as API user in browser.
	 */
	private View meV;
	/**
	 * Container for user-photo and button to edit user.
	 */
	private View mUserPhotoFl;
	/**
	 * To edit me online in browser.
	 */
	private View mEditMeV;

	/*Init scale of photo.*/
	private float mPhotoScX;
	private float mPhotoScY;
	/**
	 * Network action in progress if {@code true}.
	 */
	private boolean mInProgress;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link RefreshMyInfoEvent}.
	 *
	 * @param e
	 * 		Event {@link RefreshMyInfoEvent}.
	 */
	public void onEvent(RefreshMyInfoEvent e) {
		getMyInformation(true);
	}

	/**
	 * Handler for {@link ClearNoticeEvent}.
	 *
	 * @param e
	 * 		Event {@link ClearNoticeEvent}.
	 */
	public void onEvent(ClearNoticeEvent e) {
		switch (e.getType()) {
		case AtMe:
			mAtCount = 0;
			break;
		case Comments:
			mCommentsCount = 0;
			break;
		}
		mNoticesTv.setText(String.format(getString(R.string.msg_update_my_info), mAtCount, mCommentsCount));
	}

	/**
	 * Handler for {@link com.osc.tweet.events.OpenedDrawerEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.OpenedDrawerEvent}.
	 */
	public void onEvent(OpenedDrawerEvent e) {
		transition(e);
	}


	//------------------------------------------------

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

		mFriendsTv = (TextView) view.findViewById(R.id.friends_count_tv);
		mHomeTv = (TextView) view.findViewById(R.id.hometown_tv);

		myNoticesLl = view.findViewById(R.id.my_notices_ll);
		myHomeFriendsLl = view.findViewById(R.id.my_hometown_friends_ll);
		ViewHelper.setAlpha(myHomeFriendsLl, 0f);

		meV = view.findViewById(R.id.me_btn);
		ViewHelper.setAlpha(meV, 0f);
		meV.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				WebViewActivity.showInstance(getActivity(),  myInfo.getUrl().getHome(), getString(R.string.lbl_my_profile));
				EventBus.getDefault().post(new CloseDrawerEvent());
			}
		});

		mUserPhotoFl = view.findViewById(R.id.user_photo_fl);
		mUserPhotoIv = (RoundedNetworkImageView) view.findViewById(R.id.user_photo_iv);
		mPhotoScX = ViewHelper.getScaleX(mUserPhotoFl);
		mPhotoScY = ViewHelper.getScaleY(mUserPhotoFl);
		ViewHelper.setScaleX(mUserPhotoFl, 0f);
		ViewHelper.setScaleY(mUserPhotoFl, 0f);

		mEditMeV = view.findViewById(R.id.edit_me_btn);
		ViewHelper.setAlpha(mEditMeV, 0f);
		mEditMeV.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				WebViewActivity.showInstance(getActivity(), myInfo.getUrl().getEdit(), getString(
						R.string.lbl_edit_my_profile));
				EventBus.getDefault().post(new CloseDrawerEvent());
			}
		});

		mUserNameTv = (TextView) view.findViewById(R.id.user_name_tv);
		mRefreshV = view.findViewById(R.id.refresh_btn);
		getMyInformation(true);
		mRefreshV.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				getMyInformation(false);
			}
		});

		view.findViewById(R.id.settings_btn).setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				SettingActivity.showInstance(getActivity());
				EventBus.getDefault().post(new CloseDrawerEvent());
			}
		});

		mNoticesTv = (TextView) view.findViewById(R.id.notices_count_tv);
		String count = String.format(getString(R.string.msg_update_my_info), mAtCount, mCommentsCount);
		mNoticesTv.setText(count);

		String friends = String.format(getString(R.string.lbl_friends_count), mFansCount, mFollowedCount);
		mFriendsTv.setText(friends);

	}


	/**
	 * Get my personal information.
	 *
	 * @param feedback
	 * 		Show some feedback when loaded if {@code true}.
	 */
	private void getMyInformation(final boolean feedback) {
		if (!mInProgress) {
			AsyncTaskCompat.executeParallel(new AsyncTask<Object, MyInformation, MyInformation>() {
				ObjectAnimator objectAnimator;


				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mInProgress = true;
					mUserPhotoIv.setDefaultImageResId(R.drawable.ic_portrait_preview);

					mRefreshV.setEnabled(false);
					objectAnimator = ObjectAnimator.ofFloat(mRefreshV, "rotation", 0, 360f);
					objectAnimator.setDuration(800);
					objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
					objectAnimator.start();
				}

				@Override
				protected MyInformation doInBackground(Object... params) {
					try {
						return OscApi.myInformation(App.Instance, Prefs.getInstance().settingShowMeInNoticesList());
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
							mUserPhotoIv.setImageUrl(am.getPortrait(), TaskHelper.getImageLoader());
							mUserNameTv.setText(am.getName());
							mAtCount = myInfo.getNotices() == null ? 0 : myInfo.getNotices().size();
							mCommentsCount = myInfo.getComments() == null ? 0 : myInfo.getComments().size();

							String count = String.format(getString(R.string.msg_update_my_info), mAtCount,
									mCommentsCount);
							if (feedback) {
								Utils.showShortToast(App.Instance, count);
								com.osc.tweet.utils.Utils.vibrationFeedback(App.Instance);
							}
							mNoticesTv.setText(count);

							if (mAtCount != 0 || mCommentsCount != 0) {
								EventBus.getDefault().post(new OpenMyNoticesDrawerEvent());
							}

							mFansCount = am.getFansCount();
							mFollowedCount = am.getFollowersCount();
							mHomeTv.setText(am.getCity() + "," + am.getProvince());
							//						doAnimation();
							//						Prefs.getInstance().setShowMyInfoAnim(false);
							mEditMeV.setVisibility(View.VISIBLE);
							meV.setVisibility(View.VISIBLE);
						} else {
							if (MyInfoFragment.this.myInfo == null) {
								meV.setVisibility(View.INVISIBLE);
								mEditMeV.setVisibility(View.INVISIBLE);
							}
							EventBus.getDefault().post(new OperatingEvent(false));
						}

						EventBus.getDefault().post(new GetMyInformationEvent(myInfo));
						objectAnimator.cancel();
						mRefreshV.setEnabled(true);

						String friends = String.format(getString(R.string.lbl_friends_count), mFansCount,
								mFollowedCount);
						mFriendsTv.setText(friends);

						String count = String.format(getString(R.string.msg_update_my_info), mAtCount, mCommentsCount);
						mNoticesTv.setText(count);
					} catch (IllegalStateException e) {
						//Activity has been destroyed
					}
					mInProgress = false;
				}
			});
		}
	}

	/**
	 * Make some animation when first time opening of this view.
	 *
	 * @param e
	 * 		The view is seen when {@link OpenedDrawerEvent} comes.
	 */
	private void transition(OpenedDrawerEvent e) {
		if ( e.getGravity() == Gravity.LEFT) {
			doAnimation();
			if (MyInfoFragment.this.myInfo == null) {
				getMyInformation(false);
			}
		}
	}


	/**
	 * Animation for the photo.
	 */
	private void doAnimation() {
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mUserPhotoFl);
		animator.x(0f).scaleX(mPhotoScX).scaleY(mPhotoScY).setDuration(getResources().getInteger(
				R.integer.anim_super_fast_duration)).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				float x = ViewHelper.getX(mUserPhotoFl) + (mUserPhotoFl.getWidth() * 1.5f);
				float y = ViewHelper.getY(mUserPhotoFl);
				ViewPropertyAnimator animator2 = ViewPropertyAnimator.animate(myNoticesLl);
				animator2.x(x).y(y).setDuration(getResources().getInteger(R.integer.anim_super_fast_duration))
						.setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								super.onAnimationEnd(animation);
								ViewPropertyAnimator animator3 = ViewPropertyAnimator.animate(myHomeFriendsLl);
								animator3.alpha(1).setDuration(getResources().getInteger(R.integer.anim_fast_duration))
										.start();
								ViewPropertyAnimator animator4 = ViewPropertyAnimator.animate(meV);
								animator4.alpha(1).setDuration(getResources().getInteger(R.integer.anim_slow_duration))
										.start();
								ViewPropertyAnimator animator5 = ViewPropertyAnimator.animate(mEditMeV);
								animator5.alpha(1).setDuration(getResources().getInteger(R.integer.anim_slow_duration))
										.start();
							}
						}).start();

			}
		}).start();
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

}
