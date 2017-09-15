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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slingerxv.limitart.util.FTPUtil;
import org.slingerxv.limitart.util.FileUtil;

public class JarScriptLoader<KEY> extends AbstractScriptLoader<KEY> {

	private AbstractScriptLoader<KEY> loadScriptsByJar(String jarName)
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, ScriptException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		File file = new File(jarName);
		if (!file.exists()) {
			throw new IOException("file not exist:" + jarName);
		}
		try (JarClassLoader newLoader = new JarClassLoader(new URL[] { file.toURI().toURL() })) {
			log.info("create class loader:" + newLoader.getClass().getName() + ",parent:"
					+ newLoader.getParent().getClass().getName());
			log.info("current thread loader:" + Thread.currentThread().getContextClassLoader().getClass().getName());
			log.info("system class loader:" + ClassLoader.getSystemClassLoader().getClass().getName());
			Map<KEY, IScript<KEY>> scriptMap_new = new ConcurrentHashMap<KEY, IScript<KEY>>();
			try (JarFile jarFile = new JarFile(file)) {
				Enumeration<JarEntry> entrys = jarFile.entries();
				while (entrys.hasMoreElements()) {
					JarEntry jarEntry = entrys.nextElement();
					String entryName = jarEntry.getName();
					if (entryName.endsWith(".class")) {
						String className = entryName.replace("/", ".").substring(0, entryName.indexOf(".class"));
						Class<?> clazz = newLoader.loadClass(className);
						log.info("load class：" + className);
						if (clazz == null) {
							throw new ScriptException("class not found:" + className);
						}
						if (className.contains("$")) {
							continue;
						}
						Class<?> superclass = clazz.getSuperclass();
						if (!superclass.isInterface()) {
							log.warn(
									"CAUTION!!!!parent better be INTERFACE,if your script's parent is CLASS,reference field must be PUBLIC!!!");
						}
						Object newInstance = clazz.newInstance();
						if (newInstance instanceof IScript) {
							if (newInstance instanceof IDynamicCode) {
								continue;
							}
							@SuppressWarnings("unchecked")
							IScript<KEY> script = (IScript<KEY>) newInstance;
							KEY scriptId = script.getScriptId();
							if (scriptMap_new.containsKey(scriptId)) {
								log.error("script id duplicated,source:" + scriptPath.get(scriptId).getName()
										+ ",yours:" + clazz.getName());
							} else {
								scriptMap_new.put(scriptId, script);
								log.info("compile script success:" + clazz.getName());
							}
						} else {
							throw new ScriptException("script file must implement IScript:" + clazz.getName());
						}
					}
				}
				this.scriptMap = scriptMap_new;
			}
		}
		return this;
	}

	/**
	 * 加载本地Jar
	 * 
	 * @param jarPath
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 * @throws ScriptException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public void reloadScriptJarLocal(String jarPath)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, ScriptException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		log.info("start load local jar:" + jarPath);
		loadScriptsByJar(jarPath);
	}

	/**
	 * 远程FTP加载Jar
	 * 
	 * @param ftpIp
	 * @param ftpPort
	 * @param username
	 * @param password
	 * @param resourceDir
	 * @param jarName
	 * @throws ScriptException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public void reloadScriptJarFTP(String ftpIp, int ftpPort, String username, String password, String resourceDir,
			String jarName)
			throws ScriptException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		log.info("strat load remote jar:" + jarName);
		byte[] download = FTPUtil.download(ftpIp, ftpPort, username, password, resourceDir, jarName);
		if (download == null) {
			throw new ScriptException("download script file failed！");
		}
		log.info("download jar file success，file length：" + download.length);
		String tempDir = "temp";
		String tempJar = "script.limitart";
		String tempPath = tempDir + "/" + tempJar;
		File jarFile = new File(tempPath);
		FileUtil.writeNewFile(tempDir, tempJar, download);
		loadScriptsByJar(tempPath);
		jarFile.delete();
	}
}
