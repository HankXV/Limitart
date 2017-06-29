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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.netty.util.CharsetUtil;
import org.slingerxv.limitart.base.Conditions;

public final class SecurityUtil {
    private static final String ALGORITHM_MD5 = "MD5";
    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f'};
    public static final int NO_OPTIONS = 0;
    public static final int ENCODE = 1;
    public static final int DECODE = 0;
    public static final int DO_BREAK_LINES = 8;
    public static final int URL_SAFE = 16;
    public static final int ORDERED = 32;
    private static final byte[] _STANDARD_ALPHABET = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80,
            81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110,
            111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43,
            47};
    private static final byte[] _STANDARD_DECODABET = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6,
            7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28,
            29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};
    private static final byte[] _URL_SAFE_ALPHABET = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80,
            81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110,
            111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45,
            95};
    private static final byte[] _URL_SAFE_DECODABET = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6,
            7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28,
            29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};
    private static final byte[] _ORDERED_ALPHABET = {45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69,
            70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 95, 97, 98, 99, 100,
            101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121,
            122};
    private static final byte[] _ORDERED_DECODABET = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9,
            -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};

    private SecurityUtil() {
    }

    public static boolean isBase64(byte[] bytes) {
        try {
            base64Decode(bytes);
        } catch (InvalidBase64CharacterException e) {
            return false;
        }
        return true;
    }

    public static String base64Decode2(String b64string) throws InvalidBase64CharacterException {
        return new String(base64Decode(b64string.getBytes(CharsetUtil.UTF_8)));
    }

    public static String base64Encode2(String stringsrc) throws UnsupportedEncodingException {
        return new String(base64Encode(stringsrc.getBytes(CharsetUtil.UTF_8)), CharsetUtil.UTF_8);
    }

    public static byte[] base64Decode(byte[] base64) throws InvalidBase64CharacterException {
        return decode(base64, 0, base64.length, 0);
    }

    public static byte[] base64Encode(byte[] src) {
        return encodeBytesToBytes(src, 0, src.length, 0);
    }

    public static String md5Encode32(String source) throws NoSuchAlgorithmException {
        byte[] strTemp = source.getBytes(CharsetUtil.UTF_8);
        MessageDigest mdTemp = MessageDigest.getInstance(ALGORITHM_MD5);
        mdTemp.update(strTemp);
        byte[] md = mdTemp.digest();
        char[] hexEncode = hexEncode(md);
        return new String(hexEncode);
    }

    public static String md5Encode32(byte[] source) throws NoSuchAlgorithmException {
        MessageDigest mdTemp = MessageDigest.getInstance(ALGORITHM_MD5);
        mdTemp.update(source);
        byte[] md = mdTemp.digest();
        char[] hexEncode = hexEncode(md);
        return new String(hexEncode);
    }

    public static String encodePassword(String rawPass, Object salt) throws NoSuchAlgorithmException {
        String saltedPass = mergePasswordAndSalt(rawPass, salt, false);
        MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM_MD5);
        byte[] digest = messageDigest.digest(utf8Encode(saltedPass));
        return new String(hexEncode(digest));
    }

    public static boolean isPasswordValid(String encPass, String rawPass, Object salt) throws NoSuchAlgorithmException {
        String pass2 = encodePassword(rawPass, salt);
        byte[] expectedBytes = encPass == null ? null : utf8Encode(encPass);
        byte[] actualBytes = utf8Encode(pass2);
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

    private static String mergePasswordAndSalt(String password, Object salt, boolean strict) {
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

    public static char[] hexEncode(byte[] bytes) {
        int nBytes = bytes.length;
        char[] result = new char[2 * nBytes];
        int j = 0;
        for (byte aByte : bytes) {
            result[(j++)] = hexDigits[((0xF0 & aByte) >>> 4)];
            result[(j++)] = hexDigits[(0xF & aByte)];
        }
        return result;
    }

    public static byte[] hexDecode(CharSequence s) {
        int nChars = s.length();

        if (nChars % 2 != 0) {
            throw new IllegalArgumentException("Hex-encoded string must have an even number of characters");
        }

        byte[] result = new byte[nChars / 2];

        for (int i = 0; i < nChars; i += 2) {
            int msb = Character.digit(s.charAt(i), 16);
            int lsb = Character.digit(s.charAt(i + 1), 16);

            if ((msb < 0) || (lsb < 0)) {
                throw new IllegalArgumentException(
                        "Detected a Non-hex character at " + (i + 1) + " or " + (i + 2) + " position");
            }

            result[(i / 2)] = (byte) (msb << 4 | lsb);
        }
        return result;
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
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             GZIPOutputStream gos = new GZIPOutputStream(byteArrayOutputStream)) {
            int count;
            byte[] temp = new byte[1024];
            while ((count = bais.read(temp, 0, temp.length)) != -1) {
                gos.write(temp, 0, count);
            }
            gos.finish();
            gos.flush();
            return byteArrayOutputStream.toByteArray();
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
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPInputStream gis = new GZIPInputStream(bais)) {
            int count;
            byte[] temp = new byte[1024];
            while ((count = gis.read(temp, 0, temp.length)) != -1) {
                baos.write(temp, 0, count);
            }
            baos.flush();
            return baos.toByteArray();
        }
    }

    public static byte[] utf8Encode(CharSequence string) {
        try {
            ByteBuffer bytes = CharsetUtil.UTF_8.newEncoder().encode(CharBuffer.wrap(string));
            byte[] bytesCopy = new byte[bytes.limit()];
            System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());
            return bytesCopy;
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException("Encoding failed", e);
        }
    }

    public static String utf8Decode(byte[] bytes) {
        try {
            return CharsetUtil.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException("Decoding failed", e);
        }
    }

    private static byte[] getAlphabet(int options) {
        if ((options & 0x10) == 16) {
            return _URL_SAFE_ALPHABET;
        }
        if ((options & 0x20) == 32) {
            return _ORDERED_ALPHABET;
        }

        return _STANDARD_ALPHABET;
    }

    private static byte[] getDecodabet(int options) {
        if ((options & 0x10) == 16) {
            return _URL_SAFE_DECODABET;
        }
        if ((options & 0x20) == 32) {
            return _ORDERED_DECODABET;
        }

        return _STANDARD_DECODABET;
    }

    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset,
                                     int options) {
        byte[] ALPHABET = getAlphabet(options);

        int inBuff = ((numSigBytes > 0) ? source[srcOffset] << 24 >>> 8 : 0)
                | ((numSigBytes > 1) ? source[(srcOffset + 1)] << 24 >>> 16 : 0)
                | ((numSigBytes > 2) ? source[(srcOffset + 2)] << 24 >>> 24 : 0);

        switch (numSigBytes) {
            case 3:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[(destOffset + 1)] = ALPHABET[(inBuff >>> 12 & 0x3F)];
                destination[(destOffset + 2)] = ALPHABET[(inBuff >>> 6 & 0x3F)];
                destination[(destOffset + 3)] = ALPHABET[(inBuff & 0x3F)];
                return destination;
            case 2:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[(destOffset + 1)] = ALPHABET[(inBuff >>> 12 & 0x3F)];
                destination[(destOffset + 2)] = ALPHABET[(inBuff >>> 6 & 0x3F)];
                destination[(destOffset + 3)] = 61;
                return destination;
            case 1:
                destination[destOffset] = ALPHABET[(inBuff >>> 18)];
                destination[(destOffset + 1)] = ALPHABET[(inBuff >>> 12 & 0x3F)];
                destination[(destOffset + 2)] = 61;
                destination[(destOffset + 3)] = 61;
                return destination;
        }

        return destination;
    }

    private static byte[] encodeBytesToBytes(byte[] source, int off, int len, int options) {
        if (source == null) {
            throw new NullPointerException("Cannot serialize a null array.");
        }

        if (off < 0) {
            throw new IllegalArgumentException("Cannot have negative offset: " + off);
        }

        if (len < 0) {
            throw new IllegalArgumentException("Cannot have length offset: " + len);
        }

        if (off + len > source.length) {
            throw new IllegalArgumentException(
                    String.format("Cannot have offset of %d and length of %d with array of length %d",
                            new Object[]{off, len, source.length}));
        }

        boolean breakLines = (options & 0x8) > 0;

        int encLen = len / 3 * 4 + ((len % 3 > 0) ? 4 : 0);

        if (breakLines) {
            encLen += encLen / 76;
        }
        byte[] outBuff = new byte[encLen];

        int d = 0;
        int e = 0;
        int len2 = len - 2;
        int lineLength = 0;
        for (; d < len2; e += 4) {
            encode3to4(source, d + off, 3, outBuff, e, options);

            lineLength += 4;
            if ((breakLines) && (lineLength >= 76)) {
                outBuff[(e + 4)] = 10;
                ++e;
                lineLength = 0;
            }
            d += 3;
        }

        if (d < len) {
            encode3to4(source, d + off, len - d, outBuff, e, options);
            e += 4;
        }

        if (e <= outBuff.length - 1) {
            byte[] finalOut = new byte[e];
            System.arraycopy(outBuff, 0, finalOut, 0, e);

            return finalOut;
        }

        return outBuff;
    }

    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, int options) {
        Conditions.notNull(source, "Source array was null.");
        Conditions.notNull(destination, "Destination array was null.");
        if ((srcOffset < 0) || (srcOffset + 3 >= source.length)) {
            throw new IllegalArgumentException(
                    String.format("Source array with length %d cannot have offset of %d and still process four bytes.",
                            new Object[]{source.length, srcOffset}));
        }
        if ((destOffset < 0) || (destOffset + 2 >= destination.length)) {
            throw new IllegalArgumentException(String.format(
                    "Destination array with length %d cannot have offset of %d and still store three bytes.",
                    new Object[]{destination.length, destOffset}));
        }

        byte[] DECODABET = getDecodabet(options);

        if (source[(srcOffset + 2)] == 61) {
            int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18
                    | (DECODABET[source[(srcOffset + 1)]] & 0xFF) << 12;

            destination[destOffset] = (byte) (outBuff >>> 16);
            return 1;
        }

        if (source[(srcOffset + 3)] == 61) {
            int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18
                    | (DECODABET[source[(srcOffset + 1)]] & 0xFF) << 12
                    | (DECODABET[source[(srcOffset + 2)]] & 0xFF) << 6;

            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[(destOffset + 1)] = (byte) (outBuff >>> 8);
            return 2;
        }

        int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[(srcOffset + 1)]] & 0xFF) << 12
                | (DECODABET[source[(srcOffset + 2)]] & 0xFF) << 6 | DECODABET[source[(srcOffset + 3)]] & 0xFF;

        destination[destOffset] = (byte) (outBuff >> 16);
        destination[(destOffset + 1)] = (byte) (outBuff >> 8);
        destination[(destOffset + 2)] = (byte) outBuff;

        return 3;
    }

    private static byte[] decode(byte[] source, int off, int len, int options) throws InvalidBase64CharacterException {
        Conditions.notNull(source, "Cannot decode null source array.");
        if ((off < 0) || (off + len > source.length)) {
            throw new IllegalArgumentException(
                    String.format("Source array with length %d cannot have offset of %d and process %d bytes.",
                            new Object[]{source.length, off, len}));
        }

        if (len == 0) {
            return new byte[0];
        }
        if (len < 4) {
            throw new IllegalArgumentException(
                    "Base64-encoded string must have at least four characters, but length specified was " + len);
        }

        byte[] DECODABET = getDecodabet(options);

        int len34 = len * 3 / 4;
        byte[] outBuff = new byte[len34];
        int outBuffPosn = 0;

        byte[] b4 = new byte[4];
        int b4Posn = 0;
        int i;
        byte sbiDecode;

        for (i = off; i < off + len; ++i) {
            sbiDecode = DECODABET[(source[i] & 0xFF)];

            if (sbiDecode >= -5) {
                if (sbiDecode < -1)
                    continue;
                b4[(b4Posn++)] = source[i];
                if (b4Posn <= 3)
                    continue;
                outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, options);
                b4Posn = 0;

                if (source[i] != 61)
                    continue;
                break;
            }

            throw new InvalidBase64CharacterException(
                    String.format("Bad Base64 input character decimal %d in array position %d", source[i] & 0xFF, i));
        }

        byte[] out = new byte[outBuffPosn];
        System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
        return out;
    }
}
