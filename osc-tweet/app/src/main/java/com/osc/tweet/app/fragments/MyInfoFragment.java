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
import com.nineoldandroids.animation.ObjectAnimator;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.utils.Prefs;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc.tweet.views.RoundedNetworkImageView;
import com.osc4j.OscApi;
import com.osc4j.ds.personal.Actives;
import com.osc4j.ds.personal.Am;
import com.osc4j.ds.personal.MyInformation;
import com.osc4j.exceptions.OscTweetException;

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
	 * My information personal.
	 */
	private MyInformation mMyInfo;
	/**
	 * Click to refresh my-info.
	 */
	private View mRefreshV;
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
	}


	/**
	 * Get my personal information.
	 */
	private void getMyInformation() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, MyInformation, MyInformation>() {
			ObjectAnimator objectAnimator;


			@Override
			protected void onPreExecute() {
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
				if (myInfo != null && myInfo.getAm() != null) {
					mMyInfo = myInfo;
					Am am = mMyInfo.getAm();
					mUserPhotoIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
					mUserPhotoIv.setImageUrl(am.getPortrait(), TaskHelper.getImageLoader());
					mUserNameTv.setText(am.getName());

					if (myInfo.getActives() != null) {
						Actives actives = new Actives(com.osc4j.ds.common.Status.STATUS_OK, myInfo.getActives());
						getChildFragmentManager().beginTransaction().replace(R.id.my_last_actives_fl,
								ActivesListFragment.newInstance(App.Instance, actives)).commit();
					}
				}

				objectAnimator.cancel();
			}
		});
	}


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
