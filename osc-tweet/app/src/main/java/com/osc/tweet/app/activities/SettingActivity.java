package com.osc.tweet.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

import com.chopping.application.LL;
import com.chopping.bus.ApplicationConfigurationDownloadedEvent;
import com.chopping.bus.ApplicationConfigurationLoadingIgnoredEvent;
import com.chopping.exceptions.CanNotOpenOrFindAppPropertiesException;
import com.chopping.exceptions.InvalidAppPropertiesException;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.utils.Prefs;

import de.greenrobot.event.EventBus;


/**
 * Setting .
 *
 * @author Xinyue Zhao
 */
public final class SettingActivity extends PreferenceActivity {

	/**
	 * The "ActionBar".
	 */
	private Toolbar mToolbar;

	/**
	 * Progress indicator.
	 */
	private ProgressDialog mPb;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.chopping.bus.ApplicationConfigurationDownloadedEvent}
	 *
	 * @param e
	 * 		Event {@link  com.chopping.bus.ApplicationConfigurationDownloadedEvent}.
	 */
	public void onEvent(ApplicationConfigurationDownloadedEvent e) {
		onAppConfigLoaded();
	}

	/**
	 * Handler for {@link com.chopping.bus.ApplicationConfigurationLoadingIgnoredEvent}.
	 *
	 * @param e
	 * 		Event {@link com.chopping.bus.ApplicationConfigurationLoadingIgnoredEvent}.
	 */
	public void onEvent(ApplicationConfigurationLoadingIgnoredEvent e) {
		LL.i("Ignored a change to load application's configuration.");
		onAppConfigIgnored();
	}

	//------------------------------------------------

	/**
	 * Show an instance of SettingsActivity.
	 *
	 * @param context
	 * 		A context object.
	 */
	public static void showInstance(Activity context) {
		Intent intent = new Intent(context, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		mPb = ProgressDialog.show(this, null, getString(R.string.msg_load_config));
		mPb.setCancelable(true);

		mToolbar = (Toolbar) getLayoutInflater().inflate(R.layout.setting_toolbar, null, false);
		addContentView(mToolbar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mToolbar.setTitle(R.string.lbl_settings);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		mToolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backPressed();
			}
		});


		((MarginLayoutParams) findViewById(android.R.id.list).getLayoutParams()).topMargin = getActionBarHeight(this);
	}


	/**
	 * Get height of {@link android.support.v7.app.ActionBar}.
	 *
	 * @param activity
	 * 		{@link Activity} that hosts an  {@link android.support.v7.app.ActionBar}.
	 *
	 * @return Height of bar.
	 */
	public static int getActionBarHeight(Activity activity) {
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = activity.obtainStyledAttributes(abSzAttr);
		return a.getDimensionPixelSize(0, -1);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			backPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onResume() {
		EventBus.getDefault().registerSticky(this);
		super.onResume();

		String mightError = null;
		try {
			Prefs.getInstance().downloadApplicationConfiguration();
		} catch (InvalidAppPropertiesException _e) {
			mightError = _e.getMessage();
		} catch (CanNotOpenOrFindAppPropertiesException _e) {
			mightError = _e.getMessage();
		}
		if (mightError != null) {
			new AlertDialog.Builder(this).setTitle(com.chopping.R.string.app_name).setMessage(mightError).setCancelable(
					false).setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).create().show();
		}
	}

	@Override
	protected void onPause() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	/**
	 * Remove the progress indicator.
	 */
	private void dismissPb() {
		if (mPb != null && mPb.isShowing()) {
			mPb.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		backPressed();
	}

	private void onAppConfigLoaded() {
		dismissPb();
		init();
	}

	private void onAppConfigIgnored() {
		dismissPb();
		init();
	}

	/**
	 * Init the settings.
	 */
	private void init() {
		Prefs prefs = Prefs.getInstance();

		CheckBoxPreference vibration = (CheckBoxPreference) findPreference(Prefs.SETTING_VIBRATION_FEEDBACK);
		vibration.setChecked(prefs.settingVibrationFeedback());


		CheckBoxPreference showMe = (CheckBoxPreference) findPreference(Prefs.SETTING_SHOW_ME_IN_NOTICES_LIST);
		showMe.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				com.chopping.utils.Utils.showLongToast(App.Instance, R.string.msg_works_in_next_request);
				return true;
			}
		});
		showMe.setChecked(prefs.settingShowMeInNoticesList());


		CheckBoxPreference openClear = (CheckBoxPreference) findPreference(Prefs.SETTING_NOTICES_OPEN_TO_CLEAR);
		openClear.setChecked(prefs.settingNoticesOpenToClear());

		ListPreference photoRotate = (ListPreference) findPreference(Prefs.SETTING_PHOTO_ROTATE_SLOW_OR_FAST);
		String[] types = getResources().getStringArray(R.array.setting_photo_rotate_types);
		int index = prefs.settingPhotoRotateSlowOrFast();
		photoRotate.setSummary(types[index]);
		photoRotate.setValue(String.valueOf(prefs.settingPhotoRotateSlowOrFast()));
		photoRotate.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int index = Integer.valueOf(newValue.toString());
				String[] types = getResources().getStringArray(R.array.setting_photo_rotate_types);
				preference.setSummary(types[index]);
				return true;
			}
		});
	}

	private void backPressed() {
		dismissPb();
		ActivityCompat.finishAfterTransition(this);
	}
}
