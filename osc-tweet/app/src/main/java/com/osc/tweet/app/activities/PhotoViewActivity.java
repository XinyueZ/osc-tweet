package com.osc.tweet.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.chopping.net.TaskHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.osc.tweet.R;
import com.osc.tweet.utils.Prefs;
import com.osc4j.ds.tweet.TweetListItem;

/**
 * Show big image as photo.
 *
 * @author Xinyue Zhao
 */
public final class PhotoViewActivity extends BaseActivity implements ImageListener {
	private static final String EXTRAS_TWEET = "com.osc.tweet.app.extras.TWEET";
	private static final int MENU = R.menu.menu_photo_view;
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_photo_view;
	/**
	 * Tweet object that contains the image.
	 */
	private TweetListItem mTweetListItem;
	/**
	 * Support actionbar.
	 */
	private Toolbar mToolbar;

	/**
	 * Show single instance of {@link PhotoViewActivity}
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param tweetListItem
	 * 		Location to the image.
	 */
	public static void showInstance(Context cxt, TweetListItem tweetListItem) {
		Intent intent = new Intent(cxt, PhotoViewActivity.class);
		intent.putExtra(EXTRAS_TWEET, tweetListItem);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		if (savedInstanceState != null) {
			mTweetListItem = (TweetListItem) savedInstanceState.getSerializable(EXTRAS_TWEET);
		} else {
			mTweetListItem = (TweetListItem) getIntent().getSerializableExtra(EXTRAS_TWEET);
		}
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setTitle(null);
		calcActionBarHeight();
		showImage();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mTweetListItem = (TweetListItem) intent.getSerializableExtra(EXTRAS_TWEET);
		showImage();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRAS_TWEET, mTweetListItem);
	}


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	/**
	 * Show remote image.
	 */
	private void showImage() {
		getSupportActionBar().setTitle(mTweetListItem.getAuthor());
		TaskHelper.getImageLoader().get(mTweetListItem.getImgBig(), this);
	}

	@Override
	public void onResponse(ImageContainer response, boolean isImmediate) {
		ImageView iv = (ImageView) findViewById(R.id.big_img_iv);
		if (response != null && response.getBitmap() != null) {
			iv.setImageBitmap(response.getBitmap());
			animToolActionBar(-getActionBarHeight() * 4);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		findViewById(R.id.loading_pb).setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(MENU, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuShare = menu.findItem(R.id.action_item_share);
		android.support.v7.widget.ShareActionProvider provider =
				(android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);
		String subject = "Hi";
		String text = String.format("%s\n%s", mTweetListItem.getBody(), mTweetListItem.getImgBig());
		provider.setShareIntent(com.osc.tweet.utils.Utils.getDefaultShareIntent(provider, subject, text));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		animToolActionBar(0);
		mHandler.postDelayed(mActionBarAnimTask, 5000); return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mHandler != null) {
			mHandler.removeCallbacks(mActionBarAnimTask);
		}
	}

	/**
	 * Timer for animation on action-bar.
	 */
	private Handler mHandler = new Handler();
	/**
	 * The  animation on action-bar.
	 */
	private Runnable mActionBarAnimTask = new Runnable() {
		@Override
		public void run() {
			animToolActionBar(-getActionBarHeight() * 4);
		}
	};

	/**
	 * Animation and moving actionbar(toolbar).
	 *
	 * @param value
	 * 		The property value of animation.
	 */
	private void animToolActionBar(float value) {
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mToolbar);
		animator.translationY(value).setDuration(400);
	}


	/**
	 * Height of action-bar general.
	 */
	private int mActionBarHeight;

	/**
	 * Calculate height of actionbar.
	 */
	private void calcActionBarHeight() {
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = obtainStyledAttributes(abSzAttr);
		mActionBarHeight = a.getDimensionPixelSize(0, -1);
	}


	/**
	 * @return Height of action-bar general.
	 */
	private int getActionBarHeight() {
		return mActionBarHeight;
	}
}
