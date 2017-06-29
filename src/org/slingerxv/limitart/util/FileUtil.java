/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 文件操作
 *
 * @author hank
 */
public final class FileUtil {
    private FileUtil() {
    }

    /**
     * 遍历文件夹内所有文件
     *
     * @param root  根目录
     * @param types 文件筛选类型(java,class等)
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

    /**
     * 递归删除文件
     *
     * @param root
     */
    public static void deleteFile(File root) {
        if (root.isDirectory()) {
            File[] listFiles = root.listFiles();
            if (listFiles != null) {
                for (File temp : listFiles) {
                    if (temp == null) {
                        continue;
                    }
                    deleteFile(temp);
                }
            }
        }
        root.delete();
    }

    /**
     * 读取文件为ByteBuf(需要手动释放)
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static ByteBuf readFile(File file) throws IOException {
        ByteBuf buf = Unpooled.buffer();
        try (FileInputStream input = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                buf.writeBytes(buffer, 0, len);
            }
        }
        return buf;
    }

    /**
     * 读取文件为byte[]
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static byte[] readFile1(File file) throws IOException {
        try (FileInputStream input = new FileInputStream(file)) {
            return inputStream2ByteArray(input);
        }

    }

    /**
     * 输入流转为byte[]
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] inputStream2ByteArray(InputStream input) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        }
    }

    /**
     * 覆盖写新文件
     *
     * @param path
     * @param fileName
     * @param content
     * @throws IOException
     */
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
        if (!temp.exists()) {
            if (!temp.createNewFile()) {
                throw new IOException("create new file failed!");
            }
        }
        Files.write(temp.toPath(), content);
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
