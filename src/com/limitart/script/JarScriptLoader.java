package com.limitart.script;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.limitart.script.define.AbstractScriptLoader;
import com.limitart.script.define.IDynamicCode;
import com.limitart.script.define.IScript;
import com.limitart.script.exception.ScriptException;
import com.limitart.util.FTPUtil;
import com.limitart.util.FileUtil;

public class JarScriptLoader<KEY> extends AbstractScriptLoader<KEY> {
	private AbstractScriptLoader<KEY> loadScriptsByJar(String jarName) throws ClassNotFoundException, IOException,
			InstantiationException, IllegalAccessException, ScriptException {
		File file = new File(jarName);
		if (!file.exists()) {
			throw new IOException("file not exist:" + jarName);
		}
		ConcurrentHashMap<KEY, IScript<KEY>> scriptMap_new = new ConcurrentHashMap<KEY, IScript<KEY>>();
		try (URLClassLoader classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() },
				Thread.currentThread().getContextClassLoader()); JarFile jarFile = new JarFile(file);) {
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					String className = entryName.replace("/", ".").substring(0, entryName.indexOf(".class"));
					Class<?> clazz = classLoader.loadClass(className);
					log.info("load class：" + className);
					if (clazz == null) {
						throw new ScriptException("class not found:" + className);
					}
					if (className.contains("$")) {
						continue;
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
							log.error("script id duplicated,source:" + scriptPath.get(scriptId).getName() + ",yours:"
									+ clazz.getName());
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
	 */
	public void reloadScriptJarLocal(String jarPath) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IOException, ScriptException {
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
	 */
	public void reloadScriptJarFTP(String ftpIp, int ftpPort, String username, String password, String resourceDir,
			String jarName) throws ScriptException, IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException {
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
