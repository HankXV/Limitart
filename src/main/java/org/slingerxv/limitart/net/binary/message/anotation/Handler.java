package org.slingerxv.limitart.net.binary.message.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.slingerxv.limitart.net.binary.message.Message;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handler {
	Class<? extends Message> value();
}
