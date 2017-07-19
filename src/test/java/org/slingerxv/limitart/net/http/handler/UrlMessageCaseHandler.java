package org.slingerxv.limitart.net.http.handler;

import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.net.http.message.UrlMessageCase;

public class UrlMessageCaseHandler implements HttpHandler<UrlMessageCase> {

	@Override
	public ConstraintMap<String> doServer(UrlMessageCase msg) {
		System.out.println(msg);
		return null;
	}
}
