package com.limitart.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public final class FTPUtil {
	private FTPUtil() {
	}

	public static byte[] download(String url, int port, String username, String password, String remotePath,
			String fileName) throws IOException {
		FTPClient ftp = new FTPClient();
		ftp.setConnectTimeout(5000);
		try {
			ftp.connect(url, port);
			ftp.login(username, password);// 登录
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				throw new IOException("login fail!");
			}
			ftp.changeWorkingDirectory(remotePath);
			FTPFile[] fs = ftp.listFiles();
			for (FTPFile ff : fs) {
				if (ff.getName().equals(fileName)) {
					ByteArrayOutputStream is = new ByteArrayOutputStream();
					ftp.retrieveFile(ff.getName(), is);
					byte[] result = is.toByteArray();
					is.close();
					return result;
				}
			}

			ftp.logout();
		} finally {
			if (ftp.isConnected()) {
				ftp.disconnect();
			}
		}
		return null;
	}

	public static List<byte[]> download(String url, int port, String username, String password, String remotePath,
			String dirName, String filePattern) throws IOException {
		List<byte[]> result = new ArrayList<>();
		FTPClient ftp = new FTPClient();
		ftp.setConnectTimeout(5000);
		try {
			ftp.connect(url, port);
			ftp.login(username, password);// 登录
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				throw new IOException("login fail!");
			}
			ftp.changeWorkingDirectory(remotePath);
			ftp.changeWorkingDirectory(dirName);
			FTPFile[] fs = ftp.listFiles();
			for (FTPFile ff : fs) {
				if (ff.getName().endsWith("." + filePattern)) {
					ByteArrayOutputStream is = new ByteArrayOutputStream();
					ftp.retrieveFile(ff.getName(), is);
					result.add(is.toByteArray());
					is.close();
				}
			}

			ftp.logout();
		} finally {
			if (ftp.isConnected()) {
				ftp.disconnect();
			}
		}
		return result;
	}
}
