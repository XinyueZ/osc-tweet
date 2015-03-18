package com.osc.tweet.app.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.utils.Prefs;
import com.osc.tweet.views.WebViewEx;
import com.osc.tweet.views.WebViewEx.OnWebViewExScrolledListener;
import com.osc4j.Consts;
import com.osc4j.OscApi;

/**
 * A WebView.
 *
 * @author Xinyue Zhao
 */
public final class WebViewActivity extends BaseActivity  {

	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_webview;
	/**
	 * The menu to this view.
	 */
	private static final int MENU = R.menu.menu_webview;
	/**
	 * Store link that the {@link WebView} opens.
	 */
	private static final String EXTRAS_URL = "com.hpush.app.activities.EXTRAS.Url";
	/**
	 * The title of the browser
	 */
	private static final String EXTRAS_TITLE = "com.hpush.app.activities.EXTRAS.Msg";
	/**
	 * {@link WebView} shows homepage.
	 */
	private WebViewEx mWebView;
	/**
	 * The "ActionBar".
	 */
	private Toolbar mToolbar;
	/**
	 * Progress indicator.
	 */
	private SwipeRefreshLayout mRefreshLayout;

	/**
	 * Height of action-bar general.
	 */
	private int mActionBarHeight;

	/**
	 * Show a website in the {@link WebView}.
	 *
	 * @param cxt
	 * 		The start {@link android.content.Context}.
	 * @param url
	 * 		The location of the site.
	 * @param title
	 * 		The title of this view.
	 */
	public static void showInstance(Activity cxt, String url, String title) {
		String session = OscApi.getSession(App.Instance);
		String cookieString = Consts.KEY_SESSION + "=" + session;
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeSessionCookie();
		cookieManager.setCookie(Prefs.getInstance().websiteDomain(), cookieString);
		if (Build.VERSION.SDK_INT >= 21) {
			cookieManager.flush();
		} else {
			CookieSyncManager.getInstance().sync();
		}

		Intent intent = new Intent(cxt, WebViewActivity.class);
		intent.putExtra(EXTRAS_URL, url);
		intent.putExtra(EXTRAS_TITLE, title);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivity(cxt, intent, null);
	}


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

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		//		if (getResources().getBoolean(R.bool.landscape)) {
		//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//		}
		calcActionBarHeight();
		//Progress-indicator.
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_srl);
		mRefreshLayout.setColorSchemeResources(R.color.color_pocket_1, R.color.color_pocket_2, R.color.color_pocket_3,
				R.color.color_pocket_4);

		mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				animToolActionBar(-getActionBarHeight() * 4);
				mWebView.reload();
			}
		});
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mWebView = (WebViewEx) findViewById(R.id.home_wv);
		mWebView.setOnWebViewExScrolledListener(new OnWebViewExScrolledListener() {
			@Override
			public void onScrollChanged(boolean isUp) {
				if (isUp) {
					animToolActionBar(0);
				} else {
					animToolActionBar(-getActionBarHeight() * 4);
				}
			}

			@Override
			public void onScrolledTop() {
				animToolActionBar(-getActionBarHeight() * 4);
			}
		});
		WebSettings settings = mWebView.getSettings();
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setCacheMode(WebSettings.LOAD_NORMAL);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(false);
		settings.setDomStorageEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
				mRefreshLayout.setRefreshing(true);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mRefreshLayout.setRefreshing(false);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		handleIntent();
		animToolActionBar(-getActionBarHeight() * 4);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent();
	}


	private void handleIntent() {
		Intent intent = getIntent();
		String url = intent.getStringExtra(EXTRAS_URL);
		String title = intent.getStringExtra(EXTRAS_TITLE);
		mWebView.loadUrl(url);
		getSupportActionBar().setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(MENU, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ActivityCompat.finishAfterTransition(this);
			break;
		case R.id.action_forward:
			if (mWebView.canGoForward()) {
				mWebView.goForward();
			}
			break;
		case R.id.action_backward:
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}



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


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	@Override
	public void onBackPressed() {
		backPressed();
	}

	private void backPressed() {
		ActivityCompat.finishAfterTransition(this);
	}
}
