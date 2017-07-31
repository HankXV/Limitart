package org.slingerxv.limitart.net.http.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.net.http.constant.ContentTypes;
import org.slingerxv.limitart.net.http.constant.RequestErrorCode;
import org.slingerxv.limitart.util.StringUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public final class HttpUtil {
	private HttpUtil() {
	}

	public static HttpResult post(String hostUrl, ConstraintMap<String> param, HashMap<String, String> requestProperty)
			throws IOException {
		HttpResult result = new HttpResult();
		URL url = new URL(hostUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		if (requestProperty != null) {
			for (Entry<String, String> entry : requestProperty.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		try (DataOutputStream ds = new DataOutputStream(conn.getOutputStream());) {
			ds.write(map2QueryParam(param).getBytes("utf-8"));
		}
		result.setStatus(conn.getResponseCode());
		InputStream input = null;
		if (result.getStatus() / 200 == 1) {
			input = conn.getInputStream();
		} else {
			input = conn.getErrorStream();
		}
		try (DataInputStream inputStream = new DataInputStream(input);
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();) {
			byte[] b = new byte[1024];
			int l;
			while ((l = inputStream.read(b)) > 0) {
				buffer.write(b, 0, l);
			}
			result.setResult(buffer.toByteArray());
			return result;
		} finally {
			conn.disconnect(); // 中断连接
		}
	}

	public static HttpResult get(String hostUrl) throws IOException {
		return get(hostUrl, null);
	}

	public static HttpResult get(String hostUrl, ConstraintMap<String> param) throws IOException {
		HttpResult result = new HttpResult();
		URL url;
		if (param == null) {
			url = new URL(hostUrl);
		} else {
			url = new URL(hostUrl + "?" + map2QueryParam(param));
		}
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		result.setStatus(conn.getResponseCode());
		InputStream input = null;
		if (result.getStatus() / 200 == 1) {
			input = conn.getInputStream();
		} else {
			input = conn.getErrorStream();
		}
		try (DataInputStream inputStream = new DataInputStream(input);
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();) {
			byte[] b = new byte[1024];
			int l;
			while ((l = inputStream.read(b)) > 0) {
				buffer.write(b, 0, l);
			}
			result.setResult(buffer.toByteArray());
			return result;
		} finally {
			conn.disconnect(); // 中断连接
		}
	}

	public static void sendResponse(Channel channel, HttpResponseStatus resultCode, String result, boolean isClose) {
		ByteBuf buf = Unpooled.copiedBuffer(result.getBytes(CharsetUtil.UTF_8));
		sendResponse(channel, resultCode, ContentTypes.text_plain, buf, isClose);
	}

	public static void sendResponse(Channel channel, HttpResponseStatus resultCode, ContentTypes contentType,
			ByteBuf result, boolean isClose) {
		channel.eventLoop().execute(() -> {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, resultCode, result);
			response.headers().add(HttpHeaderNames.CONTENT_TYPE, contentType.getValue());
			response.headers().add(HttpHeaderNames.CONTENT_LENGTH, result.readableBytes() + "");
			response.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION, "inline;filename=\"stupid.jpg\"");
			ChannelFuture future = channel.writeAndFlush(response);
			if (isClose) {
				future.addListener(ChannelFutureListener.CLOSE);
			}
		});
	}

	public static void sendResponse(Channel channel, HttpResponseStatus resultCode, String result) {
		sendResponse(channel, resultCode, result, true);
	}

	public static void sendResponseError(Channel channel, RequestErrorCode errorCode, String others) {
		sendResponse(channel, HttpResponseStatus.BAD_GATEWAY, "ErrorCode:" + errorCode.getValue() + " Info:" + others,
				true);
	}

	public static void sendResponseError(Channel channel, RequestErrorCode errorCode) {
		sendResponseError(channel, errorCode, errorCode.toString());
	}

	public static String map2QueryParam(ConstraintMap<String> map) {
		StringBuilder queryParamBuffer = new StringBuilder();
		map.foreach((k, v) -> {
			queryParamBuffer.append(k).append("=").append(v.toString()).append("&");
		});
		if (queryParamBuffer.length() > 0) {
			queryParamBuffer.deleteCharAt(queryParamBuffer.length() - 1);
		}
		return queryParamBuffer.toString();
	}

	public static ConstraintMap<String> queryParam2Map(String queryParam) throws Exception {
		ConstraintMap<String> map = ConstraintMap.empty();
		if (!StringUtil.isEmptyOrNull(queryParam)) {
			String[] split2 = queryParam.split("[&]");
			for (String temp : split2) {
				String[] split3 = temp.split("[=]");
				if (split3.length == 2) {
					map.putString(split3[0], split3[1]);
				}
			}
		}
		return map;
	}
}
