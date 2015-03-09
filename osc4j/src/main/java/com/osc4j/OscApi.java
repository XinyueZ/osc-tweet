package com.osc4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.osc4j.ds.Login;
import com.osc4j.ds.comment.Comments;
import com.osc4j.ds.common.Status;
import com.osc4j.ds.common.StatusResult;
import com.osc4j.ds.personal.FriendsList;
import com.osc4j.ds.personal.MyInformation;
import com.osc4j.ds.personal.UserInformation;
import com.osc4j.ds.tweet.TweetDetail;
import com.osc4j.ds.tweet.TweetList;
import com.osc4j.ds.tweet.TweetListItem;
import com.osc4j.exceptions.Osc4jConfigErrorException;
import com.osc4j.exceptions.Osc4jConfigNotFoundException;
import com.osc4j.exceptions.OscTweetException;
import com.osc4j.utils.AuthUtil;
import com.osc4j.utils.Utils;
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
	 * 		{@link android.content.Context}.
	 * @param account
	 * 		Login-Account.
	 * @param password
	 * 		Login password.
	 *
	 * @return {@link com.osc4j.ds.Login}, contains user info.
	 *
	 * @throws NoSuchPaddingException,
	 * @throws InvalidKeyException,
	 * @throws NoSuchAlgorithmException,
	 * @throws IllegalBlockSizeException,
	 * @throws BadPaddingException,
	 * @throws InvalidAlgorithmParameterException,
	 * @throws IOException,
	 * @throws OscTweetException,
	 * @throws Osc4jConfigNotFoundException,
	 * @throws Osc4jConfigErrorException
	 */
	public static Login login(Context cxt, String account, String password) throws NoSuchPaddingException,
			InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException, IOException, OscTweetException, Osc4jConfigNotFoundException,
			Osc4jConfigErrorException {
		InputStream is = null;
		Properties prop = new Properties();
		try {
			AssetManager assManager = cxt.getAssets();
			is = assManager.open(Consts.CONFIG_FILE);
			prop.load(is);
		} catch (IOException ex) {
			throw new Osc4jConfigNotFoundException();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new Osc4jConfigNotFoundException();
				}
			}
		}

		String appId = prop.getProperty("appid");
		String appSec = prop.getProperty("appsec");
		String redirectUrl = prop.getProperty("appred");
		String scope = prop.getProperty("scope");

		if (TextUtils.isEmpty(appId)) {
			throw new Osc4jConfigErrorException("The application-id [appid] must be given.");
		}

		if (TextUtils.isEmpty(appSec)) {
			throw new Osc4jConfigErrorException("The application security-code [appsec] must be given.");
		}
		if (TextUtils.isEmpty(redirectUrl)) {
			throw new Osc4jConfigErrorException("A redirection-url of application [appred] must be given.");
		}
		if (TextUtils.isEmpty(scope)) {
			throw new Osc4jConfigErrorException("The application-scope [scope] must be given.");
		}

		String info = String.format(Consts.LOGIN_PARAMS, account, password, appId, appSec, redirectUrl, scope);
		String pubBody = AuthUtil.encrypt(info);
		RequestBody body = RequestBody.create(Consts.CONTENT_TYPE, pubBody);
		Request request = new Request.Builder().url(Consts.LOGIN_URL).post(body).build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();

		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
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
			prefs.edit().putLong(Consts.KEY_EXPIRES, System.currentTimeMillis() + login.getUser().getExpired() * 1000)
					.commit();

			String expiresTime = Utils.convertTimestamps2DateString(cxt, prefs.getLong(Consts.KEY_EXPIRES, -1));
			Log.d(OscApi.class.getSimpleName(), "Expire on: " + expiresTime);

			prefs.edit().putString(Consts.KEY_SESSION, session).commit();
			prefs.edit().putString(Consts.KEY_ACCESS_TOKEN, token).commit();
			return login;
		}
	}

	/**
	 * Get list of all tweets.
	 *
	 * @param context
	 * 		{@link android.content.Context}
	 * @param page
	 * 		Set the page index of list, start from <code>1</code>.
	 * @param myTweets
	 * 		If <code>true</code> then show all my tweets, it works only when <code>hotspot</code> is <code>false</code>.
	 * @param hotspot
	 * 		If <code>true</code> then show all hotspot and ignore value of <code>myTweets</code>.
	 *
	 * @return The {@link com.osc4j.ds.tweet.TweetList}.
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static TweetList tweetList(Context context, int page, boolean myTweets, boolean hotspot) throws IOException,
			OscTweetException {
		TweetList ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		int uid = prefs.getInt(Consts.KEY_UID, 0);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		String url;
		if (hotspot) {
			url = String.format(Consts.TWEET_HOTSPOT_LIST_URL, page);
		} else {
			url = myTweets ? String.format(Consts.TWEET_MY_LIST_URL, uid, page) : String.format(Consts.TWEET_LIST_URL,
					page);
		}
		Request request = new Request.Builder().url(url).get().header("Cookie", sessionInCookie + ";" + tokenInCookie)
				.build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
			response.body().close();
			throw new OscTweetException();
		} else {
			ret = sGson.fromJson(response.body().string(), TweetList.class);
		}

		return ret;
	}


	public static StatusResult tweetPub(Context context, String msg) throws IOException, OscTweetException {
		StatusResult ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		Request request = new Request.Builder().url(Consts.TWEET_PUB_URL).get().header("Cookie",
				sessionInCookie + ";" + tokenInCookie + ";msg=" + msg).build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
			response.body().close();
			throw new OscTweetException();
		} else {
			ret = sGson.fromJson(response.body().string(), StatusResult.class);
		}

		return ret;
	}

	public static StatusResult tweetCommentPub(Context context, TweetListItem tweetListItem, String content) throws
			IOException, OscTweetException {
		StatusResult ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		String url = String.format(Consts.TWEET_COMMENT_PUB_URL, tweetListItem.getId());
		Request request = new Request.Builder().url(url).get().header("Cookie",
				sessionInCookie + ";" + tokenInCookie + ";content=" + content).build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
			response.body().close();
			throw new OscTweetException();
		} else {
			ret = sGson.fromJson(response.body().string(), StatusResult.class);
		}

		return ret;
	}


	/**
	 * Get friend list.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 *
	 * @return
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static FriendsList friendsList(Context context) throws IOException, OscTweetException {
		FriendsList ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		String url = Consts.FRIENDS_URL;
		Request request = new Request.Builder().url(url).get().header("Cookie", sessionInCookie + ";" + tokenInCookie)
				.build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
			response.body().close();
			throw new OscTweetException();
		} else {
			ret = sGson.fromJson(response.body().string(), FriendsList.class);
		}

		return ret;
	}

	/**
	 * Get user information.
	 *
	 * @param context
	 * @param friend
	 * @param needMessage
	 *
	 * @return
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static UserInformation userInformation(Context context, long friend, boolean needMessage) throws IOException,
			OscTweetException {
		UserInformation ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get my user-id on oschina.net
		int uid = prefs.getInt(Consts.KEY_UID, -1);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		String url = String.format(Consts.USER_INFORMATION_URL, uid, friend, needMessage ? 1 : 0);
		Request request = new Request.Builder().url(url).get().header("Cookie", sessionInCookie + ";" + tokenInCookie)
				.build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
			response.body().close();
			throw new OscTweetException();
		} else {
			ret = sGson.fromJson(response.body().string(), UserInformation.class);
		}

		return ret;
	}

	/**
	 * Update relation to other users.
	 *
	 * @param context
	 * @param friend
	 * @param cancel
	 *
	 * @return
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static StatusResult updateRelation(Context context, com.osc4j.ds.personal.User friend, boolean cancel) throws
			IOException, OscTweetException {
		StatusResult ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		String url = String.format(Consts.UPDATE_RELATION_URL, friend.getUid(), cancel ? 0 : 1);
		Request request = new Request.Builder().url(url).get().header("Cookie", sessionInCookie + ";" + tokenInCookie)
				.build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
			response.body().close();
			throw new OscTweetException();
		} else {
			ret = sGson.fromJson(response.body().string(), StatusResult.class);
		}

		return ret;
	}


	/**
	 * Get my personal information.
	 *
	 * @return {@link com.osc4j.ds.personal.MyInformation}.
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static MyInformation myInformation(Context context) throws IOException, OscTweetException {
		MyInformation ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		Request request = new Request.Builder().url(Consts.MY_INFORMATION_URL).get().header("Cookie",
				sessionInCookie + ";" + tokenInCookie).build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
			response.body().close();
			throw new OscTweetException();
		} else {
			ret = sGson.fromJson(response.body().string(), MyInformation.class);
		}

		return ret;
	}


	/**
	 * Get comments of a {@link com.osc4j.ds.tweet.TweetListItem}.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param item
	 * 		{@link com.osc4j.ds.tweet.TweetListItem}.
	 * @param page
	 * 		Which page.
	 *
	 * @return
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static Comments tweetCommentList(Context context, TweetListItem item, int page) throws IOException,
			OscTweetException {
		Comments ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);

		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;

		String url = String.format(Consts.TWEET_COMMENT_LIST_URL, item.getId(), page);
		Request request = new Request.Builder().url(url).get().header("Cookie", sessionInCookie + ";" + tokenInCookie)
				.build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
			response.body().close();
			throw new OscTweetException();
		} else {
			ret = sGson.fromJson(response.body().string(), Comments.class);
		}

		return ret;
	}

	/**
	 * Get detail of a tweet item.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param tweetId
	 * 		The id of tweet.
	 *
	 * @return A {@link com.osc4j.ds.tweet.TweetDetail}.
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static TweetDetail tweetDetail(Context context, long tweetId) throws IOException, OscTweetException {
		TweetDetail ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);

		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;

		String url = String.format(Consts.TWEET_DETAIL_URL, tweetId);
		Request request = new Request.Builder().url(url).get().header("Cookie", sessionInCookie + ";" + tokenInCookie)
				.build();
		OkHttpClient client = new OkHttpClient();
		client.networkInterceptors().add(new StethoInterceptor());
		Response response = client.newCall(request).execute();
		int responseCode = response.code();
		if (responseCode >= Status.STATUS_ERR) {
			response.body().close();
			throw new OscTweetException();
		} else {
			ret = sGson.fromJson(response.body().string(), TweetDetail.class);
		}
		return ret;
	}


	/**
	 * Get last replies of tweets you joined. Who at you, who replied you, etc.
	 * @param context
	 * @param me
	 * @param page
	 * @return
	 * @throws IOException
	 * @throws OscTweetException
	 */
	//	public static Comments lastTweetActiveList(Context context, Am me, int page) throws IOException,
	//			OscTweetException {
	//		Comments ret;
	//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	//		String session = prefs.getString(Consts.KEY_SESSION, null);
	//		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
	//
	//		//Get session and set to cookie returning to server.
	//		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
	//		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
	//
	//		String url = String.format(Consts.TWEET_COMMENT_LIST_URL, item.getId(), page);
	//		Request request = new Request.Builder().url(url).get().header("Cookie", sessionInCookie + ";" + tokenInCookie)
	//				.build();
	//		OkHttpClient client = new OkHttpClient();
	//		client.networkInterceptors().add(new StethoInterceptor());
	//		Response response = client.newCall(request).execute();
	//		int responseCode = response.code();
	//		if (responseCode >= Status.STATUS_ERR) {
	//			response.body().close();
	//			throw new OscTweetException();
	//		} else {
	//			ret = sGson.fromJson(response.body().string(), Comments.class);
	//		}
	//
	//		return ret;
	//	}
}
