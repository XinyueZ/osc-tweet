package com.osc.tweet.app.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chopping.net.TaskHelper;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.ScreenSize;
import com.osc.tweet.R;
import com.osc.tweet.views.RoundedNetworkImageView;
import com.osc4j.OscApi;
import com.osc4j.OscTweetException;
import com.osc4j.ds.personal.Gender;
import com.osc4j.ds.personal.User;
import com.osc4j.ds.personal.UserInformation;

/**
 * Show user-information.
 *
 * @author Xinyue Zhao
 */
public final class UserInformationDialogFragment extends DialogFragment {
	private static final String EXTRAS_FRIEND = UserInformationDialogFragment.class.getName() + ".EXTRAS." + "friend";
	private static final String EXTRAS_NEED_MESSAGES =
			UserInformationDialogFragment.class.getName() + ".EXTRAS." + "need_message";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_dialog_user_information;

	private RoundedNetworkImageView mUserPhotoIv;
	private TextView mUserNameTv;
	private TextView mUserIdentTv;
	private TextView mUserGenderTv;
	private TextView mUserPlatformTv;
	private TextView mUserSkillTv;
	private TextView mUserLocationTv;
	private View mPb;
	private View mAllContainerV;

	/**
	 * Initialize an {@link  UserInformationDialogFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @param friend
	 * 		User-id of friend.
	 * @param needMessages
	 * 		{@code true} then show top messages.
	 *
	 * @return An instance of {@link UserInformationDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context, long friend, boolean needMessages) {
		Bundle args = new Bundle();
		args.putLong(EXTRAS_FRIEND, friend);
		args.putBoolean(EXTRAS_NEED_MESSAGES, needMessages);
		return (DialogFragment) Fragment.instantiate(context, UserInformationDialogFragment.class.getName(), args);
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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mUserPhotoIv = (RoundedNetworkImageView) view.findViewById(R.id.user_photo_iv);
		mUserNameTv = (TextView) view.findViewById(R.id.user_name_tv);
		mUserIdentTv = (TextView) view.findViewById(R.id.user_ident_tv);
		mUserGenderTv = (TextView) view.findViewById(R.id.user_gender_tv);
		mUserPlatformTv = (TextView) view.findViewById(R.id.user_platform_tv);
		mUserSkillTv = (TextView) view.findViewById(R.id.user_skill_tv);
		mUserLocationTv = (TextView) view.findViewById(R.id.user_location_tv);
		mPb = view.findViewById(R.id.pb);
		mAllContainerV = view.findViewById(R.id.all_container);
		getUserInformation();

		ScreenSize sz = DeviceUtils.getScreenSize(getActivity().getApplication());
		view.findViewById(R.id.root_v).setLayoutParams(new FrameLayout.LayoutParams(sz.Width,
				LayoutParams.WRAP_CONTENT));
	}


	/**
	 * Get user-information.
	 */
	private void getUserInformation() {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, UserInformation, UserInformation>() {
			@Override
			protected UserInformation doInBackground(Object... params) {
				try {
					long fri = getArguments().getLong(EXTRAS_FRIEND);
					boolean nmsg = getArguments().getBoolean(EXTRAS_NEED_MESSAGES);
					return OscApi.userInformation(getActivity().getApplicationContext(), fri, nmsg);
				} catch (IOException e) {
					return null;
				} catch (OscTweetException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(UserInformation userInformation) {
				super.onPostExecute(userInformation);
				if (userInformation != null && userInformation.getUser() != null) {
					User user = userInformation.getUser();
					mUserPhotoIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
					mUserPhotoIv.setImageUrl(user.getPortrait(), TaskHelper.getImageLoader());
					mUserNameTv.setText(user.getName());
					mUserIdentTv.setText(user.getIdent());
					mUserSkillTv.setText(user.getExpertise());
					mUserPlatformTv.setText(user.getPlatforms());
					mUserGenderTv.setText(getString(user.getGender() == Gender.Male ? R.string.lbl_user_gender_male :
							R.string.lbl_user_gender_famle));
					mUserLocationTv.setText(String.format("%s, %s", user.getCity(), user.getProvince()));
					mPb.setVisibility(View.INVISIBLE);
					mAllContainerV.setVisibility(View.VISIBLE);
				}
			}
		});
	}
}
