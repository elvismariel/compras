package br.com.mariel.compras.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5Generator {

	public static String generate(String value) throws NoSuchAlgorithmException {
		// Create MD5 Hash
		MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
		digest.update(value.getBytes());
		byte messageDigest[] = digest.digest();

		// Create Hex String
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			String h = Integer.toHexString(0xFF & messageDigest[i]);
			while (h.length() < 2)
				h = "0" + h;
			hexString.append(h);
		}
		return hexString.toString();
	}
}
