package com.osc.tweet.utils;

import android.content.Context;

import com.chopping.application.BasicPrefs;
import com.osc.tweet.R;

/**
 * Store app and device information.
 *
 * @author Chris.Xinyue Zhao
 */
public final class Prefs extends BasicPrefs {

	/**
	 * The Instance.
	 */
	private static Prefs sInstance;

	/**
	 * Storage. Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 * {@code true} if EULA has been shown and agreed.
	 */
	private static final String KEY_EULA_SHOWN = "key_eula_shown";
	/**
	 * Default in config for setting feedback with vibration on/off when operating is succeed.
	 */
	private static final String DEFAULT_SETTING_VIBRATION_FEEDBACK = "default_setting_vibration_feedback";
	/**
	 * Default in config for setting that opens one notice (not reply) then clear all of them.
	 */
	private static final String DEFAULT_SETTING_NOTICES_OPEN_TO_CLEAR = "default_setting_notices_open_to_clear";
	/**
	 * Default in config for setting that rotates photo slow or medium(fast).
	 */
	private static final String DEFAULT_SETTING_PHOTO_ROTATE_SLOW_OR_FAST = "default_setting_photo_rotate_slow_or_fast";
	/**
	 * Default in config for showing "me" in notices.
	 */
	private static final String DEFAULT_SETTING_SHOW_ME_IN_NOTICES_LIST = "default_setting_show_me_in_notices_list";
	/**
	 * A feedback with vibration  on/off when operating is succeed.
	 */
	public static final String SETTING_VIBRATION_FEEDBACK = "setting.vibration.feedback";
	/**
	 * Open one notice (not reply) then clear all of them.
	 */
	public static final String SETTING_NOTICES_OPEN_TO_CLEAR = "setting.notices.open.to.clear";
	/**
	 * Rotate photo slow or medium(fast).
	 */
	public static final String SETTING_PHOTO_ROTATE_SLOW_OR_FAST = "setting.photo.rotate.slow.or.fast";
	/**
	 * Showing "me" in notices.
	 */
	public static final String SETTING_SHOW_ME_IN_NOTICES_LIST = "setting.show.me.in.notices.list";

	private Prefs() {
		super(null);
	}

	/**
	 * Created a DeviceData storage.
	 *
	 * @param context
	 * 		A context object.
	 */
	private Prefs(Context context) {
		super(context);
	}

	/**
	 * Singleton method.
	 *
	 * @param context
	 * 		A context object.
	 *
	 * @return single instance of DeviceData
	 */
	public static Prefs createInstance(Context context) {
		if (sInstance == null) {
			synchronized (Prefs.class) {
				if (sInstance == null) {
					sInstance = new Prefs(context);
				}
			}
		}
		return sInstance;
	}

	/**
	 * Singleton getInstance().
	 *
	 * @return The instance of Prefs.
	 */
	public static Prefs getInstance() {
		return sInstance;
	}


	/**
	 * Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @return {@code true} if EULA has been shown and agreed.
	 */
	public boolean isEULAOnceConfirmed() {
		return getBoolean(KEY_EULA_SHOWN, false);
	}

	/**
	 * Set whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @param isConfirmed
	 * 		{@code true} if EULA has been shown and agreed.
	 */
	public void setEULAOnceConfirmed(boolean isConfirmed) {
		setBoolean(KEY_EULA_SHOWN, isConfirmed);
	}

	/**
	 * A list to quick reply message.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 *
	 * @return Array of {@link java.lang.String}.
	 */
	public String[] getQuickReplyList(Context cxt) {
		String original = getString(cxt.getString(R.string.quick_reply_list), null);

		String[] messages = original.split(";");
		return messages;
	}

	/**
	 * A feedback with vibration  on/off when operating is succeed.
	 *
	 * @param on
	 * 		{@code true} if set on.
	 */
	public void settingVibrationFeedback(boolean on) {
		setBoolean(SETTING_VIBRATION_FEEDBACK, on);
	}

	/**
	 * @return {@code true} if set on that a feedback with vibration  on/off when operating is succeed.
	 */
	public boolean settingVibrationFeedback() {
		boolean defaultValue = getBoolean(DEFAULT_SETTING_VIBRATION_FEEDBACK, false);
		return getBoolean(SETTING_VIBRATION_FEEDBACK, defaultValue);
	}

	/**
	 * Open one notice (not reply) then clear all of them.
	 *
	 * @param yesNo
	 * 		Default {@code false} if not clear.
	 */
	public void settingNoticesOpenToClear(boolean yesNo) {
		setBoolean(SETTING_NOTICES_OPEN_TO_CLEAR, yesNo);
	}

	/**
	 * Open one notice (not reply) then clear all of them.
	 *
	 * @return Default {@code false} if not clear.
	 */
	public boolean settingNoticesOpenToClear() {
		boolean defaultValue = getBoolean(DEFAULT_SETTING_NOTICES_OPEN_TO_CLEAR, false);
		return getBoolean(SETTING_NOTICES_OPEN_TO_CLEAR, defaultValue);
	}

	/**
	 * Rotate photo slow or medium(fast).
	 *
	 * @param slowFast
	 * 		{@code 0} slow, {@code >0} medium(fast).
	 */
	public void settingPhotoRotateSlowOrFast(int slowFast) {
		setString(SETTING_PHOTO_ROTATE_SLOW_OR_FAST, String.valueOf(slowFast));
	}

	/**
	 * Rotate photo slow or medium(fast).
	 *
	 * @return slowFast {@code 0} slow, {@code >0} medium(fast).
	 */
	public int settingPhotoRotateSlowOrFast() {
		String defaultValue = String.valueOf(getInt(
				DEFAULT_SETTING_PHOTO_ROTATE_SLOW_OR_FAST, 0));
		return Integer.valueOf(getString(SETTING_PHOTO_ROTATE_SLOW_OR_FAST, defaultValue));
	}

	/**
	 * Show "me" in notices list.
	 *
	 * @param show
	 * 		{@code true} if show.
	 */
	public void settingShowMeInNoticesList(boolean show) {
		setBoolean(SETTING_SHOW_ME_IN_NOTICES_LIST, show);
	}

	/**
	 * Show "me" in notices list.
	 *
	 * @return {@code true} if show.
	 */
	public boolean settingShowMeInNoticesList() {
		boolean defaultValue = getBoolean(DEFAULT_SETTING_SHOW_ME_IN_NOTICES_LIST, false);
		return getBoolean(SETTING_SHOW_ME_IN_NOTICES_LIST, defaultValue);
	}

}
