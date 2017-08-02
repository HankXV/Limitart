package org.slingerxv.limitart.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public final class FileUtil {
	private FileUtil() {
	}

	/**
	 * 遍历文件夹内所有文件
	 *
	 * @param root
	 *            根目录
	 * @param result
	 *            接收结果的列表
	 * @param type
	 *            文件筛选类型(java,class等)
	 */
	public static List<File> getFiles(File root, String... types) {
		List<File> result = new ArrayList<>();
		if (root.isDirectory()) {
			File[] listFiles = root.listFiles();
			if (listFiles == null) {
				return result;
			}
			for (File temp : listFiles) {
				if (temp == null) {
					continue;
				}
				result.addAll(getFiles(temp, types));
			}
		} else {
			boolean filter = false;
			if (types != null && types.length > 0) {
				for (String type : types) {
					if (getFileNameExtention(root.getName()).equals(type)) {
						filter = true;
						break;
					}
				}
			} else {
				filter = true;
			}
			if (filter) {
				result.add(root);
			}
		}
		return result;
	}

	public static ByteBuf readFile(File file) throws IOException {
		ByteBuf buf = PooledByteBufAllocator.DEFAULT.ioBuffer();
		try (FileInputStream input = new FileInputStream(file)) {
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = input.read(buffer)) != -1) {
				buf.writeBytes(buffer, 0, len);
			}
		}
		return buf;
	}

	public static void writeNewFile(String path, String fileName, byte[] content) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				throw new IOException("make dir failed!");
			}
		} else {
			if (!file.isDirectory()) {
				return;
			}
		}
		File temp = new File(path + "//" + fileName);
		temp.deleteOnExit();
		if (!temp.createNewFile()) {
			throw new IOException("create new file failed!");
		}
		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(temp))) {
			out.write(content);
		}
	}

	/**
	 * 获取文件扩展名
	 *
	 * @param fileName
	 * @return
	 */
	public static String getFileNameExtention(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
}
