package com.osctweet4j.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import android.util.Base64;

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


	public static String encrypt(String secret) throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		SecretKey secretKey = new SecretKeySpec(Sec.KEY, ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(Sec.IV));
		byte[] encrypted = cipher.doFinal(secret.getBytes());
		return Base64.encodeToString(encrypted, Base64.NO_WRAP);
	}
}
