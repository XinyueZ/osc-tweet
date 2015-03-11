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
	 * A feedback with vibration  on/off when operating is succeed.
	 */
	public static final String SETTING_VIBRATION_FEEDBACK = "setting.vibration.feedback";

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
	 * @param cxt {@link android.content.Context}.
	 * @return Array of {@link java.lang.String}.
	 */
	public String[] getQuickReplyList(Context cxt) {
		String original =  getString(cxt.getString(R.string.quick_reply_list), null);

		String[] messages = original.split(";");
		return messages;
	}

	/**
	 * A feedback with vibration  on/off when operating is succeed.
	 * @param on {@code true} if set on.
	 */
	public void settingVibrationFeedback( boolean on) {
		setBoolean(SETTING_VIBRATION_FEEDBACK, on);
	}

	/**
	 *
	 * @return  {@code true} if set on that a feedback with vibration  on/off when operating is succeed.
	 */
	public boolean settingVibrationFeedback() {
		return getBoolean(SETTING_VIBRATION_FEEDBACK, getBoolean(DEFAULT_SETTING_VIBRATION_FEEDBACK, false));
	}
}
