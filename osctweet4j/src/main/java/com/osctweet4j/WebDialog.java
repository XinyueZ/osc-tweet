package com.osctweet4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A AuthO2 WebView dialog to get client code.
 *
 * @author Xinyue Zhao
 */
public final class WebDialog extends DialogFragment {
	private static final String TAG = WebDialog.class.getName();
	/**
	 * Body type when http.
	 */
	private static final MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");
	/**
	 * WebView.
	 */
	private WebView mWebView;
	/**
	 * Root layout.
	 */
	private ViewGroup mRootVg;
	/**
	 * Progress indicator.
	 */
	private ProgressDialog mPb;
	/**
	 * Http-client.
	 */
	private OkHttpClient mHttpClient;
	/**
	 * Storage.
	 */
	private SharedPreferences mPrefs;

	/**
	 * Initialize an {@link  WebDialog}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link WebDialog}.
	 */
	public static DialogFragment newInstance(Context context) {
		return (DialogFragment) Fragment.instantiate(context, WebDialog.class.getName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		container = new FrameLayout(getActivity());
		mRootVg = container;
		return container;
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mHttpClient = new OkHttpClient();
		mWebView = new WebView(getActivity());
		mWebView.loadUrl(Consts.AUTHORIZE_URL);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
				if (mPb == null) {
					mPb = ProgressDialog.show(getActivity(), null, null);
				}
				if (!mPb.isShowing()) {
					mPb.show();
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (mPb != null && mPb.isShowing()) {
					mPb.dismiss();
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				if (url.contains(Consts.REDIRECT_URL + "?code=")) {
					Uri uri = Uri.parse(url);
					String code = uri.getQueryParameter("code");//The authorization code.
					authToken(code);
					dismiss();
				}
				return false;
			}
		});
		ScreenSize screenSize = getScreenSize(getActivity());
		mRootVg.addView(mWebView, new LayoutParams(screenSize.Width, screenSize.Height));
	}

	/**
	 * Get {@link  ScreenSize} of default/first display.
	 *
	 * @param cxt
	 * 		{@link  android.content.Context} .
	 *
	 * @return A {@link  ScreenSize}.
	 */
	private static ScreenSize getScreenSize(Context cxt) {
		return getScreenSize(cxt, 0);
	}

	/**
	 * Get {@link  ScreenSize} with different {@code displayIndex} .
	 *
	 * @param cxt
	 * 		{@link android.content.Context} .
	 * @param displayIndex
	 * 		The index of display.
	 *
	 * @return A {@link  ScreenSize}.
	 */
	private static ScreenSize getScreenSize(Context cxt, int displayIndex) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		Display[] displays = DisplayManagerCompat.getInstance(cxt).getDisplays();
		Display display = displays[displayIndex];
		display.getMetrics(displaymetrics);
		return new ScreenSize(displaymetrics.widthPixels, displaymetrics.heightPixels);
	}

	/**
	 * Screen-size in pixels.
	 */
	private static class ScreenSize {
		int Width;
		int Height;

		ScreenSize(int _width, int _height) {
			Width = _width;
			Height = _height;
		}
	}

	/**
	 * Get auth-token.
	 *
	 * @param code
	 * 		The authorization code generated by osc when login.
	 */
	private void authToken(final String code) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String tokenBody = String.format(Consts.GET_TOKEN_BODY, code);
				RequestBody body = RequestBody.create(CONTENT_TYPE, tokenBody);
				Request request = new Request.Builder().url(Consts.TOKEN_URL).post(body).build();
				try {
					Response response = mHttpClient.newCall(request).execute();
					int responseCode = response.code();
					if (responseCode >= 300) {
						response.body().close();
						Log.e(TAG,
								"Error when getting response of access-token, client-code: " + code + ", http-code: " +
										responseCode);
					} else {
						String json = response.body().string();
						JSONObject object = new JSONObject(json);
						mPrefs.edit().putInt(Consts.KEY_UID, object.getInt(Consts.KEY_UID)).commit();
						mPrefs.edit().putLong(Consts.KEY_EXPIRES_IN_SECONDS, object.getInt(Consts.KEY_EXPIRES_IN_SECONDS)).commit();
						long exp = TimeUnit.SECONDS.toMillis(object.getInt(Consts.KEY_EXPIRES_IN_SECONDS));
						long expiresTime = System.currentTimeMillis() + exp;
						Log.i(TAG, "Expires time: " + expiresTime);
						mPrefs.edit().putLong(Consts.KEY_EXPIRES_TIME_IN_MILLIS, expiresTime).commit();
						mPrefs.edit().putString(Consts.KEY_TOKEN_TYPE, object.getString(Consts.KEY_TOKEN_TYPE)).commit();
						mPrefs.edit().putString(Consts.KEY_REFRESH_TOKEN, object.getString(Consts.KEY_REFRESH_TOKEN)).commit();
						mPrefs.edit().putString(Consts.KEY_ACCESS_TOKEN, object.getString(Consts.KEY_ACCESS_TOKEN)).commit();
					}
				} catch (IOException e) {
					Log.e(TAG, "Can't execute request to get access-token, client-code: " + code);
				} catch (JSONException e) {
					Log.e(TAG, "No json when response of access-token, client-code: " + code);
				}
			}
		}).start();
	}
}
