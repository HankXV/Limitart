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
package org.slingerxv.limitart.script;

import org.slingerxv.limitart.base.LimitartNonRuntimeException;

/**
 * 脚本构造错误
 * 
 * @author hank
 *
 */
public class ScriptConstructException extends LimitartNonRuntimeException {

	private static final long serialVersionUID = 1L;

	public ScriptConstructException(Class<?> clazz) {
		super("script file must implement " + Script.class.getName() + ":" + clazz.getName());
	}
}
