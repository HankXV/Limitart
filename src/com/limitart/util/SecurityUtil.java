package com.limitart.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

	/**
	 * 数据压缩
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static byte[] gzipCompress(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gos = new GZIPOutputStream(byteArrayOutputStream);
		try {
			int count;
			byte[] temp = new byte[1024];
			while ((count = bais.read(temp, 0, temp.length)) != -1) {
				gos.write(temp, 0, count);
			}
			gos.finish();
			gos.flush();
			return byteArrayOutputStream.toByteArray();
		} finally {
			bais.close();
			gos.close();
			byteArrayOutputStream.close();
		}

	}

	/**
	 * 数据解压缩
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static byte[] gzipDecompress(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPInputStream gis = new GZIPInputStream(bais);
		try {
			int count;
			byte[] temp = new byte[1024];
			while ((count = gis.read(temp, 0, temp.length)) != -1) {
				baos.write(temp, 0, count);
			}
			baos.flush();
			return baos.toByteArray();
		} finally {
			baos.close();
			bais.close();
			gis.close();
		}
	}
}
