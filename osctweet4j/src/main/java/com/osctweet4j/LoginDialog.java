package com.osctweet4j;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import com.osctweet4j.ds.Login;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * A login dialog.
 *
 * @author Xinyue Zhao
 */
public final class LoginDialog extends DialogFragment {
	private static final String TAG = LoginDialog.class.getName();
	/**
	 * Root layout.
	 */
	private ViewGroup mRootVg;
	/**
	 * User name.
	 */
	private EditText mNameEt;
	/**
	 * Password.
	 */
	private EditText mPasswordEt;
	/**
	 * Progress indicator.
	 */
	private ProgressDialog mPb;
	/**
	 * Send message when auth is done.
	 */
	private LocalBroadcastManager mLocalBroadcastManager;

	/**
	 * Initialize an {@link  LoginDialog}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link LoginDialog}.
	 */
	public static DialogFragment newInstance(Context context) {
		return (DialogFragment) Fragment.instantiate(context, LoginDialog.class.getName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(activity.getApplication());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		container = new FrameLayout(getActivity());
		mRootVg = container;
		return container;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LinearLayout container = new LinearLayout(getActivity());
		container.setOrientation(LinearLayout.VERTICAL);
		container.setBackgroundColor(Color.GREEN);

		mNameEt = new EditText(getActivity());
		mNameEt.setHint("Account");
		mNameEt.setText("shikeai20121015@gmail.com");
		mNameEt.setTextColor(Color.BLACK);
		mPasswordEt = new EditText(getActivity());
		mPasswordEt.setHint("Password");
		mPasswordEt.setText("474006");
		mPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
		mPasswordEt.setTextColor(Color.BLACK);

		container.addView(mNameEt);
		container.addView(mPasswordEt);

		Button loginBtn = new Button(getActivity());
		loginBtn.setText("Login");
		Button regBtn = new Button(getActivity());
		regBtn.setText("Register");
		LinearLayout buttons = new LinearLayout(getActivity());
		buttons.setOrientation(LinearLayout.HORIZONTAL);
		buttons.addView(loginBtn);
		buttons.addView(regBtn);
		container.addView(buttons);

		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AsyncTaskCompat.executeParallel(new AsyncTask<Object, Login, Login>() {
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						mPb = ProgressDialog.show(getActivity(), "Login...", null);
					}

					@Override
					protected Login doInBackground(Object... params) {

						try {
							return OscApi.login(getActivity().getApplicationContext(), mNameEt.getText().toString(),
									mPasswordEt.getText().toString());
						} catch (BadPaddingException e) {
							e.printStackTrace();
						} catch (InvalidKeyException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (IllegalBlockSizeException e) {
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (NoSuchPaddingException e) {
							e.printStackTrace();
						} catch (InvalidAlgorithmParameterException e) {
							e.printStackTrace();
						} catch (OscTweetException e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Login login) {
						super.onPostExecute(login);
						mPb.dismiss();
						if (login != null) {
							dismiss();
							mLocalBroadcastManager.sendBroadcast(new Intent(Consts.ACTION_AUTH_DONE));
						} else {
							//TODO error-handling for login.
							mNameEt.setText("");
							mPasswordEt.setText("");
						}
					}
				});
			}
		});


		ScreenSize screenSize = getScreenSize(getActivity());
		mRootVg.addView(container, new LayoutParams(screenSize.Width, screenSize.Height));
	}

	/**
	 * Get {@link  ScreenSize} of default/first display.
	 *
	 * @param cxt
	 * 		{@link  android.content.Context} .
	 *
	 * @return A {@link  ScreenSize}.
	 */
	private static ScreenSize getScreenSize(Context cxt) {
		return getScreenSize(cxt, 0);
	}

	/**
	 * Get {@link  ScreenSize} with different {@code displayIndex} .
	 *
	 * @param cxt
	 * 		{@link android.content.Context} .
	 * @param displayIndex
	 * 		The index of display.
	 *
	 * @return A {@link  ScreenSize}.
	 */
	private static ScreenSize getScreenSize(Context cxt, int displayIndex) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		Display[] displays = DisplayManagerCompat.getInstance(cxt).getDisplays();
		Display display = displays[displayIndex];
		display.getMetrics(displaymetrics);
		return new ScreenSize(displaymetrics.widthPixels, displaymetrics.heightPixels);
	}

	/**
	 * Screen-size in pixels.
	 */
	private static class ScreenSize {
		int Width;
		int Height;

		ScreenSize(int _width, int _height) {
			Width = _width;
			Height = _height;
		}
	}

}
