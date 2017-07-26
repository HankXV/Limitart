package org.slingerxv.limitart.net.http.codec;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Objects;

import org.slingerxv.limitart.collections.ConstraintMap;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.CharsetUtil;

public class QueryStringDecoderV2 {

	private static final int DEFAULT_MAX_PARAMS = 1024;

	private final Charset charset;
	private final String uri;
	private final boolean hasPath;
	private final int maxParams;
	private String path;
	private ConstraintMap<String> params;
	private int nParams;

	public QueryStringDecoderV2(String uri) {
		this(uri, HttpConstants.DEFAULT_CHARSET);
	}

	public QueryStringDecoderV2(String uri, boolean hasPath) {
		this(uri, HttpConstants.DEFAULT_CHARSET, hasPath);
	}

	public QueryStringDecoderV2(String uri, Charset charset) {
		this(uri, charset, true);
	}

	public QueryStringDecoderV2(String uri, Charset charset, boolean hasPath) {
		this(uri, charset, hasPath, DEFAULT_MAX_PARAMS);
	}

	public QueryStringDecoderV2(String uri, Charset charset, boolean hasPath, int maxParams) {
		if (maxParams <= 0) {
			throw new IllegalArgumentException("maxParams: " + maxParams + " (expected: a positive integer)");
		}
		this.uri = Objects.requireNonNull(uri, "getUri");
		this.charset = Objects.requireNonNull(charset, "charset");
		this.maxParams = maxParams;
		this.hasPath = hasPath;
	}

	public QueryStringDecoderV2(URI uri) {
		this(uri, HttpConstants.DEFAULT_CHARSET);
	}

	public QueryStringDecoderV2(URI uri, Charset charset) {
		this(uri, charset, DEFAULT_MAX_PARAMS);
	}

	public QueryStringDecoderV2(URI uri, Charset charset, int maxParams) {
		if (uri == null) {
			throw new NullPointerException("getUri");
		}
		if (charset == null) {
			throw new NullPointerException("charset");
		}
		if (maxParams <= 0) {
			throw new IllegalArgumentException("maxParams: " + maxParams + " (expected: a positive integer)");
		}

		String rawPath = uri.getRawPath();
		if (rawPath != null) {
			hasPath = true;
		} else {
			rawPath = "";
			hasPath = false;
		}
		// Also take care of cut of things like "http://localhost"
		this.uri = rawPath + (uri.getRawQuery() == null ? "" : '?' + uri.getRawQuery());

		this.charset = charset;
		this.maxParams = maxParams;
	}

	public String uri() {
		return uri;
	}

	public String path() {
		if (path == null) {
			if (!hasPath) {
				path = "";
			} else {
				int pathEndPos = uri.indexOf('?');
				path = decodeComponent(pathEndPos < 0 ? uri : uri.substring(0, pathEndPos), this.charset);
			}
		}
		return path;
	}

	public ConstraintMap<String> parameters() {
		if (params == null) {
			if (hasPath) {
				int pathEndPos = uri.indexOf('?');
				if (pathEndPos >= 0 && pathEndPos < uri.length() - 1) {
					decodeParams(uri.substring(pathEndPos + 1));
				} else {
					params = ConstraintMap.empty();
				}
			} else {
				if (uri.isEmpty()) {
					params = ConstraintMap.empty();
				} else {
					decodeParams(uri);
				}
			}
		}
		return params;
	}

	private void decodeParams(String s) {
		ConstraintMap<String> params = this.params = ConstraintMap.empty();
		nParams = 0;
		String name = null;
		int pos = 0; // Beginning of the unprocessed region
		int i; // End of the unprocessed region
		char c; // Current character
		for (i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (c == '=' && name == null) {
				if (pos != i) {
					name = decodeComponent(s.substring(pos, i), charset);
				}
				pos = i + 1;
				// http://www.w3.org/TR/html401/appendix/notes.html#h-B.2.2
			} else if (c == '&' || c == ';') {
				if (name == null && pos != i) {
					// We haven't seen an `=' so far but moved forward.
					// Must be a param of the form '&a&' so add it with
					// an empty value.
					if (!addParam(params, decodeComponent(s.substring(pos, i), charset), "")) {
						return;
					}
				} else if (name != null) {
					if (!addParam(params, name, decodeComponent(s.substring(pos, i), charset))) {
						return;
					}
					name = null;
				}
				pos = i + 1;
			}
		}

		if (pos != i) { // Are there characters we haven't dealt with?
			if (name == null) { // Yes and we haven't seen any `='.
				addParam(params, decodeComponent(s.substring(pos, i), charset), "");
			} else { // Yes and this must be the last value.
				addParam(params, name, decodeComponent(s.substring(pos, i), charset));
			}
		} else if (name != null) { // Have we seen a name without value?
			addParam(params, name, "");
		}
	}

	private boolean addParam(ConstraintMap<String> params, String name, String value) {
		if (nParams >= maxParams) {
			return false;
		}
		if (!params.containsKey(name)) {
			nParams++;
		}
		params.putString(name, value);

		return true;
	}

	/**
	 * Decodes a bit of an URL encoded by a browser.
	 * <p>
	 * This is equivalent to calling {@link #decodeComponent(String, Charset)} with
	 * the UTF-8 charset (recommended to comply with RFC 3986, Section 2).
	 * 
	 * @param s
	 *            The string to decode (can be empty).
	 * @return The decoded string, or {@code s} if there's nothing to decode. If the
	 *         string to decode is {@code null}, returns an empty string.
	 * @throws IllegalArgumentException
	 *             if the string contains a malformed escape sequence.
	 */
	public static String decodeComponent(final String s) {
		return decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
	}

	/**
	 * Decodes a bit of an URL encoded by a browser.
	 * <p>
	 * The string is expected to be encoded as per RFC 3986, Section 2. This is the
	 * encoding used by JavaScript functions {@code encodeURI} and
	 * {@code encodeURIComponent}, but not {@code escape}. For example in this
	 * encoding, &eacute; (in Unicode {@code U+00E9} or in UTF-8 {@code 0xC3 0xA9})
	 * is encoded as {@code %C3%A9} or {@code %c3%a9}.
	 * <p>
	 * This is essentially equivalent to calling
	 * {@link URLDecoder#decode(String, String) URLDecoder.decode(s,
	 * charset.name())} except that it's over 2x faster and generates less garbage
	 * for the GC. Actually this function doesn't allocate any memory if there's
	 * nothing to decode, the argument itself is returned.
	 * 
	 * @param s
	 *            The string to decode (can be empty).
	 * @param charset
	 *            The charset to use to decode the string (should really be
	 *            {@link CharsetUtil#UTF_8}.
	 * @return The decoded string, or {@code s} if there's nothing to decode. If the
	 *         string to decode is {@code null}, returns an empty string.
	 * @throws IllegalArgumentException
	 *             if the string contains a malformed escape sequence.
	 */
	public static String decodeComponent(final String s, final Charset charset) {
		if (s == null) {
			return "";
		}
		final int size = s.length();
		boolean modified = false;
		for (int i = 0; i < size; i++) {
			final char c = s.charAt(i);
			if (c == '%' || c == '+') {
				modified = true;
				break;
			}
		}
		if (!modified) {
			return s;
		}
		final byte[] buf = new byte[size];
		int pos = 0; // position in `buf'.
		for (int i = 0; i < size; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '+':
				buf[pos++] = ' '; // "+" -> " "
				break;
			case '%':
				if (i == size - 1) {
					throw new IllegalArgumentException("unterminated escape" + " sequence at end of string: " + s);
				}
				c = s.charAt(++i);
				if (c == '%') {
					buf[pos++] = '%'; // "%%" -> "%"
					break;
				}
				if (i == size - 1) {
					throw new IllegalArgumentException("partial escape" + " sequence at end of string: " + s);
				}
				c = decodeHexNibble(c);
				final char c2 = decodeHexNibble(s.charAt(++i));
				if (c == Character.MAX_VALUE || c2 == Character.MAX_VALUE) {
					throw new IllegalArgumentException("invalid escape sequence `%" + s.charAt(i - 1) + s.charAt(i)
							+ "' at index " + (i - 2) + " of: " + s);
				}
				c = (char) (c * 16 + c2);
				// Fall through.
			default:
				buf[pos++] = (byte) c;
				break;
			}
		}
		return new String(buf, 0, pos, charset);
	}

	/**
	 * Helper to decode half of a hexadecimal number from a string.
	 * 
	 * @param c
	 *            The ASCII character of the hexadecimal number to decode. Must be
	 *            in the range {@code [0-9a-fA-F]}.
	 * @return The hexadecimal value represented in the ASCII character given, or
	 *         {@link Character#MAX_VALUE} if the character is invalid.
	 */
	private static char decodeHexNibble(final char c) {
		if ('0' <= c && c <= '9') {
			return (char) (c - '0');
		} else if ('a' <= c && c <= 'f') {
			return (char) (c - 'a' + 10);
		} else if ('A' <= c && c <= 'F') {
			return (char) (c - 'A' + 10);
		} else {
			return Character.MAX_VALUE;
		}
	}
}
