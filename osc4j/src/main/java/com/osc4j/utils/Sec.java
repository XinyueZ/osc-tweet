package com.osc4j.utils;

/**
 * Define Key and CommonIV for AuthUtil.
 *
 * On the server the write in same file "sec.go" you need.
 *
 * @author Xinyue Zhao
 */
public final class Sec {

	public static final byte[] KEY = new byte[] { 11, 45, 78, 110, 118, 9, 3, 4, 18, 47, 3, 7, 77, 8, 56, 101 };

	public static final byte[] IV = { 34, 35, 35, 57, 68, 4, 35, 36, 7, 8, 35, 23, 35, 86, 35, 23 };
}
