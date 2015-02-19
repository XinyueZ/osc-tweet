package com.osc4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.osc4j.ds.Login;
import com.osc4j.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * A login dialog.
 *
 * @author Xinyue Zhao
 */
public final class LoginDialog extends DialogFragment {
	private static final String LOGO_COLOR = "#f5f5f5";
	private static final String LOGO_URL = "https://dl.dropboxusercontent.com/s/mzxis3wzbg14dn3/oschina.png";
	private static final String LOGIN_BUTTON_URL = "https://dl.dropboxusercontent.com/s/wstg4ilxl0i9xb4/ic_login.png";
	private static final String REGISTER_BUTTON_URL =
			"https://dl.dropboxusercontent.com/s/r735v70lnrslq29/ic_register.png";
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
	 * Broadcast-receiver for event after authentication is done.
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			dismiss();
		}
	};
	/**
	 * Filter for {@link #mBroadcastReceiver}.
	 */
	private IntentFilter mIntentFilter = new IntentFilter(Consts.ACTION_AUTH_DONE);

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
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Dialog);
		setCancelable(false);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(activity.getApplication());
		mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
	}

	@Override
	public void onDetach() {
		mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		container = new FrameLayout(getActivity());
		mRootVg = container;
		mRootVg.setBackgroundColor(Color.parseColor(LOGO_COLOR));
		return container;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LinearLayout editors = new LinearLayout(getActivity());
		editors.setOrientation(LinearLayout.VERTICAL);

		TextView oscLogoV = new TextView(getActivity());
		editors.addView(oscLogoV, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		oscLogoV.setText("Login on www.oschina.net");
		((LayoutParams) oscLogoV.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;

		AsyncTaskCompat.executeParallel(new AsyncTask<TextView, Bitmap, Bitmap>() {
			private WeakReference<TextView> mLogoIvRef;

			@Override
			protected Bitmap doInBackground(TextView... params) {
				mLogoIvRef = new WeakReference<>(params[0]);
				Request request = new Request.Builder().url(LOGO_URL).get().build();
				Bitmap bmp = null;
				try {
					Response response = new OkHttpClient().newCall(request).execute();
					InputStream is = new BufferedInputStream(response.body().byteStream());
					bmp = BitmapFactory.decodeStream(is);
					is.close();
				} catch (IOException e) {
				}
				return bmp;
			}

			@Override
			protected void onPostExecute(Bitmap bmp) {
				super.onPostExecute(bmp);
				if (bmp != null && mLogoIvRef.get() != null) {
					mLogoIvRef.get().setText("");
					mLogoIvRef.get().setBackgroundDrawable(new BitmapDrawable(getResources(), bmp));
				}
			}
		}, oscLogoV);

		mNameEt = new EditText(getActivity());
		mNameEt.setHint("Account");
		mNameEt.setText("shikeai20121015@gmail.com");
		mNameEt.setTextColor(Color.BLACK);
		mPasswordEt = new EditText(getActivity());
		mPasswordEt.setHint("Password");
		mPasswordEt.setText("474006");
		mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mPasswordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
		mPasswordEt.setTextColor(Color.BLACK);

		editors.addView(mNameEt);
		editors.addView(mPasswordEt);

		Button loginBtn = new Button(getActivity());
		loginBtn.setText("Login");
		loginBtn.setBackgroundColor(Color.TRANSPARENT);
		Button regBtn = new Button(getActivity());
		regBtn.setText("Register");
		regBtn.setBackgroundColor(Color.TRANSPARENT);
		LinearLayout buttons = new LinearLayout(getActivity());
		buttons.setOrientation(LinearLayout.HORIZONTAL);
		buttons.addView(loginBtn);
		buttons.addView(regBtn);
		editors.addView(buttons, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		((LinearLayout.LayoutParams) buttons.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;

		AsyncTaskCompat.executeParallel(new AsyncTask<Button, Bitmap[], Bitmap[]>() {
			private WeakReference<Button> mLoginBtnRef;
			private WeakReference<Button> mRegBtnRef;

			@Override
			protected Bitmap[] doInBackground(Button... params) {
				mLoginBtnRef = new WeakReference<>(params[0]);
				mRegBtnRef = new WeakReference<>(params[1]);
				Request request = new Request.Builder().url(LOGIN_BUTTON_URL).get().build();
				Bitmap[] bmp = new Bitmap[2];
				try {
					Response response = new OkHttpClient().newCall(request).execute();
					InputStream is = new BufferedInputStream(response.body().byteStream());
					bmp[0] = BitmapFactory.decodeStream(is);
					is.close();
				} catch (IOException e) {
				}

				request = new Request.Builder().url(REGISTER_BUTTON_URL).get().build();
				try {
					Response response = new OkHttpClient().newCall(request).execute();
					InputStream is = new BufferedInputStream(response.body().byteStream());
					bmp[1] = BitmapFactory.decodeStream(is);
					is.close();
				} catch (IOException e) {
				}
				return bmp;
			}

			@Override
			protected void onPostExecute(Bitmap[] bmp) {
				super.onPostExecute(bmp);
				if (bmp != null && mLoginBtnRef.get() != null) {
					mLoginBtnRef.get().setText("");
					mLoginBtnRef.get().setBackgroundDrawable(new BitmapDrawable(getResources(), bmp[0]));
				}

				if (bmp != null && mRegBtnRef.get() != null) {
					mRegBtnRef.get().setText("");
					mRegBtnRef.get().setBackgroundDrawable(new BitmapDrawable(getResources(), bmp[1]));
				}
			}
		}, loginBtn, regBtn);

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
							Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_SHORT).show();
							LoginDialog.this.dismissAllowingStateLoss();
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
		mRootVg.addView(editors, new LayoutParams(screenSize.Width, ViewGroup.LayoutParams.WRAP_CONTENT));
		MarginLayoutParams ml = ((MarginLayoutParams)editors.getLayoutParams());
		int margin = (int)Utils.convertPixelsToDp(getActivity(), 16f);
		ml.rightMargin = margin;
		ml.leftMargin = margin;
		ml.topMargin = margin;
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
