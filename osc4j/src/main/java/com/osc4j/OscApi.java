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
import com.google.gson.JsonSyntaxException;
import com.osc4j.ds.Login;
import com.osc4j.ds.comment.Comment;
import com.osc4j.ds.comment.Comments;
import com.osc4j.ds.common.NoticeType;
import com.osc4j.ds.common.Status;
import com.osc4j.ds.common.StatusResult;
import com.osc4j.ds.favorite.TweetFavoritesList;
import com.osc4j.ds.personal.FriendsList;
import com.osc4j.ds.personal.MyInformation;
import com.osc4j.ds.personal.PeopleList;
import com.osc4j.ds.personal.Notice;
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
	 * Get current user login session-id.
	 *
	 * @param context
	 * 		{@link Context}.
	 *
	 * @return The current session-id.
	 */
	public static String getSession(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		return session;
	}

	/**
	 * Get current access-token to all original openAPIs.
	 *
	 * @param context
	 * 		{@link Context}.
	 *
	 * @return The current access-token.
	 */
	public static String getAccessToken(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		return token;
	}

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
	 * @throws  JsonSyntaxException
	 */
	public static TweetList tweetList(Context context, int page, boolean myTweets, boolean hotspot) throws IOException,
			OscTweetException, JsonSyntaxException {
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


	public static StatusResult tweetPub(Context context, String msg) throws IOException, OscTweetException, JsonSyntaxException {
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

	public static StatusResult tweetReply(Context context, TweetListItem tweet, String content, Comment comment) throws
			IOException, OscTweetException , JsonSyntaxException{
		return tweetReply(context, tweet.getId(), content, comment.getCommentAuthorId(), comment.getId());
	}

	public static StatusResult tweetReply(Context context, String content, Notice notice) throws IOException,
			OscTweetException , JsonSyntaxException{
		return tweetReply(context, notice.getObjectId(), content, notice.getAuthorId(), notice.getId());
	}

	public static StatusResult tweetReply(Context context, long tweetId, String content, long commentAuthorId,
			long repliedId) throws IOException, OscTweetException , JsonSyntaxException{
		StatusResult ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		int uid = prefs.getInt(Consts.KEY_UID, 0);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		Request request = new Request.Builder().url(Consts.TWEET_REPLY_URL).get().header("Cookie",
				sessionInCookie + ";" + tokenInCookie + ";tId=" + tweetId + ";cnt=" + content + ";recId=" +
						commentAuthorId + ";auId=" + uid + ";repId=" + repliedId).build();
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
			IOException, OscTweetException, JsonSyntaxException {
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
	public static FriendsList friendsList(Context context) throws IOException, OscTweetException, JsonSyntaxException {
		FriendsList ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		int uid = prefs.getInt(Consts.KEY_UID, 0);
		String url = String.format(Consts.FRIENDS_URL, uid);
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
			OscTweetException , JsonSyntaxException{
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
	public static StatusResult updateRelation(Context context, com.osc4j.ds.personal.People friend, boolean cancel) throws
			IOException, OscTweetException, JsonSyntaxException {
		StatusResult ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		String url = String.format(Consts.UPDATE_RELATION_URL, friend.getId(), cancel ? 0 : 1);
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
	 * @param context
	 * 		{@link Context}.
	 * @param showMe
	 * 		{@code true} if I wanna see myself in notices-list.
	 *
	 * @return {@link com.osc4j.ds.personal.MyInformation}.
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static MyInformation myInformation(Context context, boolean showMe) throws IOException, OscTweetException, JsonSyntaxException {
		MyInformation ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		int uid = prefs.getInt(Consts.KEY_UID, 0);
		String url = String.format(Consts.MY_INFORMATION_URL, uid, showMe ? 1 : 0);
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
			OscTweetException, JsonSyntaxException {
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
	public static TweetDetail tweetDetail(Context context, long tweetId) throws IOException, OscTweetException , JsonSyntaxException{
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
	 * Clear different notice.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param type
	 * 		{@link com.osc4j.ds.common.NoticeType}.
	 *
	 * @return The result of clear action.
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static StatusResult clearNotice(Context context, NoticeType type) throws IOException, OscTweetException, JsonSyntaxException {
		StatusResult ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		String url = null;
		switch (type) {
		case AtMe:
			url = Consts.CLEAR_AT_NOTICE_URL;
			break;
		case Comments:
			url = Consts.CLEAR_COMMENTS_NOTICE_URL;
			break;
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
			ret = sGson.fromJson(response.body().string(), StatusResult.class);
		}

		return ret;
	}

	/**
	 * Get list of favorite tweets.
	 *
	 * @param context
	 * 		{@link Context}.
	 *
	 * @return The {@link TweetFavoritesList}.
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static TweetFavoritesList tweetFavoritesList(Context context) throws IOException, OscTweetException, JsonSyntaxException {
		TweetFavoritesList ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);

		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		int uid = prefs.getInt(Consts.KEY_UID, 0);

		String url = String.format(Consts.FAVORITE_TWEETS_URL, uid);
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
			ret = sGson.fromJson(response.body().string(), TweetFavoritesList.class);
		}

		return ret;
	}

	/**
	 * Add a new tweet to favorite.
	 *
	 * @param context
	 * 		{@link Context}.
	 * @param item
	 * 		{@link TweetListItem} A new tweet item.
	 *
	 * @return {@link StatusResult}
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static StatusResult addTweetFavorite(Context context, TweetListItem item) throws IOException,
			OscTweetException , JsonSyntaxException{
		StatusResult ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);

		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		int uid = prefs.getInt(Consts.KEY_UID, 0);

		String url = String.format(Consts.ADD_TWEET_FAVORITE_URL, uid, item.getId());
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
	 * Delete the tweet from favorite.
	 *
	 * @param context
	 * 		{@link Context}.
	 * @param item
	 * 		{@link TweetListItem} A new tweet item.
	 *
	 * @return {@link StatusResult}
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static StatusResult delTweetFavorite(Context context, TweetListItem item) throws IOException,
			OscTweetException, JsonSyntaxException {
		StatusResult ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);

		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		int uid = prefs.getInt(Consts.KEY_UID, 0);

		String url = String.format(Consts.DEL_TWEET_FAVORITE_URL, uid, item.getId());
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
	 * Get some unknown and no relation people.
	 *
	 * @param context
	 * 		{@link Context}.
	 *
	 * @return {@link PeopleList}
	 *
	 * @throws IOException
	 * @throws OscTweetException
	 */
	public static PeopleList noRelationPeople(Context context, int myPage, int friendPage) throws IOException, OscTweetException, JsonSyntaxException {
		PeopleList ret;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);

		//Get session and set to cookie returning to server.
		String sessionInCookie = Consts.KEY_SESSION + "=" + session;
		String tokenInCookie = Consts.KEY_ACCESS_TOKEN + "=" + token;
		int uid = prefs.getInt(Consts.KEY_UID, 0);

		String url = String.format(Consts.NO_RELATION_PEOPLE, uid, myPage, friendPage);
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
			ret = sGson.fromJson(response.body().string(), PeopleList.class);
		}

		return ret;
	}
}
