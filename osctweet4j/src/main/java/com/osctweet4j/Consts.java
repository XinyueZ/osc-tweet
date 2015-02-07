package com.osctweet4j;

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
	 * Cookie key when session returns.
	 */
	static  final  String OSCID = "oscid";
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
	static final String KEY_SESSION = "session";
	/**
	 * Key for storage for expires.
	 */
	static final String KEY_EXPIRES = "expires";

	/**
	 * Login
	 */
	static final String LOGIN_URL = OSC_HOST + "login?u=%s&pw=%s";
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
}
