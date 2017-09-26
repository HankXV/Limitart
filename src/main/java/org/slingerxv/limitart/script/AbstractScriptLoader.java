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

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器脚本管理
 * 
 * @author hank
 *
 * @param <KEY>
 *            脚本Id
 */
public abstract class AbstractScriptLoader<KEY> {
	protected static final Logger log = LoggerFactory.getLogger(AbstractScriptLoader.class);
	protected Map<KEY, IScript<KEY>> scriptMap = new ConcurrentHashMap<>();
	protected Map<KEY, File> scriptPath = new ConcurrentHashMap<>();

	public void foreach(BiConsumer<? super KEY, ? super IScript<KEY>> action) {
		scriptMap.forEach(action);
	}

	/**
	 * 获取脚本
	 * 
	 * @param scriptId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends IScript<KEY>> T getScript(KEY scriptId) {
		return (T) scriptMap.get(scriptId);
	}
}
