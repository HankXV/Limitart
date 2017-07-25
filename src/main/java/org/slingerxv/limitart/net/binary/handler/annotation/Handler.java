package org.slingerxv.limitart.net.binary.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.slingerxv.limitart.net.binary.message.Message;
import org.slingerxv.limitart.util.Beta;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Beta
public @interface Handler {
	Class<? extends Message> value();
}
