/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import io.netty.util.CharsetUtil;

public final class FTPUtil {
	private FTPUtil() {
	}

	public static byte[] download(String url, int port, String username, String password, String remotePath,
			String fileName) throws IOException {
		FTPClient ftp = new FTPClient();
		ftp.setConnectTimeout(5000);
		ftp.setAutodetectUTF8(true);
		ftp.setCharset(CharsetUtil.UTF_8);
		ftp.setControlEncoding(CharsetUtil.UTF_8.name());
		try {
			ftp.connect(url, port);
			ftp.login(username, password);// 登录
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				throw new IOException("login fail!");
			}
			ftp.changeWorkingDirectory(remotePath);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			FTPFile[] fs = ftp.listFiles();
			for (FTPFile ff : fs) {
				if (ff.getName().equals(fileName)) {
					try (ByteArrayOutputStream is = new ByteArrayOutputStream();) {
						ftp.retrieveFile(ff.getName(), is);
						byte[] result = is.toByteArray();
						return result;
					}
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
		ftp.setAutodetectUTF8(true);
		ftp.setCharset(CharsetUtil.UTF_8);
		ftp.setControlEncoding(CharsetUtil.UTF_8.name());
		try {
			ftp.connect(url, port);
			ftp.login(username, password);// 登录
			if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				throw new IOException("login fail!");
			}
			ftp.changeWorkingDirectory(remotePath);
			ftp.changeWorkingDirectory(dirName);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			FTPFile[] fs = ftp.listFiles();
			for (FTPFile ff : fs) {
				if (ff.getName().endsWith("." + filePattern)) {
					try (ByteArrayOutputStream is = new ByteArrayOutputStream();) {
						ftp.retrieveFile(ff.getName(), is);
						result.add(is.toByteArray());
					}
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
