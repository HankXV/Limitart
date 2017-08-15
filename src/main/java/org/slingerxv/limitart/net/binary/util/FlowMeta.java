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
package org.slingerxv.limitart.net.binary.util;

import org.slingerxv.limitart.funcs.Func;
import org.slingerxv.limitart.net.binary.message.Message;

/**
 * @author hank
 *
 */
public class FlowMeta implements Func<Class<? extends Message>> {
	private Class<? extends Message> clazz;
	private long value;

	@Override
	public Class<? extends Message> run() {
		return clazz;
	}

	public Class<? extends Message> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends Message> clazz) {
		this.clazz = clazz;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "FlowMeta [clazz=" + clazz + ", value=" + value + "]";
	}

}
