package com.osctweet4j;

import java.io.IOException;

import android.content.Context;
import android.preference.PreferenceManager;
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
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param msg
	 * 		Message to share.
	 */
	public static void tweetPub(Context cxt, String msg) throws IOException, OscTweetUnauthorizationException,
			OscTweetException {
		final String token = PreferenceManager.getDefaultSharedPreferences(cxt).getString(Consts.KEY_ACCESS_TOKEN,
				null);
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
			throw new OscTweetUnauthorizationException();
		}
	}


	/**
	 * See. <a href="http://www.oschina.net/openapi/docs/tweet_list">tweet_list</a>
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param page
	 * 		Page index.
	 *
	 * @return List of all tweets.
	 */
	public static TweetList tweetList(Context cxt, final int page) throws IOException, OscTweetUnauthorizationException,
			OscTweetException {
		TweetList ret = null;
		final String token = PreferenceManager.getDefaultSharedPreferences(cxt).getString(Consts.KEY_ACCESS_TOKEN,
				null);
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
			throw new OscTweetUnauthorizationException();
		}
		return ret;
	}
}
