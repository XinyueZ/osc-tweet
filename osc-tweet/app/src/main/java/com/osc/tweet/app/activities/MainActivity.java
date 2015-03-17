package com.osc.tweet.app.activities;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.chopping.bus.CloseDrawerEvent;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.Utils;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.adapters.MainViewPagerAdapter;
import com.osc.tweet.app.fragments.AboutDialogFragment;
import com.osc.tweet.app.fragments.AboutDialogFragment.EulaConfirmationDialog;
import com.osc.tweet.app.fragments.AppListImpFragment;
import com.osc.tweet.app.fragments.EditorDialogFragment;
import com.osc.tweet.app.fragments.FriendsListFragment;
import com.osc.tweet.app.fragments.MyInfoFragment;
import com.osc.tweet.app.fragments.TweetCommentListDialogFragment;
import com.osc.tweet.app.fragments.UserInformationDialogFragment;
import com.osc.tweet.events.CloseFriendsListEvent;
import com.osc.tweet.events.CommentTweetEvent;
import com.osc.tweet.events.EULAConfirmedEvent;
import com.osc.tweet.events.EULARejectEvent;
import com.osc.tweet.events.OpenMyNoticesDrawerEvent;
import com.osc.tweet.events.OpenedDrawerEvent;
import com.osc.tweet.events.OperatingEvent;
import com.osc.tweet.events.ShowBigImageEvent;
import com.osc.tweet.events.ShowEditorEvent;
import com.osc.tweet.events.ShowTweetCommentsListEvent;
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.events.ShowingLoadingEvent;
import com.osc.tweet.events.SnackMessageEvent;
import com.osc.tweet.utils.Prefs;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc4j.Consts;
import com.osc4j.LoginDialog;
import com.osc4j.OscApi;
import com.osc4j.ds.favorite.TweetFavoritesList;
import com.osc4j.exceptions.OscTweetException;
import com.osc4j.utils.AuthUtil;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class MainActivity extends BaseActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * Navigation drawer.
	 */
	private DrawerLayout mDrawerLayout;
	/**
	 * Use navigation-drawer for this fork.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	/**
	 * Support actionbar.
	 */
	private Toolbar mToolbar;
	/**
	 * Progress indicator.
	 */
	private SmoothProgressBar mSmoothProgressBar;

	/**
	 * Tabs.
	 */
	private PagerSlidingTabStrip mTabs;

	/**
	 * The pagers
	 */
	private ViewPager mViewPager;
	/**
	 * Adapter for {@link #mViewPager}.
	 */
	private MainViewPagerAdapter mPagerAdapter;

	/**
	 * Broadcast-manager.
	 */
	private LocalBroadcastManager mLocalBroadcastManager;
	/**
	 * Broadcast-receiver for event after authentication is done.
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			initViewPager();

		}
	};
	/**
	 * Filter for {@link #mBroadcastReceiver}.
	 */
	private IntentFilter mIntentFilter = new IntentFilter(Consts.ACTION_AUTH_DONE);
	/**
	 * View to open friends-list.
	 */
	private View mOpenFriendsListV;
	/**
	 * The view clicked to open editing.
	 */
	private View mEditBtn;
	/**
	 * A bar bottom.
	 */
	private SnackBar mSnackBar;
	/**
	 * Indicator when loading application config.
	 */
	private ProgressDialog mPbDlg;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.osc.tweet.events.OpenMyNoticesDrawerEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.OpenMyNoticesDrawerEvent}.
	 */
	public void onEvent(OpenMyNoticesDrawerEvent e) {
		if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT) && !mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
			mDrawerLayout.closeDrawer(Gravity.LEFT);
			mDrawerLayout.openDrawer(Gravity.RIGHT);
		}
	}

	/**
	 * Handler for {@link  EULARejectEvent}.
	 *
	 * @param e
	 * 		Event {@link  EULARejectEvent}.
	 */
	public void onEvent(EULARejectEvent e) {
		finish();
	}

	/**
	 * Handler for {@link EULAConfirmedEvent}
	 *
	 * @param e
	 * 		Event {@link  EULAConfirmedEvent}.
	 */
	public void onEvent(EULAConfirmedEvent e) {

		initViewPager();

	}


	/**
	 * Handler for {@link com.osc.tweet.events.ShowingLoadingEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.ShowingLoadingEvent}.
	 */
	public void onEvent(ShowingLoadingEvent e) {
		mSmoothProgressBar.setVisibility(e.isShow() ? View.VISIBLE : View.GONE);
	}

	/**
	 * Handler for {@link com.chopping.bus.CloseDrawerEvent}.
	 *
	 * @param e
	 * 		Event {@link com.chopping.bus.CloseDrawerEvent}.
	 */
	public void onEvent(CloseDrawerEvent e) {
		mDrawerLayout.closeDrawers();
	}

	/**
	 * Handler for {@link com.osc.tweet.events.ShowBigImageEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.ShowBigImageEvent}.
	 */
	public void onEvent(ShowBigImageEvent e) {
		PhotoViewActivity.showInstance(this, e.getTweetListItem());
	}


	/**
	 * Handler for {@link com.osc.tweet.events.CloseFriendsListEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.CloseFriendsListEvent}.
	 */
	public void onEvent(CloseFriendsListEvent e) {
		showFriendsListButton();
	}

	/**
	 * Handler for {@link com.osc.tweet.events.ShowUserInformationEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.ShowUserInformationEvent}.
	 */
	public void onEvent(ShowUserInformationEvent e) {
		showDialogFragment(UserInformationDialogFragment.newInstance(getApplicationContext(), e.getUserId(), true),
				"userInfo");
	}


	/**
	 * Handler for {@link com.osc.tweet.events.SnackMessageEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.SnackMessageEvent}.
	 */
	public void onEvent(SnackMessageEvent e) {
		mSnackBar.show(e.getMessage());
	}

	/**
	 * Handler for {@link com.osc.tweet.events.ShowEditorEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.ShowEditorEvent}.
	 */
	public void onEvent(ShowEditorEvent e) {
		showDialogFragment(EditorDialogFragment.newInstance(getApplicationContext(), e.getDefaultMessage(),
				e.isDefaultFixed()), "editor");
	}


	/**
	 * Handler for {@link com.osc.tweet.events.CommentTweetEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.CommentTweetEvent}.
	 */
	public void onEvent(CommentTweetEvent e) {
		if (e.getComment() != null) {
			showDialogFragment(EditorDialogFragment.newInstance(getApplicationContext(), e.getTweetListItem(),
					e.getComment()), "comment-editor");
		} else if (e.getNotice() != null) {
			showDialogFragment(EditorDialogFragment.newInstance(getApplicationContext(), e.getTweetListItem(),
					e.getNotice()), "comment-editor");
		} else {
			showDialogFragment(EditorDialogFragment.newInstance(getApplicationContext(), e.getTweetListItem()),
					"comment-editor");
		}
	}


	/**
	 * Handler for {@link ShowTweetCommentsListEvent}.
	 *
	 * @param e
	 * 		Event {@link ShowTweetCommentsListEvent}.
	 */
	public void onEvent(final ShowTweetCommentsListEvent e) {
		showDialogFragment(TweetCommentListDialogFragment.newInstance(getApplicationContext(), e.getTweetItem()),
				"comment-list");
	}

	/**
	 * Handler for {@link OperatingEvent}.
	 *
	 * @param e
	 * 		Event {@link OperatingEvent}.
	 */
	public void onEvent(OperatingEvent e) {
		Utils.showLongToast(getApplicationContext(), e.isSuccess() ? getString(R.string.msg_operating_successfully) :
				getString(R.string.msg_operating_failed));
		com.osc.tweet.utils.Utils.vibrationFeedback(App.Instance);
	}
	//------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		mSnackBar = new SnackBar(this);

		//Actionbar and navi-drawer.
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		initDrawer();

		//Button to open editing message.
		mEditBtn = findViewById(R.id.edit_btn);
		mEditBtn.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new ShowEditorEvent(null));
			}
		});

		//No-pulling indicator  of loading feeds.
		mSmoothProgressBar = (SmoothProgressBar) findViewById(R.id.loading_pb);


		//Handler when login would have been done.
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplication());
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

		//Friends-list button, it opens friends-list then disappeared.
		mOpenFriendsListV = findViewById(R.id.friends_btn);
		mOpenFriendsListV.setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_right,
						R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(
						R.id.friends_list_container, FriendsListFragment.newInstance(MainActivity.this),
						FriendsListFragment.class.getSimpleName()).addToBackStack(null).commit();

				dismissFriendsListButton();
			}
		});
		ViewHelper.setX(mOpenFriendsListV, 99999);
		showFriendsListButton();

		mPbDlg = ProgressDialog.show(this, null, getString(R.string.msg_load_config));
		mPbDlg.setCancelable(false);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		showFriendsListButton();
	}

	@Override
	protected void onDestroy() {
		mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	private void showFriendsListButton() {
		int screenWidth = DeviceUtils.getScreenSize(getApplication()).Width;
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mOpenFriendsListV);
		animator.x(screenWidth - screenWidth / 10).setDuration(getResources().getInteger(R.integer.anim_slow_duration))
				.start();
	}

	private void dismissFriendsListButton() {
		int screenWidth = DeviceUtils.getScreenSize(getApplication()).Width;
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mOpenFriendsListV);
		animator.x(screenWidth).setDuration(getResources().getInteger(R.integer.anim_slow_duration)).start();
	}

	/**
	 * To confirm whether the validation of the Play-service of Google Inc.
	 */
	private void checkPlayService() {
		final int isFound = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isFound == ConnectionResult.SUCCESS) {//Ignore update.
			//The "End User License Agreement" must be confirmed before you use this application.
			if (!Prefs.getInstance().isEULAOnceConfirmed()) {
				showDialogFragment(new EulaConfirmationDialog(), null);
			}
		} else {
			new Builder(this).setTitle(R.string.application_name).setMessage(R.string.lbl_play_service).setCancelable(
					false).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(getString(R.string.play_service_url)));
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e0) {
						intent.setData(Uri.parse(getString(R.string.play_service_web)));
						try {
							startActivity(intent);
						} catch (Exception e1) {
							//Ignore now.
						}
					} finally {
						finish();
					}
				}
			}).create().show();
		}
	}

	/**
	 * Show  {@link android.support.v4.app.DialogFragment}.
	 *
	 * @param dlgFrg
	 * 		An instance of {@link android.support.v4.app.DialogFragment}.
	 * @param tagName
	 * 		Tag name for dialog, default is "dlg". To grantee that only one instance of {@link
	 * 		android.support.v4.app.DialogFragment} can been seen.
	 */
	protected void showDialogFragment(DialogFragment dlgFrg, String tagName) {
		try {
			if (dlgFrg != null) {
				DialogFragment dialogFragment = dlgFrg;
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = getSupportFragmentManager().findFragmentByTag("dlg");
				if (prev != null) {
					ft.remove(prev);
				}
				try {
					if (TextUtils.isEmpty(tagName)) {
						dialogFragment.show(ft, "dlg");
					} else {
						dialogFragment.show(ft, tagName);
					}
				} catch (Exception _e) {
				}
			}
		} catch (Exception _e) {
		}
	}


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_about:
			showDialogFragment(AboutDialogFragment.newInstance(this), null);
			break;
		case R.id.action_settings:
			SettingActivity.showInstance(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuShare = menu.findItem(R.id.action_share_app);
		//Getting the actionprovider associated with the menu item whose id is share.
		android.support.v7.widget.ShareActionProvider provider =
				(android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);
		//Setting a share intent.
		String subject = getString(R.string.lbl_share_app_title, getString(R.string.application_name));
		String text = getString(R.string.lbl_share_app_content);
		provider.setShareIntent(com.osc.tweet.utils.Utils.getDefaultShareIntent(provider, subject, text));

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Initialize the navigation drawer.
	 */
	private void initDrawer() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.application_name,
					R.string.app_name) {
				@Override
				public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);

					if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
						EventBus.getDefault().post(new OpenedDrawerEvent(Gravity.LEFT));
					} else if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
						EventBus.getDefault().post(new OpenedDrawerEvent(Gravity.RIGHT));
					}
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);

		}
	}


	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		showAppList();
		checkAndInit();
		if (mPbDlg != null && mPbDlg.isShowing()) {
			mPbDlg.dismiss();
		}
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		showAppList();
		checkAndInit();
		if (mPbDlg != null && mPbDlg.isShowing()) {
			mPbDlg.dismiss();
		}
	}


	/**
	 * Check play-service and do init-pages.
	 */
	private void checkAndInit() {
		checkPlayService();
		if (Prefs.getInstance().isEULAOnceConfirmed() && mViewPager == null) {
			initViewPager();
		}
	}

	/**
	 * Show all external applications links.
	 */
	private void showAppList() {
		getSupportFragmentManager().beginTransaction().replace(R.id.app_list_fl, AppListImpFragment.newInstance(this))
				.commit();
	}

	private void dismissEdit() {
		int screenWidth = DeviceUtils.getScreenSize(getApplication()).Width;
		ViewHelper.setRotation(mEditBtn, 360f * 4);
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mEditBtn);
		animator.x(-screenWidth).rotation(0).setDuration(getResources().getInteger(R.integer.anim_fast_duration))
				.start();
	}

	private void showInputEdit() {
		mEditBtn.setVisibility(View.VISIBLE);
		int screenWidth = DeviceUtils.getScreenSize(getApplication()).Width;
		ViewHelper.setTranslationX(mEditBtn, -screenWidth);
		ViewHelper.setRotation(mEditBtn, -360f * 4);
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mEditBtn);
		animator.x(screenWidth - getResources().getDimensionPixelSize(R.dimen.float_button_anim_qua)).rotation(0)
				.setDuration(getResources().getInteger(R.integer.anim_fast_duration)).start();

	}

	/**
	 * Make the main screen, pages, friends-list etc.
	 */
	private void initViewPager() {
		if (!AuthUtil.isLegitimate(getApplicationContext())) {
			showDialogFragment(LoginDialog.newInstance(getApplicationContext()), null);
		} else {
			if(App.Instance.getTweetFavoritesList() == null) {
				AsyncTaskCompat.executeParallel(new LoadFavoriteAsyncTask());
			}
		}
	}

	/**
	 * Task to load favorite-list.
	 */
	class LoadFavoriteAsyncTask extends AsyncTask<Object, TweetFavoritesList, TweetFavoritesList> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mOpenFriendsListV.setVisibility(View.INVISIBLE);
			mSmoothProgressBar.setVisibility(View.INVISIBLE);

//			mPbDlg = ProgressDialog.show(MainActivity.this, null, getString(R.string.msg_load_favorite));
//			mPbDlg.setCancelable(false);
		}

		@Override
		protected TweetFavoritesList doInBackground(Object... params) {
			try {
				return OscApi.tweetFavoritesList(App.Instance);
			} catch (IOException e) {
				return null;
			} catch (OscTweetException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(TweetFavoritesList tweetFavoritesList) {
			super.onPostExecute(tweetFavoritesList);
//			if (mPbDlg != null && mPbDlg.isShowing()) {
//				mPbDlg.dismiss();
//			}
			if (tweetFavoritesList != null &&
					tweetFavoritesList.getStatus() == com.osc4j.ds.common.Status.STATUS_OK) {
				App.Instance.setTweetFavoritesList(tweetFavoritesList);

				mOpenFriendsListV.setVisibility(View.VISIBLE);
				mSmoothProgressBar.setVisibility(View.VISIBLE);

				mViewPager = (ViewPager) findViewById(R.id.vp);
				mViewPager.setOffscreenPageLimit(3);
				mPagerAdapter = new MainViewPagerAdapter(MainActivity.this, getSupportFragmentManager());
				mViewPager.setAdapter(mPagerAdapter);
				// Bind the tabs to the ViewPager
				mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
				mTabs.setViewPager(mViewPager);
				mTabs.setVisibility(View.VISIBLE);
				mTabs.setIndicatorColorResource(R.color.common_white);
				showInputEdit();
				getSupportFragmentManager().beginTransaction().replace(R.id.my_info_fl,
						MyInfoFragment.newInstance(MainActivity.this)).commit();
			} else {
				showDialogFragment(new DialogFragment() {
					@Override
					public Dialog onCreateDialog(Bundle savedInstanceState) {
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setMessage(R.string.msg_reload_favorite)
								.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										AsyncTaskCompat.executeParallel(new LoadFavoriteAsyncTask());
									}
								});
						return builder.create();
					}
				} , null);
			}
		}
	};
}
