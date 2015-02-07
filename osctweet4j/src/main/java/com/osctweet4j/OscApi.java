package com.osctweet4j;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.osctweet4j.ds.Login;
import com.osctweet4j.ds.TweetList;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * API methods
 *
 * @author Xinyue Zhao
 */
public final class OscApi {
	/**
	 * Gson.
	 */
	private static final Gson sGson = new Gson();

	/**
	 * Login.
	 * @param cxt
	 * @param account
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static Login login(Context cxt, String account, String password) throws IOException, OscTweetException {
		String url = String.format(Consts.LOGIN_URL, account, password);
		Request request = new Request.Builder().url(url).get().build();
		OkHttpClient client = new OkHttpClient();
		Response response = client.newCall(request).execute();

		int responseCode = response.code();
		if (responseCode >= 300) {
			response.body().close();
			throw new OscTweetException();
		} else {
			String cookie = response.header("Set-Cookie");
			String[] sp = cookie.split("=");
			String session = sp[1];
			Login login = sGson.fromJson(response.body().string(), Login.class);

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cxt);
			prefs.edit().putInt(Consts.KEY_UID, login.getUser().getUid()).commit();
			prefs.edit().putString(Consts.KEY_EXPIRES, login.getUser().getExpired()).commit();
			prefs.edit().putString(Consts.KEY_SESSION, session).commit();
			return login;
		}
	}

	/**
	 * Get list of all tweets.
	 * @param activity
	 * @param page
	 * @return
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static TweetList tweetList(FragmentActivity activity, final int page) throws IOException, OscTweetException {
		TweetList ret = null;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplication());
		String token = prefs.getString(Consts.KEY_SESSION, null);
		if (!TextUtils.isEmpty(token)) {
			//Get session and set to cookie returning to server.
			String sessionInCookie = Consts.OSCID + "=" + prefs.getString(Consts.KEY_SESSION, null);
			String url = String.format(Consts.TWEET_LIST_URL, page);
			Request request = new Request.Builder().url(url).get().header("Cookie", sessionInCookie).build();
			Response response = new OkHttpClient().newCall(request).execute();
			int responseCode = response.code();
			if (responseCode >= 300) {
				response.body().close();
				throw new OscTweetException();
			} else {
				ret = sGson.fromJson(response.body().string(), TweetList.class);
			}
		} else {
			LoginDialog.newInstance(activity.getApplication()).show(activity.getSupportFragmentManager(), null);
		}
		return ret;
	}
}
