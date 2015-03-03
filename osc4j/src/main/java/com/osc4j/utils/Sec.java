package com.osc4j.utils;

/**
 * Define Key and CommonIV for AuthUtil.
 *
 * On the server the write in same file "sec.go" you need.
 *
 * @author Xinyue Zhao
 */
public final class Sec {

	public static final byte[] KEY = new byte[] {13, 0, 21, 110, 34, 45, 3,6, 18, 23, 3, 7, 56, 8, 56, 5};

	public static final byte[] IV = {12, 34, 35, 57, 46, 4, 75, 36, 67, 8, 7, 9, 10, 86, 11, 4};
}
