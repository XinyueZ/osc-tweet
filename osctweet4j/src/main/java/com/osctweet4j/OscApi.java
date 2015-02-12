package com.osctweet4j;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.osctweet4j.ds.Login;
import com.osctweet4j.ds.TweetList;
import com.osctweet4j.utils.AuthUtil;
import com.osctweet4j.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
	 *
	 * @param cxt
	 * @param account
	 * @param password
	 *
	 * @return
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static Login login(Context cxt, String account, String password) throws NoSuchPaddingException,
			InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException, IOException, OscTweetException {
		String info = String.format(Consts.LOGIN_PARAMS, account, password);
		String pubBody = AuthUtil.encrypt(info);
		RequestBody body = RequestBody.create(Consts.CONTENT_TYPE, pubBody);
		Request request = new Request.Builder().url(Consts.LOGIN_URL).post(body).build();
		OkHttpClient client = new OkHttpClient();
		Response response = client.newCall(request).execute();

		int responseCode = response.code();
		if (responseCode >= 300) {
			response.body().close();
			throw new OscTweetException();
		} else {
			String cookie = response.header("Set-Cookie");
			if (TextUtils.isEmpty(cookie)) {
				throw new OscTweetException();
			}

			String[] sessionAndToken = cookie.split(";");
			String sessionPair = sessionAndToken[0];
			String tokenPair = sessionAndToken[1];
			String session = (sessionPair.split("="))[1];
			String token = (tokenPair.split("="))[1];
			Login login = sGson.fromJson(response.body().string(), Login.class);

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cxt);
			prefs.edit().putInt(Consts.KEY_UID, login.getUser().getUid()).commit();
			prefs.edit().putString(Consts.KEY_NAME, login.getUser().getName()).commit();
			prefs.edit().putInt(Consts.KEY_EXPIRES, login.getUser().getExpired()).commit();
			prefs.edit().putString(Consts.KEY_SESSION, session).commit();
			prefs.edit().putString(Consts.KEY_ACCESS_TOKEN, token).commit();
			return login;
		}
	}

	/**
	 * Get list of all tweets.
	 *
	 * @param activity
	 * @param page
	 *
	 * @return
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static TweetList tweetList(FragmentActivity activity, int page, boolean myTweets) throws IOException,
			OscTweetException {
		TweetList ret = null;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplication());
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		int uid = prefs.getInt(Consts.KEY_UID, 0);
		if (!TextUtils.isEmpty(session) && !TextUtils.isEmpty(token)) {
			//Get session and set to cookie returning to server.
			String sessionInCookie = Consts.KEY_SESSION + "=" + session;
			String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
			String url = myTweets ? String.format(Consts.TWEET_MY_LIST_URL, uid, page) : String.format(
					Consts.TWEET_LIST_URL, page);
			Request request = new Request.Builder().url(url).get().header("Cookie",
					sessionInCookie + ";" + tokenInCookie).build();
			Response response = new OkHttpClient().newCall(request).execute();
			int responseCode = response.code();
			if (responseCode >= 300) {
				response.body().close();
				throw new OscTweetException();
			} else {
				ret = sGson.fromJson(response.body().string(), TweetList.class);
			}
		} else {
			Utils.showDialogFragment(activity, LoginDialog.newInstance(activity.getApplicationContext()), null);
		}
		return ret;
	}



}
