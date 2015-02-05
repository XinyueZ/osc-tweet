package com.osctweet4j;

import java.io.IOException;

import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.osctweet4j.ds.TweetList;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * API methods
 *
 * @author Xinyue Zhao
 */
public final class OscApi {

	private static final Gson sGson = new Gson();


	/**
	 * See. <a href="http://www.oschina.net/openapi/docs/tweet_pub">tweet_pub</a>
	 *
	 * @param activity
	 * 		{@link android.support.v4.app.FragmentActivity}.
	 * @param msg
	 * 		Message to share.
	 */
	public static void tweetPub(FragmentActivity activity, String msg) throws IOException, OscTweetException {
		final String token = PreferenceManager.getDefaultSharedPreferences(activity.getApplication()).getString(
				Consts.KEY_ACCESS_TOKEN, null);
		if (!TextUtils.isEmpty(token)) {
			String pubBody = String.format(Consts.TWEET_PUB_BODY, token, msg);
			RequestBody body = RequestBody.create(Consts.CONTENT_TYPE, pubBody);
			Request request = new Request.Builder().url(Consts.TWEET_PUB_URL).post(body).build();
			Response response = new OkHttpClient().newCall(request).execute();
			int responseCode = response.code();
			if (responseCode >= 300) {
				response.body().close();
				throw new OscTweetException();
			}
		} else {
			WebDialog.newInstance(activity.getApplication()).show(activity.getSupportFragmentManager(), null);
		}
	}


	/**
	 * See. <a href="http://www.oschina.net/openapi/docs/tweet_list">tweet_list</a>
	 *
	 * @param activity
	 * 		{@link android.support.v4.app.FragmentActivity}.
	 * @param page
	 * 		Page index.
	 *
	 * @return List of all tweets.
	 */
	public static TweetList tweetList(FragmentActivity activity, final int page) throws IOException, OscTweetException {
		TweetList ret = null;
		final String token = PreferenceManager.getDefaultSharedPreferences(activity.getApplication()).getString(
				Consts.KEY_ACCESS_TOKEN, null);
		if (!TextUtils.isEmpty(token)) {
			String listBody = String.format(Consts.TWEET_LIST_BODY, token, page);
			RequestBody body = RequestBody.create(Consts.CONTENT_TYPE, listBody);
			Request request = new Request.Builder().url(Consts.TWEET_LIST_URL).post(body).build();
			Response response = new OkHttpClient().newCall(request).execute();
			int responseCode = response.code();
			if (responseCode >= 300) {
				response.body().close();
				throw new OscTweetException();
			} else {
				ret = sGson.fromJson(response.body().string(), TweetList.class);
			}
		} else {
			WebDialog.newInstance(activity.getApplication()).show(activity.getSupportFragmentManager(), null);
		}
		return ret;
	}
}
