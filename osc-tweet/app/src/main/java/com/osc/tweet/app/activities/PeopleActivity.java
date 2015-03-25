package com.osc.tweet.app.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chopping.bus.ReloadEvent;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.Utils;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.gson.JsonSyntaxException;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.PeopleListAdapter;
import com.osc.tweet.events.LoadEvent;
import com.osc.tweet.events.OperatingEvent;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc4j.OscApi;
import com.osc4j.ds.personal.Friend;
import com.osc4j.ds.personal.Friends;
import com.osc4j.ds.personal.FriendsList;
import com.osc4j.ds.personal.People;
import com.osc4j.ds.personal.PeopleList;
import com.osc4j.exceptions.OscTweetException;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Show list of all no-relation people.
 *
 * @author Xinyue Zhao
 */
public final class PeopleActivity extends OscActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_people;
	/**
	 * List container for showing all tweets.
	 */
	private RecyclerView mRv;
	/**
	 * Progress indicator.
	 */
	private SmoothProgressBar mSmoothProgressBar;
	/**
	 * Pull to load.
	 */
	private SwipeRefreshLayout mSwipeRefreshLayout;
	/**
	 * Some fun indicator when data not loaded.
	 */
	private View mNotLoadedIndicatorV;
	/**
	 * Indicator for empty feeds.
	 */
	private View mEmptyV;
	/**
	 * Adapter for {@link #mRv} to show all tweets.
	 */
	private PeopleListAdapter mAdp;
	/**
	 * Network action in progress if {@code true}.
	 */
	private boolean mInProgress;
	/**
	 * A bar bottom.
	 */
	private SnackBar mSnackBar;
	private LinearLayoutManager mLayoutManager;
	/**
	 * Click to find more people.
	 */
	private View mFindMoreLoadV;
	/**
	 * All my friends.
	 */
	private List<Friend> mAllFriends;

	/**
	 * Show single instance of {@link}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, PeopleActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.osc.tweet.events.LoadEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.LoadEvent}.
	 */
	public void onEvent(LoadEvent e) {
		getPeople();
	}


	/**
	 * Handler for {@link com.chopping.bus.ReloadEvent}.
	 *
	 * @param e
	 * 		Event {@link com.chopping.bus.ReloadEvent}.
	 */
	public void onEvent(ReloadEvent e) {
		getPeople();
	}


	//------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		mSnackBar = new SnackBar(this);

		//Actionbar and navi-drawer.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.lbl_settings);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDefaultDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.lbl_might_known_people);

		mEmptyV = findViewById(R.id.empty_ll);
		mNotLoadedIndicatorV = findViewById(R.id.not_loaded_ll);
		mSmoothProgressBar = (SmoothProgressBar) findViewById(R.id.loading_pb);

		mRv = (RecyclerView) findViewById(R.id.people_list_rv);
		mRv.setLayoutManager(mLayoutManager = new LinearLayoutManager(this));
		mRv.setHasFixedSize(false);
		mRv.setAdapter(mAdp = new PeopleListAdapter(null));

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.color_pocket_1, R.color.color_pocket_2,
				R.color.color_pocket_3, R.color.color_pocket_4);
		mSwipeRefreshLayout.setRefreshing(true);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mFindMoreLoadV.setVisibility(View.INVISIBLE);
				getFriendsList();
			}
		});

		mFindMoreLoadV = findViewById(R.id.load_more_btn);
		mFindMoreLoadV.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				if (mAdp != null && mAdp.getItemCount() > 0) {
					mSnackBar.show(getString(R.string.msg_load_more));
					mFindMoreLoadV.setVisibility(View.INVISIBLE);
				}
				getFriendsList();
			}
		});

		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.loading_more_pb), "rotation", 0, 360f);
		objectAnimator.setDuration(getResources().getInteger(R.integer.anim_duration));
		objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
		objectAnimator.start();

		showSearch();
		//Should get all my friends first, in order to filter out duplicated people.
		getFriendsList();

	}

	/**
	 * Load feeds
	 */
	private void getPeople() {
		if (!mInProgress) {
			AsyncTaskCompat.executeParallel(new AsyncTask<Object, PeopleList, PeopleList>() {
												@Override
												protected void onPreExecute() {
													super.onPreExecute();
													mInProgress = true;
												}

												@Override
												protected PeopleList doInBackground(Object... params) {
													try {
														int x = Utils.randInt(0, 100);
														int y = Utils.randInt(0, 100);
														return OscApi.noRelationPeople(App.Instance, x, y);
													} catch (IOException | OscTweetException | JsonSyntaxException e) {
														return null;
													}
												}

												@Override
												protected void onPostExecute(PeopleList peopleList) {
													super.onPostExecute(peopleList);
													try {
														if (peopleList != null && peopleList.getStatus() ==
																com.osc4j.ds.common.Status.STATUS_OK) {
															if (peopleList.getPeople() != null &&
																	peopleList.getPeople().size() > 0) {

																//Filter out duplicated people.
																List<People> people = new ArrayList<>();

																List<People> peopleLoaded = peopleList.getPeople();
																boolean found;
																for (People p : peopleLoaded) {
																	found = false;
																	for (Friend f : mAllFriends) {
																		if (f.getUserId() == p.getId()) {
																			found = true;
																			break;
																		}
																	}
																	if (!found) {
																		people.add(p);
																	}
																}

																if (people.size() == 0) {
																	if (mAdp.getItemCount() == 0) {
																		//We haven't data previously, then show empty information.
																		mEmptyV.setVisibility(View.VISIBLE);
																		mNotLoadedIndicatorV.setVisibility(View.GONE);
																	}//else we have information before. Stay here to show old ones.
																} else {
																	mAdp.setData(people);
																	mEmptyV.setVisibility(View.GONE);
																	mNotLoadedIndicatorV.setVisibility(View.GONE);
																}

																EventBus.getDefault().post(new OperatingEvent(true));
															} else {
																mEmptyV.setVisibility(View.VISIBLE);
																mNotLoadedIndicatorV.setVisibility(View.GONE);
															}


															finishLoading();
														} else {
															EventBus.getDefault().post(new OperatingEvent(false));
															mEmptyV.setVisibility(View.GONE);
															if (mAdp.getItemCount() == 0) {
																mNotLoadedIndicatorV.setVisibility(View.VISIBLE);
															}
															mSmoothProgressBar.setVisibility(View.INVISIBLE);

															showDialogFragment(new DialogFragment() {
																@Override
																public Dialog onCreateDialog(
																		Bundle savedInstanceState) {
																	// Use the Builder class for convenient dialog construction
																	AlertDialog.Builder builder =
																			new AlertDialog.Builder(
																					PeopleActivity.this);
																	builder.setMessage(R.string.btn_retry)
																			.setPositiveButton(R.string.btn_ok,
																					new DialogInterface.OnClickListener() {
																						public void onClick(
																								DialogInterface dialog,
																								int id) {
																							mSmoothProgressBar
																									.setVisibility(
																											View.VISIBLE);
																							getPeople();
																							dialog.dismiss();
																						}
																					});
																	return builder.create();
																}
															}, null);
														}
													} catch (IllegalStateException e) {
														//Activity has been destroyed
													}
													mInProgress = false;
												}
											}

			);
		} else {
			mFindMoreLoadV.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}


	/**
	 * Calls when loading data has been done.
	 */
	private void finishLoading() {
		mAdp.notifyDataSetChanged();
		mSmoothProgressBar.setVisibility(View.INVISIBLE);
		mSwipeRefreshLayout.setRefreshing(false);
		mFindMoreLoadV.setVisibility(View.VISIBLE);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_people, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ActivityCompat.finishAfterTransition(this);
			break;
		case R.id.action_top:
			if (mAdp != null && mAdp.getItemCount() > 0) {
				mLayoutManager.scrollToPositionWithOffset(0, 0);
				Utils.showShortToast(App.Instance, R.string.msg_move_to_top);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Get tweets data and showing on list.
	 */
	private void getFriendsList() {
		if (!mInProgress) {
			AsyncTaskCompat.executeParallel(new AsyncTask<Object, FriendsList, FriendsList>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mSmoothProgressBar.setVisibility(View.VISIBLE);
					mInProgress = true;
				}

				@Override
				protected FriendsList doInBackground(Object... params) {
					try {
						return OscApi.friendsList(App.Instance);
					} catch (IOException | OscTweetException | JsonSyntaxException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(FriendsList friendsList) {
					super.onPostExecute(friendsList);
					try {
						if (friendsList != null && friendsList.getFriends() != null) {
							Friends friends = friendsList.getFriends();
							mAllFriends = new ArrayList<>();
							List<Friend> fansList = friends.getFansList();
							if (fansList != null) {
								mAllFriends.addAll(fansList);
							}

							List<Friend> focusList = friends.getFocusList();
							if (focusList != null) {
								mAllFriends.addAll(focusList);
							}


						}
					} catch (IllegalStateException e) {
						//Activity has been destroyed
					}
					mInProgress = false;

					if (mAllFriends.size() > 0) {
						getPeople();
					}
				}
			});
		} else {
			mFindMoreLoadV.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	/**
	 * Animation to show search button.
	 */
	private void showSearch() {
		Resources res = getResources();
		View v = findViewById(R.id.buttons_fl);
		int screenWidth = DeviceUtils.getScreenSize(getApplication()).Width;
		ViewHelper.setX(v, -res.getDimensionPixelSize(R.dimen.float_button_anim_qua));
		ViewHelper.setRotation(v, -360f * 4);
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(v);
		animator.x(screenWidth - res.getDimensionPixelSize(R.dimen.float_button_anim_qua)).rotation(0).setDuration(
				getResources().getInteger(R.integer.anim_duration)).start();

	}
}
