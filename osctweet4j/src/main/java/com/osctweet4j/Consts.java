package com.osctweet4j;

/**
 * Constrains.
 *
 * @author Xinyue Zhao
 */
public final class Consts {
	/**
	 * Application keys.
	 */
	public static final String CLIENT_ID = "EOW46fNRtr7FgSlHAVz4";
	public static final String CLIENT_SECRET = "eXc7xJv1uliOm3WRmo7r9IwzuqrvxYYu";
	/**
	 * Host of osc.
	 */
	public static final String OSC_HOST = "https://www.oschina.net/";
	/**
	 * Location to get access token.
	 */
	public static final String TOKEN_URL = OSC_HOST + "action/openapi/token";
	/**
	 * A redirect to a web-app.
	 */
	public static final String REDIRECT_URL = "http://wanlingzhao.eu.pn/index.html";
	/**
	 * Http-body-format when wanna a refreshed token.
	 */
	public static final String REFRESH_TOKEN_BODY = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
			"&grant_type=refresh_token&redirect_uri=" + REDIRECT_URL + "&code=%s&dataType=json&refresh_token=%s";
	/**
	 * Http-body-format when asking token.
	 */
	public static final String GET_TOKEN_BODY = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET +
			"&grant_type=authorization_code&redirect_uri=" + REDIRECT_URL + "&code=%s&dataType=json";
	/**
	 * Location to do authorization.
	 */
	public static final String AUTHORIZE_URL = OSC_HOST +
			"action/oauth2/authorize?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URL;
	/**
	 * Key for storage "uid" of json of access-token.
	 */
	public static final String KEY_UID = "uid";
	/**
	 * Key for storage "expires_in" of json of access-token in seconds.
	 */
	public static final String KEY_EXPIRES_IN_SECONDS = "expires_in";
	/**
	 * Calculated session expires timestamps by "expires_in".
	 */
	public static final String KEY_EXPIRES_TIME_IN_MILLIS = "expires_time";
	/**
	 * Key for storage "token_type" of json of access-token.
	 */
	public static final String KEY_TOKEN_TYPE = "token_type";
	/**
	 * Key for storage "refresh_token" of json of access-token.
	 */
	public static final String KEY_REFRESH_TOKEN = "refresh_token";
	/**
	 * Key for storage "access_token" of json of access-token.
	 */
	public static final String KEY_ACCESS_TOKEN = "access_token";
}
