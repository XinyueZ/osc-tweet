package com.osctweet4j.utils;

/**
 * Define Key and CommonIV for AuthUtil.
 *
 * On the server the write in same file "sec.go" you need.
 *
 * @author Xinyue Zhao
 */
public final class Sec {

	public static final byte[] KEY = new byte[] { 11, 45, 78  };

	public static final byte[] IV = { 34, 35, 35, 57, 68, 4, 3 };
}
