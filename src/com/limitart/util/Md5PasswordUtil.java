package com.limitart.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SpringMD5密码工具
 * 
 * @author hank
 *
 */
public class Md5PasswordUtil {

	private final String algorithm = "MD5";
	private int iterations = 1;

	public String encodePassword(String rawPass, Object salt) {
		String saltedPass = mergePasswordAndSalt(rawPass, salt, false);

		MessageDigest messageDigest = getMessageDigest();

		byte[] digest = messageDigest.digest(Utf8.encode(saltedPass));

		for (int i = 1; i < this.iterations; ++i) {
			digest = messageDigest.digest(digest);
		}
		return new String(Hex.encode(digest));
	}

	private final MessageDigest getMessageDigest() throws IllegalArgumentException {
		try {
			return MessageDigest.getInstance(this.algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm [" + this.algorithm + "]");
		}
	}

	public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
		String pass1 = "" + encPass;
		String pass2 = encodePassword(rawPass, salt);
		byte[] expectedBytes = pass1 == null ? null : Utf8.encode(pass1);
		byte[] actualBytes = pass2 == null ? null : Utf8.encode(pass2);
		int expectedLength = (expectedBytes == null) ? -1 : expectedBytes.length;
		int actualLength = (actualBytes == null) ? -1 : actualBytes.length;
		int result = (expectedLength == actualLength) ? 0 : 1;
		for (int i = 0; i < actualLength; ++i) {
			byte expectedByte = (expectedLength <= 0) ? 0 : expectedBytes[(i % expectedLength)];
			byte actualByte = actualBytes[(i % actualLength)];
			result |= expectedByte ^ actualByte;
		}
		return (result == 0);
	}
	//
	// private String[] demergePasswordAndSalt(String mergedPasswordSalt) {
	// if ((mergedPasswordSalt == null) || ("".equals(mergedPasswordSalt))) {
	// throw new IllegalArgumentException("Cannot pass a null or empty String");
	// }
	//
	// String password = mergedPasswordSalt;
	// String salt = "";
	//
	// int saltBegins = mergedPasswordSalt.lastIndexOf("{");
	//
	// if ((saltBegins != -1) && (saltBegins + 1 < mergedPasswordSalt.length()))
	// {
	// salt = mergedPasswordSalt.substring(saltBegins + 1,
	// mergedPasswordSalt.length() - 1);
	// password = mergedPasswordSalt.substring(0, saltBegins);
	// }
	// return new String[] { password, salt };
	// }

	private String mergePasswordAndSalt(String password, Object salt, boolean strict) {
		if (password == null) {
			password = "";
		}

		if ((strict) && (salt != null)
				&& (((salt.toString().lastIndexOf("{") != -1) || (salt.toString().lastIndexOf("}") != -1)))) {
			throw new IllegalArgumentException("Cannot use { or } in salt.toString()");
		}

		if ((salt == null) || ("".equals(salt))) {
			return password;
		}

		return password + "{" + salt.toString() + "}";
	}
}