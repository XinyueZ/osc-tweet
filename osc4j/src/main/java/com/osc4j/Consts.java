package com.osc4j;

import com.squareup.okhttp.MediaType;

/**
 * Constrains.
 *
 * @author Xinyue Zhao
 */
public final class Consts {
	/**
	 * A configuration-file must be located under asset.
	 */
	static final String CONFIG_FILE = "osc4j.properties";
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
	public static final String KEY_SESSION = "oscid";
	/**
	 * Key for storage access-token
	 */
	public static final String KEY_ACCESS_TOKEN = "access_token";
	/**
	 * Key for storage for expires.
	 */
	public static final String KEY_EXPIRES = "expires";

	/**
	 * Login data format.
	 */
	static final String LOGIN_PARAMS = "u=%s&pw=%s&appid=%s&appsec=%s&redurl=%s&scope=%s";
	/**
	 * Login
	 */
	static final String LOGIN_URL = OSC_HOST + "login";
	/**
	 * Publish tweet.
	 */
	static final String TWEET_PUB_URL = OSC_HOST + "tweetPub";
	/**
	 * Reply someone on tweet.
	 */
	static final String TWEET_REPLY_URL = OSC_HOST + "tweetReply";
	/**
	 * Publish comment on tweet.
	 */
	static final String TWEET_COMMENT_PUB_URL = OSC_HOST + "tweetCommentPub?id=%d";
	/**
	 * Get tweet-lists.
	 */
	static final String TWEET_LIST_URL = OSC_HOST + "tweetList?page=%d";
	/**
	 * Get my tweet-lists.
	 */
	static final String TWEET_MY_LIST_URL = OSC_HOST + "myTweetList?uid=%d&page=%d";
	/**
	 * Get my tweet-lists.
	 */
	static final String TWEET_HOTSPOT_LIST_URL = OSC_HOST + "hotspotTweetList?page=%d";
	/**
	 * Friends-list.
	 */
	static final String FRIENDS_URL = OSC_HOST + "friendsList";
	/**
	 * User-information.
	 */
	static final String USER_INFORMATION_URL = OSC_HOST + "userInformation?uid=%d&fri=%d&msg=%d";
	/**
	 * Update relation between me and user.
	 */
	static final String UPDATE_RELATION_URL = OSC_HOST + "updateRelation?fri=%d&rel=%d";
	/**
	 * My personal information.
	 */
	static final String MY_INFORMATION_URL = OSC_HOST + "myInformation?me=%d";
	/**
	 * All comments of a tweet item.
	 */
	static final String TWEET_COMMENT_LIST_URL = OSC_HOST + "tweetCommentList?id=%d&page=%d";
	/**
	 * Tweet detail.
	 */
	static final String TWEET_DETAIL_URL = OSC_HOST + "tweetDetail?id=%d";
	/**
	 * Clear "@me" notice.
	 */
	static final String CLEAR_AT_NOTICE_URL = OSC_HOST + "clearAtNotice";
	/**
	 * Clear comment notice.
	 */
	static final String CLEAR_COMMENTS_NOTICE_URL = OSC_HOST + "clearCommentsNotice";
	/**
	 * Get list of all favorite-tweets.
	 */
	static final String FAVORITE_TWEETS_URL = OSC_HOST + "tweetFavorites?uid=%d";
	/**
	 * Add tweet to favorite.
	 */
	static final String ADD_TWEET_FAVORITE_URL = OSC_HOST + "addTweetFavorite?uid=%d&id=%d";
	/**
	 * Delete tweet to favorite.
	 */
	static final String DEL_TWEET_FAVORITE_URL = OSC_HOST + "delTweetFavorite?uid=%d&id=%d";
}