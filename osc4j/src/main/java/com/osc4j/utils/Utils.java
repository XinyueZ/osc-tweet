package com.osc4j.utils;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.util.TypedValue;

import static android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.FORMAT_SHOW_YEAR;
import static android.text.format.DateUtils.formatDateTime;

/**
 * Util-methods.
 *
 * @author Xinyue Zhao
 */
public final class Utils {

	/**
	 * Convert the <code>date</code> with <code>oldZone</code> to <cod>newZone</cod>.
	 *
	 * @param date
	 * 		A data which needs new time-zone.
	 * @param oldZone
	 * 		Old time-zone that will be compared with new one.
	 * @param newZone
	 * 		New time-zone.
	 *
	 * @return A new data with <code>newZone</code>.
	 */
	public static Calendar transformTime(Calendar date, TimeZone oldZone, TimeZone newZone) {
		Calendar finalDate = Calendar.getInstance();
		if (date != null) {
			long timeOffset = oldZone.getOffset(date.getTimeInMillis()) - newZone.getOffset(date.getTimeInMillis());
			finalDate.setTimeInMillis(date.getTimeInMillis() - timeOffset);
		}
		return finalDate;

	}


	/**
	 * This method converts device specific pixels to density independent pixels.
	 *
	 * @param context
	 * 		Context to get resources and device specific display metrics
	 * @param px
	 * 		A value in px (pixels) unit. Which we need to convert into db
	 *
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(Context context, float px) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
	}


	/**
	 * Convert a timestamps to a readable date in string.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param timestamps
	 * 		A long value for a timestamps.
	 *
	 * @return A date string format.
	 */
	public static String convertTimestamps2DateString(Context cxt, long timestamps) {
		return formatDateTime(cxt, timestamps, FORMAT_SHOW_YEAR | FORMAT_SHOW_DATE |
				FORMAT_SHOW_TIME | FORMAT_ABBREV_MONTH);
	}
}
