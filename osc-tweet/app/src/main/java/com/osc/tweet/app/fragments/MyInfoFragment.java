package com.osc.tweet.app.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.chopping.net.TaskHelper;
import com.chopping.utils.Utils;
import com.nineoldandroids.animation.ObjectAnimator;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.activities.SettingActivity;
import com.osc.tweet.events.GetMyInformationEvent;
import com.osc.tweet.events.OpenMyNoticesDrawerEvent;
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
	 * Root of all views of this {@link Fragment}.
	 */
	private View mRootV;

	/**
	 * Give count of all notices.
	 */
 	private TextView mNoticesTv;

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
			}
		});

		mNoticesTv = (TextView) view.findViewById(R.id.notices_count_tv);
		String count = String.format(getString(R.string.msg_update_my_info),
				0, 0);
		mNoticesTv.setText(count);
		mNoticesTv.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new OpenMyNoticesDrawerEvent());
			}
		});
	}


	/**
	 * Get my personal information.
	 *
	 * @param feedback
	 * 		Show some feedback when loaded if {@code true}.
	 */
	private void getMyInformation(final boolean feedback) {
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
						int atMeCount = myInfo.getNotices() == null ? 0 : myInfo.getNotices().size();
						int cmmCount = myInfo.getComments() == null ? 0 : myInfo.getComments().size();

						String count = String.format(getString(R.string.msg_update_my_info),
								atMeCount, cmmCount);
						if (feedback) {
							Utils.showShortToast(App.Instance, count);
							com.osc.tweet.utils.Utils.vibrationFeedback(App.Instance);
						}
						mNoticesTv.setText(count);
					} else {
						String count = String.format(getString(R.string.msg_update_my_info),
								0, 0);
						mNoticesTv.setText(count);
					}
					EventBus.getDefault().post(new GetMyInformationEvent(myInfo));
					mRootV.setVisibility(View.VISIBLE);
					objectAnimator.cancel();
					mRefreshV.setEnabled(true);
				} catch (IllegalStateException e) {
					//Activity has been destroyed
				}
			}
		});
	}


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

}
