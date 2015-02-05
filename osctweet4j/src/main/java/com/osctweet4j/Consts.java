package com.osctweet4j;

import com.squareup.okhttp.MediaType;

/**
 * Constrains.
 *
 * @author Xinyue Zhao
 */
public final class Consts {
	/**
	 * Broadcast Action: The authentication is done.
	 */
	public static final String ACTION_AUTH_DONE = "com.osctweet4j.action.AUTH_DONE";
	/**
	 * Body type when http.
	 */
	static final MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");
	/**
	 * Application keys.
	 */
	static final String CLIENT_ID = "EOW46fNRtr7FgSlHAVz4";
	static final String CLIENT_SECRET = "eXc7xJv1uliOm3WRmo7r9IwzuqrvxYYu";
	/**
	 * Host of osc.
	 */
	static final String OSC_HOST = "https://www.oschina.net/";
	/**
	 * Location to get access token.
	 */
	static final String TOKEN_URL = OSC_HOST + "action/openapi/token";
	/**
	 * A redirect to a web-app.
	 */
	static final String REDIRECT_URL = "http://wanlingzhao.eu.pn/index.html";
	/**
	 * Http-body-format when wanna a refreshed token.
	 */
	static final String REFRESH_TOKEN_BODY = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
			"&grant_type=refresh_token&redirect_uri=" + REDIRECT_URL + "&code=%s&dataType=json&refresh_token=%s";
	/**
	 * Http-body-format when asking token.
	 */
	static final String GET_TOKEN_BODY = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
			"&grant_type=authorization_code&redirect_uri=" + REDIRECT_URL + "&code=%s&dataType=json";
	/**
	 * Location to do authorization.
	 */
	static final String AUTHORIZE_URL = OSC_HOST +
			"action/oauth2/authorize?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URL;
	/**
	 * Key for storage "uid" of json of access-token.
	 */
	static final String KEY_UID = "uid";
	/**
	 * Key for storage "expires_in" of json of access-token in seconds.
	 */
	static final String KEY_EXPIRES_IN_SECONDS = "expires_in";
	/**
	 * Calculated session expires timestamps by "expires_in".
	 */
	static final String KEY_EXPIRES_TIME_IN_MILLIS = "expires_time";
	/**
	 * Key for storage "token_type" of json of access-token.
	 */
	static final String KEY_TOKEN_TYPE = "token_type";
	/**
	 * Key for storage "refresh_token" of json of access-token.
	 */
	static final String KEY_REFRESH_TOKEN = "refresh_token";
	/**
	 * Key for storage "access_token" of json of access-token.
	 */
	static final String KEY_ACCESS_TOKEN = "access_token";

	/**
	 * Publish tweet.
	 */
	static final String TWEET_PUB_URL = OSC_HOST + "action/openapi/tweet_pub";
	static final String TWEET_PUB_BODY = "access_token=%s&msg=%s";
	/**
	 * Get tweet-lists
	 */
	static final String TWEET_LIST_URL = OSC_HOST + "action/openapi/tweet_list";
	static final String TWEET_LIST_BODY = "access_token=%s&pageSize=20&page=%d&dataType=json";
}
