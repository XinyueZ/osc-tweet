package com.osc.tweet.app.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.chopping.net.TaskHelper;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.ScreenSize;
import com.chopping.utils.Utils;
import com.google.gson.JsonSyntaxException;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.UserInfoTweetListAdapter;
import com.osc.tweet.events.LoadFriendsListEvent;
import com.osc.tweet.events.OperatingEvent;
import com.osc.tweet.views.OnViewAnimatedClickedListener2;
import com.osc.tweet.views.RoundedNetworkImageView;
import com.osc4j.OscApi;
import com.osc4j.ds.common.StatusResult;
import com.osc4j.ds.personal.Gender;
import com.osc4j.ds.personal.People;
import com.osc4j.ds.personal.UserInformation;
import com.osc4j.exceptions.OscTweetException;

import de.greenrobot.event.EventBus;

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
	 * Save domain object for fallback when UI destroyed.
	 */
	private static final String USER_INFO = "user_info";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_dialog_user_information;

	private RoundedNetworkImageView mUserPhotoIv;
	private TextView mUserNameTv;
	private TextView mUserGenderTv;
	private TextView mUserPlatformTv;
	private TextView mUserSkillTv;
	private TextView mUserLocationTv;
	private Button mUserRelationBtn;
	private View mLoadUserInfoPb;
	private View mChangeRelationPb;
	private View mLoadTweetsPb;
	private View mAllContainerV;
	private UserInformation mUserInfo;
	/**
	 * Adapter for {@link #mRv} to show all tweets.
	 */
	private UserInfoTweetListAdapter mAdp;
	/**
	 * List container for showing all tweets.
	 */
	private RecyclerView mRv;
	/**
	 * Previous position of {@link #mUserPhotoIv}.
	 */
	private float mIvY;
	/**
	 * Previous position of {@link #mUserRelationBtn}.
	 */
	private float mBtnX;

	/**
	 * Network action in progress if {@code true}.
	 */
	private boolean mInProgress;

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
		mRv = (RecyclerView) view.findViewById(R.id.tweet_list_rv);
		mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new UserInfoTweetListAdapter(null));
		if (savedInstanceState != null) {
			mUserInfo = (UserInformation) savedInstanceState.getSerializable(USER_INFO);
		}
		mUserPhotoIv = (RoundedNetworkImageView) view.findViewById(R.id.user_photo_iv);
		mIvY = ViewHelper.getY(mUserPhotoIv);
		ViewHelper.setY(mUserPhotoIv, -100f);
		mUserNameTv = (TextView) view.findViewById(R.id.user_name_tv);
		mUserGenderTv = (TextView) view.findViewById(R.id.user_gender_tv);
		mUserPlatformTv = (TextView) view.findViewById(R.id.user_platform_tv);
		mUserRelationBtn = (Button) view.findViewById(R.id.relation_btn);
		mBtnX = ViewHelper.getX(mUserRelationBtn);
		ViewHelper.setTranslationX(mUserRelationBtn, 100f);
		mUserRelationBtn.setOnClickListener(new OnViewAnimatedClickedListener2() {
			@Override
			public void onClick() {
				AsyncTaskCompat.executeParallel(new AsyncTask<Object, StatusResult, StatusResult>() {
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						mChangeRelationPb.setVisibility(View.VISIBLE);
						mUserRelationBtn.setEnabled(false);
						mUserRelationBtn.setText("");
					}

					@Override
					protected StatusResult doInBackground(Object... params) {
						try {
							People people = mUserInfo.getPeople();
							return OscApi.updateRelation(App.Instance, people, people.isRelated());
						}  catch (IOException  | OscTweetException | JsonSyntaxException e) {
							return null;
						}
					}

					@Override
					protected void onPostExecute(StatusResult res) {
						super.onPostExecute(res);
						mChangeRelationPb.setVisibility(View.INVISIBLE);
						mUserRelationBtn.setEnabled(true);
						if (res != null && res.getResult() != null && Integer.valueOf(res.getResult().getCode()) ==
								com.osc4j.ds.common.Status.STATUS_OK) {
							People people = mUserInfo.getPeople();
							if (people.isRelated()) {
								Utils.showLongToast(App.Instance, String.format(getString(R.string.msg_follow_cancel),
										people.getName()));
							} else {
								Utils.showLongToast(App.Instance, String.format(getString(R.string.msg_follow),
										people.getName()));
							}
							EventBus.getDefault().post(new OperatingEvent(true));
							//New relation.
							people.setRelation(res.getResult().getRelation());
							EventBus.getDefault().post(new LoadFriendsListEvent());
						}
						updateFocusButton();
					}
				});
			}
		});
		mUserSkillTv = (TextView) view.findViewById(R.id.user_skill_tv);
		mUserLocationTv = (TextView) view.findViewById(R.id.user_location_tv);
		mLoadUserInfoPb = view.findViewById(R.id.load_user_info_pb);
		mChangeRelationPb = view.findViewById(R.id.change_relation_pb);
		mLoadTweetsPb = view.findViewById(R.id.loading_tweets_pb);
		mAllContainerV = view.findViewById(R.id.all_container);
		getUserInformation();

		ScreenSize sz = DeviceUtils.getScreenSize(getActivity().getApplication());
		view.findViewById(R.id.root_v).setLayoutParams(new FrameLayout.LayoutParams(sz.Width,
				LayoutParams.MATCH_PARENT));
	}


	/**
	 * Get user-information.
	 */
	private void getUserInformation() {
		if (!mInProgress) {
			AsyncTaskCompat.executeParallel(new AsyncTask<Object, UserInformation, UserInformation>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mInProgress = true;
				}

				@Override
				protected UserInformation doInBackground(Object... params) {
					try {
						long fri = getArguments().getLong(EXTRAS_FRIEND);
						boolean nmsg = getArguments().getBoolean(EXTRAS_NEED_MESSAGES);
						return OscApi.userInformation(App.Instance, fri, nmsg);
					} catch (IOException  | OscTweetException | JsonSyntaxException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(UserInformation userInformation) {
					super.onPostExecute(userInformation);
					try {
						if (userInformation != null && userInformation.getPeople() != null) {
							mUserInfo = userInformation;
							People people = userInformation.getPeople();
							mUserPhotoIv.setDefaultImageResId(R.drawable.ic_portrait_preview);
							mUserPhotoIv.setImageUrl(people.getPortrait(), TaskHelper.getImageLoader());
							ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mUserPhotoIv);
							animator.y(mIvY).setDuration(getResources().getInteger(R.integer.anim_fast_duration))
									.start();
							animator = ViewPropertyAnimator.animate(mUserRelationBtn);
							animator.translationX(mBtnX).setDuration(getResources().getInteger(
									R.integer.anim_fast_duration)).start();
							mUserNameTv.setText(people.getName());
							mUserSkillTv.setText(people.getExpertise());
							mUserPlatformTv.setText(people.getPlatforms());
							mUserGenderTv.setText(getString(
									people.getGender() == Gender.Male ? R.string.lbl_user_gender_male :
											R.string.lbl_user_gender_female));
							updateFocusButton();
							mUserLocationTv.setText(people.getFrom());
							mLoadUserInfoPb.setVisibility(View.INVISIBLE);
							mAllContainerV.setVisibility(View.VISIBLE);
							mAdp.setData(userInformation.getTweets());
							mAdp.notifyDataSetChanged();
							mLoadTweetsPb.setVisibility(View.INVISIBLE);
						}
					} catch (IllegalStateException e) {
						//Activity has been destroyed
					}
					mInProgress = false;
				}
			});
		}
	}

	/**
	 * Update text on button of relation.
	 */
	private void updateFocusButton() {
		mUserRelationBtn.setText(getString(
				mUserInfo.getPeople().isRelated() ? R.string.lbl_user_cancel_follow : R.string.lbl_user_follow));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(USER_INFO, mUserInfo);
	}
}
