package com.osc.tweet.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.chopping.net.TaskHelper;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.osc.tweet.R;
import com.osc.tweet.utils.Prefs;
import com.osc.tweet.views.OnViewAnimatedClickedListener;
import com.osc4j.ds.tweet.TweetListItem;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;


/**
 * Show big image as photo.
 *
 * @author Xinyue Zhao
 */
public final class PhotoViewActivity extends BaseActivity implements ImageListener, OnPhotoTapListener {
	private static final String EXTRAS_TWEET = "com.osc.tweet.app.extras.TWEET";
	/**
	 * Menu on "actionbar".
	 */
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
	 * Operation on the photo.
	 */
	private PhotoView mPhotoView;
	/**
	 * A control for rotation of the photo.
	 */
	private View mRotationLl;
	/**
	 * Original position of the control for rotation of the photo, the {@link #mRotationLl}.
	 */
	private float mRotationCtrlY;
	/**
	 * {@code true} if the photo has been loaded.
	 */
	private boolean mLoaded;
	/**
	 * Handling rotating photo automatically.
	 */
	private final Handler mRotateHandler = new Handler();
	/**
	 * Flag when rotating is processing automatically.
	 */
	private boolean mRotating = false;

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
		calcActionBarHeight();
		if (savedInstanceState != null) {
			mTweetListItem = (TweetListItem) savedInstanceState.getSerializable(EXTRAS_TWEET);
		} else {
			mTweetListItem = (TweetListItem) getIntent().getSerializableExtra(EXTRAS_TWEET);
		}
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setTitle(null);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		calcActionBarHeight();

		mPhotoView = (PhotoView) findViewById(R.id.big_img_iv);
		mPhotoView.setOnPhotoTapListener(this);
		mPhotoView.setZoomable(true);

		mRotationLl = findViewById(R.id.rotation_ll);
		mRotationCtrlY = ViewHelper.getTranslationY(mRotationLl);
		mRotationLl.findViewById(R.id.rotate_right_btn).setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				if (mLoaded) {
					mPhotoView.setRotationBy(90);
				}
			}
		});
		mRotationLl.findViewById(R.id.rotate_down_btn).setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				if (mLoaded) {
					mPhotoView.setRotationBy(90);
				}
			}
		});
		mRotationLl.findViewById(R.id.rotate_left_btn).setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				if (mLoaded) {
					mPhotoView.setRotationBy(-90);
				}
			}
		});
		mRotationLl.findViewById(R.id.rotate_up_btn).setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				if (mLoaded) {
					mPhotoView.setRotationBy(-90);
				}
			}
		});

		mRotationLl.findViewById(R.id.rotate_auto_btn).setOnClickListener(new OnViewAnimatedClickedListener() {
			@Override
			public void onClick() {
				if (mLoaded) {
					toggleRotationAutomatically();
				}
			}
		});

		showImage();
	}

	/**
	 * Rotating photo automatically.
	 */
	private void toggleRotationAutomatically() {
		if (mRotating) {
			mRotateHandler.removeCallbacksAndMessages(null);
		} else {
			rotateLoop();
		}
		mRotating = !mRotating;
	}

	/**
	 * Run rotating photo automatically.
	 */
	private void rotateLoop() {
		mRotateHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPhotoView.setRotationBy(-1);
				rotateLoop();
			}
		}, 20);
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
		if (response != null && response.getBitmap() != null) {
			mPhotoView.setImageBitmap(response.getBitmap());
			animToolActionBar(-getActionBarHeight() * 4);
			animRotationControl(99999999);
			mLoaded = true;
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		findViewById(R.id.load_user_info_pb).setVisibility(View.GONE);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ActivityCompat.finishAfterTransition(this);
			break;
		case R.id.action_undo:
			if(mLoaded) {
				if (mRotating) {
					mRotateHandler.removeCallbacksAndMessages(null);
					mRotating = false;
				}
				mPhotoView.setRotationTo(0);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUIHandler != null) {
			mUIHandler.removeCallbacks(mActionBarAnimTask);
		}
	}

	/**
	 * Timer for animation on action-bar.
	 */
	private Handler mUIHandler = new Handler();
	/**
	 * The  animation on action-bar.
	 */
	private Runnable mActionBarAnimTask = new Runnable() {
		@Override
		public void run() {
			animToolActionBar(-getActionBarHeight() * 4);
			animRotationControl(99999999);
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
	 * Animation and moving rotation control.
	 *
	 * @param value
	 * 		The property value of animation.
	 */
	private void animRotationControl(float value) {
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mRotationLl);
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



	@Override
	public void onPhotoTap(View view, float v, float v1) {
		animToolActionBar(0);
		animRotationControl(mRotationCtrlY);
		mUIHandler.postDelayed(mActionBarAnimTask, 5000);
	}
}
