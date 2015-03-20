package com.osc.tweet.app.activities;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.chopping.utils.Utils;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc.tweet.app.fragments.UserInformationDialogFragment;
import com.osc.tweet.events.OperatingEvent;
import com.osc.tweet.events.ShowUserInformationEvent;
import com.osc.tweet.utils.Prefs;

/**
 * System base {@link android.app.Activity}.
 *
 * @author Xinyue Zhao
 */
public abstract class OscActivity extends BaseActivity{
	/**
	 * Handler for {@link com.osc.tweet.events.ShowUserInformationEvent}.
	 *
	 * @param e
	 * 		Event {@link com.osc.tweet.events.ShowUserInformationEvent}.
	 */
	public void onEvent(ShowUserInformationEvent e) {
		showDialogFragment(UserInformationDialogFragment.newInstance(getApplicationContext(), e.getUserId(), true),
				"userInfo");
	}

	/**
	 * Handler for {@link OperatingEvent}.
	 *
	 * @param e
	 * 		Event {@link OperatingEvent}.
	 */
	public void onEvent(OperatingEvent e) {
		Utils.showLongToast(getApplicationContext(), e.isSuccess() ? getString(R.string.msg_operating_successfully) :
				getString(R.string.msg_operating_failed));
		com.osc.tweet.utils.Utils.vibrationFeedback(App.Instance);
	}


	/**
	 * Show  {@link android.support.v4.app.DialogFragment}.
	 *
	 * @param dlgFrg
	 * 		An instance of {@link android.support.v4.app.DialogFragment}.
	 * @param tagName
	 * 		Tag name for dialog, default is "dlg". To grantee that only one instance of {@link
	 * 		android.support.v4.app.DialogFragment} can been seen.
	 */
	protected void showDialogFragment(DialogFragment dlgFrg, String tagName) {
		try {
			if (dlgFrg != null) {
				DialogFragment dialogFragment = dlgFrg;
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = getSupportFragmentManager().findFragmentByTag("dlg");
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


	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
