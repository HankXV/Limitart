package com.limitart.script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.groovy.control.CompilationFailedException;

import com.limitart.script.constant.ScriptFileType;
import com.limitart.script.define.AbstractScriptLoader;
import com.limitart.script.define.IDynamicCode;
import com.limitart.script.define.IScript;
import com.limitart.script.exception.ScriptException;
import com.limitart.util.FileUtil;

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
		if (file == null) {
			throw new NullPointerException("script id:" + scriptId + " does not exist!");
		}
		GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
		try {
			Class<?> parseClass = loader.parseClass(file);
			Object newInstance = parseClass.newInstance();
			if (!(newInstance instanceof IScript)) {
				throw new ScriptException("script file must implement IScript:" + file.getName());
			}
			@SuppressWarnings("unchecked")
			IScript<KEY> newScript = (IScript<KEY>) newInstance;
			scriptMap.put(scriptId, newScript);
			log.info("reload script success:" + file.getName());
		} finally {
			loader.close();
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
		GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
		try {
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
		} finally {
			loader.close();
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
		ConcurrentHashMap<KEY, IScript<KEY>> scriptMap_new = new ConcurrentHashMap<KEY, IScript<KEY>>();
		ConcurrentHashMap<KEY, File> scriptPath_new = new ConcurrentHashMap<KEY, File>();
		GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
		try {
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
		} finally {
			try {
				loader.close();
			} catch (IOException e) {
				log.error(e, e);
			}
		}
		return this;
	}
}
