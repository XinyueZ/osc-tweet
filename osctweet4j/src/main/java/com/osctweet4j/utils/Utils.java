package com.osctweet4j.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Util-methods.
 *
 * @author Xinyue Zhao
 */
public final class Utils {

	/**
	 * Convert the <code>date</code> with <code>oldZone</code> to <cod>newZone</cod>.
	 *
	 * @param date A data which needs new time-zone.
	 * @param oldZone Old time-zone that will be compared with new one.
	 * @param newZone New time-zone.
	 * @return A new data with <code>newZone</code>.
	 */
	public static Calendar transformTime(Calendar date, TimeZone oldZone,
			TimeZone newZone) {
		Calendar finalDate = Calendar.getInstance();
		if (date != null) {
			long timeOffset = oldZone.getOffset(date.getTimeInMillis())
					- newZone.getOffset(date.getTimeInMillis());
			finalDate.setTimeInMillis(date.getTimeInMillis() - timeOffset);
		}
		return finalDate;

	}
}
