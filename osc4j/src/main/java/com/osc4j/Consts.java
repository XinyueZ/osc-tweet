package com.osc4j;

import com.squareup.okhttp.MediaType;

/**
 * Constrains.
 *
 * @author Xinyue Zhao
 */
public final class Consts {
	/**
	 * Body type when http.
	 */
	static final MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");

	/**
	 * Broadcast Action: The authentication is done.
	 */
	public static final String ACTION_AUTH_DONE = "com.osctweet4j.action.AUTH_DONE";
	/**
	 * Host of osc.
	 */
	static final String OSC_HOST = "http://osc-server-848.appspot.com/";
	/**
	 * Key for storage user-id
	 */
	static final String KEY_UID = "uid";
	/**
	 * Your name.
	 */
	static final String KEY_NAME = "name";
	/**
	 * Key for storage session
	 */
	static final String KEY_SESSION = "oscid";
	/**
	 * Key for storage access-token
	 */
	static final String KEY_ACCESS_TOKEN = "access_token";
	/**
	 * Key for storage for expires.
	 */
	static final String KEY_EXPIRES = "expires";

	/**
	 * Login data format.
	 */
	static final String LOGIN_PARAMS = "u=%s&pw=%s";
	/**
	 * Login
	 */
	static final String LOGIN_URL = OSC_HOST + "login";
	/**
	 * Publish tweet.
	 */
	static final String TWEET_PUB_URL = OSC_HOST + "tweetPub?uid=%d&msg=%s";
	/**
	 * Get tweet-lists
	 */
	static final String TWEET_LIST_URL = OSC_HOST + "tweetList?page=%d";
	/**
	 * Get my tweet-lists
	 */
	static final String TWEET_MY_LIST_URL = OSC_HOST + "myTweetList?uid=%d&page=%d";
	/**
	 * Get my tweet-lists
	 */
	static final String TWEET_HOTSPOT_LIST_URL = OSC_HOST + "hotspotTweetList?page=%d";
}
