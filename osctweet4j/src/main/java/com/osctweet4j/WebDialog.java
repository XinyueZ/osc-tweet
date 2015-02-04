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
	 * Application keys.
	 */
	private static final String CLIENT_ID = "EOW46fNRtr7FgSlHAVz4";
	private static final String CLIENT_SECRET = "eXc7xJv1uliOm3WRmo7r9IwzuqrvxYYu";
	/**
	 * Host of osc.
	 */
	private static final String OSC_HOST = "https://www.oschina.net/";
	/**
	 * A redirect to a web-app.
	 */
	private static final String REDIRECT_URL = "http://wanlingzhao.eu.pn/index.html";
	/**
	 * Location to do authorization.
	 */
	private static final String AUTHORIZE_URL = OSC_HOST +
			"action/oauth2/authorize?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URL;
	/**
	 * Location to get access token.
	 */
	private static final String TOKEN_URL = OSC_HOST + "action/openapi/token";
	/**
	 * Http-body-format when asking token.
	 */
	private static final String GET_TOKEN_BODY = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
			"&grant_type=authorization_code&redirect_uri=" + REDIRECT_URL + "&code=%s&dataType=json";
	/**
	 * Http-body-format when wanna a refreshed token.
	 */
	private static final String REFRESH_TOKEN_BODY = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
			"&grant_type=refresh_token&redirect_uri=" + REDIRECT_URL + "&code=%s&dataType=json&refresh_token=%s";
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
	 * Key for storage "uid" of json of access-token.
	 */
	private static final String KEY_UID = "uid";
	/**
	 * Key for storage "expires_in" of json of access-token in seconds.
	 */
	private static final String KEY_EXPIRES_IN_SECONDS = "expires_in";
	/**
	 * Calculated session expires timestamps by "expires_in".
	 */
	private static final String KEY_EXPIRES_TIME_IN_MILLIS = "expires_time";
	/**
	 * Key for storage "token_type" of json of access-token.
	 */
	private static final String KEY_TOKEN_TYPE = "token_type";
	/**
	 * Key for storage "refresh_token" of json of access-token.
	 */
	private static final String KEY_REFRESH_TOKEN = "refresh_token";
	/**
	 * Key for storage "access_token" of json of access-token.
	 */
	private static final String KEY_ACCESS_TOKEN = "access_token";

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
		mWebView.loadUrl(AUTHORIZE_URL);
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
				if (url.contains(REDIRECT_URL + "?code=")) {
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
				String tokenBody = String.format(GET_TOKEN_BODY, code);
				RequestBody body = RequestBody.create(CONTENT_TYPE, tokenBody);
				Request request = new Request.Builder().url(TOKEN_URL).post(body).build();
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
						mPrefs.edit().putInt(KEY_UID, object.getInt(KEY_UID)).commit();
						mPrefs.edit().putLong(KEY_EXPIRES_IN_SECONDS, object.getInt(KEY_EXPIRES_IN_SECONDS)).commit();
						long exp = TimeUnit.SECONDS.toMillis(object.getInt(KEY_EXPIRES_IN_SECONDS));
						long expiresTime = System.currentTimeMillis() + exp;
						Log.i(TAG, "Expires time: " + expiresTime);
						mPrefs.edit().putLong(KEY_EXPIRES_TIME_IN_MILLIS, expiresTime).commit();
						mPrefs.edit().putString(KEY_TOKEN_TYPE, object.getString(KEY_TOKEN_TYPE)).commit();
						mPrefs.edit().putString(KEY_REFRESH_TOKEN, object.getString(KEY_REFRESH_TOKEN)).commit();
						mPrefs.edit().putString(KEY_ACCESS_TOKEN, object.getString(KEY_ACCESS_TOKEN)).commit();
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
