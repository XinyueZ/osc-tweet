package com.osc.tweet.app.fragments;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.ScreenSize;
import com.chopping.utils.Utils;
import com.osc.tweet.R;
import com.osc.tweet.app.App;
import com.osc4j.OscApi;
import com.osc4j.OscTweetException;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/**
 * Message editor.
 *
 * @author Xinyue Zhao
 */
public final class EditorDialogFragment extends DialogFragment implements OnMenuItemClickListener {
	private static final String EXTRAS_DEFAULT_TEXT = EditorDialogFragment.class.getName() + ".EXTRAS.defaultText";
	private static final String EXTRAS_DEFAULT_FIXED = EditorDialogFragment.class.getName() + ".EXTRAS.defaultFixed";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_dialog_editor;
	/**
	 * Menu for the editor.
	 */
	private static final int MENU = R.menu.menu_editor;
	/**
	 * An "action-bar" for the editor.
	 */
	private Toolbar mToolbar;
	/**
	 * Editor for message.
	 */
	private EditText mEditText;
	/**
	 * {@link android.view.MenuItem} clicked to send message.
	 */
	private MenuItem mSendMi;
	/**
	 * An {@link android.view.View} to indicate that "sending" is in progress.
	 */
	private SmoothProgressBar mSendingIndicatorV;
	/**
	 * Initialize an {@link  EditorDialogFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @param defaultText
	 * 		Default text. It could be {@code null} if no default message provided.
	 * @param isDefaultFixed
	 * 		{@code true} if the default text must be used at first position of message. It should be used only when {@code
	 * 		defaultText != null}.
	 *
	 * @return An instance of {@link EditorDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context, @Nullable String defaultText, boolean isDefaultFixed) {
		Bundle args = new Bundle();
		args.putString(EXTRAS_DEFAULT_TEXT, defaultText);
		args.putBoolean(EXTRAS_DEFAULT_FIXED, isDefaultFixed);
		return (DialogFragment) Fragment.instantiate(context, EditorDialogFragment.class.getName(), args);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Transparent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ScreenSize sz = DeviceUtils.getScreenSize(getActivity().getApplication());
		view.findViewById(R.id.root_v).setLayoutParams(new FrameLayout.LayoutParams(sz.Width, sz.Height));

		mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
		mToolbar.inflateMenu(MENU);
		mToolbar.setTitle(getString(R.string.lbl_editor_title));
		mSendMi = mToolbar.getMenu().findItem(R.id.action_send);
		mSendMi.setOnMenuItemClickListener(this);
		mEditText = (EditText) view.findViewById(R.id.message_content_et);
		String defaultText = getDefaultText();
		if (!TextUtils.isEmpty(defaultText)) {
			mEditText.setText(defaultText);
		}

		mSendingIndicatorV = (SmoothProgressBar) view.findViewById(R.id.sending_pb);
		mSendingIndicatorV.setSmoothProgressDrawableBackgroundDrawable(
				SmoothProgressBarUtils.generateDrawableWithColors(getResources().getIntArray(
						R.array.pocket_background_colors),
						((SmoothProgressDrawable) mSendingIndicatorV.getIndeterminateDrawable()).getStrokeWidth()));
		mSendingIndicatorV.progressiveStart();

	}

	/**
	 * @return Default text. It could be {@code null} if no default message provided.
	 */
	private
	@Nullable
	String getDefaultText() {
		return getArguments().getString(EXTRAS_DEFAULT_TEXT);
	}

	/**
	 * @return {@code true} if the default text must be used at first position of message. It works only when {@code
	 * getDefaultText() != null}.
	 */
	private boolean isDefaultFixed() {
		return getArguments().getBoolean(EXTRAS_DEFAULT_FIXED);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_send:
			sendMessage();
			break;
		}
		return true;
	}

	/**
	 * Send tweet.
	 */
	private void sendMessage() {
		String msg = mEditText.getText().toString();
		if (!TextUtils.isEmpty(msg)) {
			AsyncTaskCompat.executeParallel(new AsyncTask<String, Object, String>() {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					mSendMi.setEnabled(false);
					mSendingIndicatorV.setVisibility(View.VISIBLE);
				}

				@Override
				protected String doInBackground(String... params) {
					String msg = params[0];
					try {
						OscApi.tweetPub(App.Instance, Utils.encode(msg));
					} catch (IOException | OscTweetException e) {
						return getString(R.string.msg_message_sent_failed);
					}
					return getString(R.string.msg_message_sent_successfully);
				}

				@Override
				protected void onPostExecute(String s) {
					super.onPostExecute(s);
					Utils.showLongToast(App.Instance, s);
					if (isVisible() && mSendMi != null) {
						mSendMi.setEnabled(true);
						mSendingIndicatorV.setVisibility(View.INVISIBLE);
						mSendingIndicatorV.progressiveStop();
						dismiss();
					}
				}
			}, msg);
		} else {
			Utils.showShortToast(App.Instance, R.string.msg_message_input);
		}
	}
}
