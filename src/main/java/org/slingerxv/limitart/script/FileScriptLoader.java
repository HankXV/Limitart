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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.groovy.control.CompilationFailedException;
import org.slingerxv.limitart.util.FileUtil;

import groovy.lang.GroovyClassLoader;

public class FileScriptLoader<KEY> extends AbstractScriptLoader<KEY> {

	/**
	 * 重载脚本
	 * 
	 * @param scriptId
	 * @throws IOException
	 * @throws CompilationFailedException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ScriptException
	 */
	public AbstractScriptLoader<KEY> reloadScript(KEY scriptId) throws CompilationFailedException, IOException,
			InstantiationException, IllegalAccessException, ScriptException {
		File file = scriptPath.get(scriptId);
		Objects.requireNonNull(file, "script id:" + scriptId + " does not exist!");
		try (GroovyClassLoader loader = new GroovyClassLoader()) {
			Class<?> parseClass = loader.parseClass(file);
			Object newInstance = parseClass.newInstance();
			if (!(newInstance instanceof IScript)) {
				throw new ScriptException("script file must implement IScript:" + file.getName());
			}
			@SuppressWarnings("unchecked")
			IScript<KEY> newScript = (IScript<KEY>) newInstance;
			scriptMap.put(scriptId, newScript);
			log.info("reload script success:" + file.getName());
		}
		return this;
	}

	/**
	 * 加载单个文件
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws ScriptException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public AbstractScriptLoader<KEY> loadScript(String filePath)
			throws IOException, ScriptException, InstantiationException, IllegalAccessException {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("file not exist!");
		}
		if (file.isDirectory()) {
			throw new ScriptException("script file is directory!");
		}
		String[] split = file.getName().split("[.]");
		if (split.length < 2) {
			throw new ScriptException("file name must has extension,like .java .groovy,yours:" + file.getName());
		}
		String type = split[1];
		ScriptFileType typeByValue = ScriptFileType.getTypeByValue(type);
		if (typeByValue == null) {
			throw new ScriptException("script type not supported" + type);
		}
		try (GroovyClassLoader loader = new GroovyClassLoader(ClassLoader.getSystemClassLoader())) {
			@SuppressWarnings("rawtypes")
			Class parseClass = loader.parseClass(file);
			Object newInstance = parseClass.newInstance();
			if (!(newInstance instanceof IScript)) {
				throw new ScriptException("script file must implement IScript:" + file.getName());
			}
			if (newInstance instanceof IDynamicCode) {
				throw new ScriptException("script file must not extend IDynamicCode:" + file.getName());
			}
			@SuppressWarnings("unchecked")
			IScript<KEY> script = (IScript<KEY>) newInstance;
			KEY scriptId = script.getScriptId();
			if (scriptMap.containsKey(scriptId)) {
				throw new ScriptException("script id(" + scriptId + ") duplicated,source:"
						+ scriptPath.get(scriptId).getName() + ",yours:" + file.getName());
			}
			scriptMap.put(scriptId, script);
			scriptPath.put(scriptId, file);
			log.info("compile script success:" + file.getName());
		}
		return this;
	}

	/**
	 * 加载一个目录下对应的全部脚本(不会加载IDynamicCode相关)
	 * 
	 * @param dir
	 * @param scriptTypes
	 * @return
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ScriptException
	 */
	public AbstractScriptLoader<KEY> loadScriptsBySourceDir(String dir, ScriptFileType... scriptTypes)
			throws IOException, InstantiationException, IllegalAccessException, ScriptException {
		Map<KEY, IScript<KEY>> scriptMap_new = new ConcurrentHashMap<>();
		Map<KEY, File> scriptPath_new = new ConcurrentHashMap<>();
		try (GroovyClassLoader loader = new GroovyClassLoader(ClassLoader.getSystemClassLoader());) {
			File dir_root = new File(dir);
			if (!dir_root.exists()) {
				throw new IOException("scripts root dir does not exist:" + dir);
			}
			if (!dir_root.isDirectory()) {
				throw new IOException("file is not dir:" + dir);
			}
			String[] types = null;
			if (scriptTypes != null && scriptTypes.length > 0) {
				types = new String[scriptTypes.length];
				for (int i = 0; i < scriptTypes.length; ++i) {
					types[i] = scriptTypes[i].getValue();
				}
			}
			List<File> result = FileUtil.getFiles(dir_root, types);
			for (File file : result) {
				Class<?> parseClass = loader.parseClass(file);
				Object newInstance = parseClass.newInstance();
				if (newInstance instanceof IScript) {
					if (newInstance instanceof IDynamicCode) {
						continue;
					}
					@SuppressWarnings("unchecked")
					IScript<KEY> script = (IScript<KEY>) newInstance;
					KEY scriptId = script.getScriptId();
					if (scriptMap_new.containsKey(scriptId)) {
						log.error("script id duplicated,source:" + scriptPath.get(scriptId).getName() + ",yours:"
								+ file.getName());
					} else {
						scriptMap_new.put(scriptId, script);
						scriptPath_new.put(scriptId, file);
						log.info("compile script success:" + file.getName());
					}
				} else {
					throw new ScriptException("script file must implement IScript:" + file.getName());
				}
			}
			this.scriptMap = scriptMap_new;
			this.scriptPath = scriptPath_new;
		}
		return this;
	}
}
