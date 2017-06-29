package com.limitart.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.netty.util.CharsetUtil;

public class SecurityUtil {
	private static Md5PasswordUtil md5PasswordEncoder = new Md5PasswordUtil();
	private static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	public static boolean isBase64(byte[] bytes) {
		return Base64.isBase64(bytes);
	}

	public static String base64Decode2(String b64string) throws Exception {
		return new String(base64Decode(b64string.getBytes(CharsetUtil.UTF_8)));
	}

	public static String base64Encode2(String stringsrc) throws UnsupportedEncodingException {
		return new String(base64Encode(stringsrc.getBytes(CharsetUtil.UTF_8)), CharsetUtil.UTF_8);
	}

	public static byte[] base64Decode(byte[] base64) throws Exception {
		return Base64.decode(base64);
	}

	public static byte[] base64Encode(byte[] src) throws UnsupportedEncodingException {
		return Base64.encode(src);
	}

	public static String md5Encode32(String source) throws NoSuchAlgorithmException {
		byte[] strTemp = source.getBytes(CharsetUtil.UTF_8);
		MessageDigest mdTemp = MessageDigest.getInstance("MD5");
		mdTemp.update(strTemp);
		byte[] md = mdTemp.digest();
		int j = md.length;
		char str[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static String md5Encode32(String source, Object salt) {
		return md5PasswordEncoder.encodePassword(source, salt);
	}
}
