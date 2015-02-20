package com.osc4j.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import com.osc4j.Consts;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Util for authentication of app. Work for the requests need API Authentication.
 *
 * @author Xinyue Zhao
 */
public final class AuthUtil {
	private static final String ALGORITHM = "AES";
	private static final String CIPHER_ALGORITHM = "AES/CFB/PKCS5Padding";

	/**
	 * Encrypt by using AES.
	 * @param secret
	 * @return
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static String encrypt(String secret) throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		SecretKey secretKey = new SecretKeySpec(Sec.KEY, ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(Sec.IV));
		byte[] encrypted = cipher.doFinal(secret.getBytes());
		return Base64.encodeToString(encrypted, Base64.NO_WRAP);
	}

	/**
	 * Call, when need to test whether current user should be allowed to use API or not.
	 * <p/>
	 * @param context {@link android.content.Context}
	 * @return  {@code false} when the session is expired, or user has not login.
	 */
	public static boolean isLegitimate(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String session = prefs.getString(Consts.KEY_SESSION, null);
		String token = prefs.getString(Consts.KEY_ACCESS_TOKEN, null);
		if(TextUtils.isEmpty(session) || TextUtils.isEmpty(token)) {
			return false;
		}

		long expiresTime = prefs.getLong(Consts.KEY_EXPIRES, -1);
		if(System.currentTimeMillis() >= expiresTime) {
			return false;
		}
		return true;
	}
}
