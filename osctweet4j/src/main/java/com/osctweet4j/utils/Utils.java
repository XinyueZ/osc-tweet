package com.osctweet4j.utils;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;

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
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	/**
	 * Show  {@link android.support.v4.app.DialogFragment}.
	 *
	 * @param activity
	 * 		Host activity.
	 * @param dlgFrg
	 * 		An instance of {@link android.support.v4.app.DialogFragment}.
	 * @param tagName
	 * 		Tag name for dialog, default is "dlg". To grantee that only one instance of {@link
	 * 		android.support.v4.app.DialogFragment} can been seen.
	 */
	public static void showDialogFragment(FragmentActivity activity, DialogFragment dlgFrg, String tagName) {
		try {
			if (dlgFrg != null) {
				DialogFragment dialogFragment = dlgFrg;
				FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dlg");
				if (prev != null) {
					ft.remove(prev);
				}
				try {
					if (TextUtils.isEmpty(tagName)) {
						dialogFragment.show(ft, "dlg");
					} else {
						dialogFragment.show(ft, tagName);
					}
				} catch (Exception _e) {
				}
			}
		} catch (Exception _e) {
		}
	}
}
