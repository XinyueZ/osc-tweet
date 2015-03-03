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
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.utils.Prefs;
import com.osc.tweet.views.RoundedNetworkImageView;
import com.osc4j.OscApi;
import com.osc4j.exceptions.OscTweetException;
import com.osc4j.ds.personal.Am;
import com.osc4j.ds.personal.MyInformation;

/**
 * Show my information.
 *
 * @author Xinyue Zhao
 */
public final class MyInfoFragment extends BaseFragment {
	private static final String EXTRAS_MY_INFO = MyInfoFragment.class.getName() + ".EXTRAS.MyInfo";
	/**
	 * Save domain object for fallback when UI destroyed.
	 */
	private static final String MY_INFO = "my_info";
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
	 * My nickname.
	 */
	private TextView mUserIdentTv;
	/**
	 * My information personal.
	 */
	private MyInformation mMyInfo;
	/**
	 * Initialize an {@link  MyInfoFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link MyInfoFragment}.
	 */
	public static  MyInfoFragment newInstance(Context context) {
		Bundle args = new Bundle();
		return (MyInfoFragment) Fragment.instantiate(context, MyInfoFragment.class.getName(), args);
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
		mUserIdentTv = (TextView) view.findViewById(R.id.user_ident_tv);

		getMyInformation();
	}


	/**
	 * Get my personal information.
	 */
	private void getMyInformation() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, MyInformation, MyInformation>() {
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
					mMyInfo  = myInfo;
					Am am = mMyInfo.getAm();
					mUserPhotoIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
					mUserPhotoIv.setImageUrl(am.getPortrait(), TaskHelper.getImageLoader());
					mUserNameTv.setText(am.getName());
					mUserIdentTv.setText(am.getIdent());
				}
			}
		});
	}





	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
