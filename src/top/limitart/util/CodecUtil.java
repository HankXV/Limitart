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
package top.limitart.util;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 编码相关
 *
 * @author hank
 * @version 2018/4/20 0020 11:06
 */
public class CodecUtil {
    public static byte[] toBase64(byte[] src) {
        return Base64.getEncoder().encode(src);
    }

    public static String toBase64Str(byte[] src) {
        return Base64.getEncoder().encodeToString(src);
    }

    public static byte[] fromBase64(byte[] src) {
        return Base64.getDecoder().decode(src);
    }

    public static String toMD5(String password) throws NoSuchAlgorithmException {
        return toMD5(password, "");
    }

    public static String toMD5(String password, String salt) throws NoSuchAlgorithmException {
        return toMD5((password + salt).getBytes(StandardCharsets.UTF_8));
    }

    public static String toMD5(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(b);
        StringBuilder ret = new StringBuilder(bytes.length << 1);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(Character.forDigit((bytes[i] >> 4) & 0xf, 16));
            ret.append(Character.forDigit(bytes[i] & 0xf, 16));
        }
        return ret.toString();
    }
}
