package com.limitart.script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import com.limitart.script.constant.ScriptFileType;
import com.limitart.script.exception.ScriptException;
import com.limitart.util.FileUtil;

import groovy.lang.GroovyClassLoader;

/**
 * 服务器脚本管理
 */
public class ScriptLoader<KEY> {
	private static final Logger log = LogManager.getLogger();
	private ConcurrentHashMap<KEY, IScript<KEY>> scriptMap = new ConcurrentHashMap<KEY, IScript<KEY>>();
	private ConcurrentHashMap<KEY, File> scriptPath = new ConcurrentHashMap<KEY, File>();
	private AtomicLong dynamicCodeCount = new AtomicLong(100000);
	// 1 not 2 yes
	private int isJar = 0;

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

	/**
	 * 执行一段继承了IDynamicCode的代码
	 * 
	 * @param path
	 * @return
	 * @throws ScriptException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @see IDynamicCode
	 */
	public ScriptLoader<KEY> executeCommand(String path)
			throws ScriptException, IOException, InstantiationException, IllegalAccessException {
		File file = new File(path);
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
			throw new ScriptException("script type not supported:" + type);
		}
		GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
		try {
			@SuppressWarnings("rawtypes")
			Class parseClass = loader.parseClass(file);
			Object newInstance = parseClass.newInstance();
			if (!(newInstance instanceof IDynamicCode)) {
				throw new ScriptException("class must extends IDynamicCode");
			}
			IDynamicCode temp = (IDynamicCode) newInstance;
			log.info("compile code success,start executing...");
			temp.execute();
			log.info("done!");
		} finally {
			loader.close();
		}
		return this;
	}

	/**
	 * 执行几串简单的命令
	 * 
	 * @param importList
	 * @param commandLines
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 * @throws ScriptException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public ScriptLoader<KEY> executeCommand(List<String> importList, String commandLines) throws InstantiationException,
			IllegalAccessException, IOException, ScriptException, IllegalArgumentException, InvocationTargetException {
		StringBuilder importBuffer = new StringBuilder();
		if (importList != null) {
			for (String temp : importList) {
				importBuffer.append("import " + temp + ";");
			}
		}
		String result = importBuffer.toString()
				+ "import com.limitart.script.IDynamicCode; public class DynamicCodeProxy"
				+ dynamicCodeCount.getAndIncrement() + " extends IDynamicCode {" + " public void execute() {"
				+ commandLines + "}" + "}";
		GroovyClassLoader loader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
		try {
			@SuppressWarnings("rawtypes")
			Class parseClass = loader.parseClass(result);
			IDynamicCode newInstance = (IDynamicCode) parseClass.newInstance();
			log.info("compile code success,start executing...");
			newInstance.execute();
			log.info("done!");
		} finally {
			loader.close();
		}
		return this;
	}

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
	public ScriptLoader<KEY> reloadScript(KEY scriptId) throws CompilationFailedException, IOException,
			InstantiationException, IllegalAccessException, ScriptException {
		if (isJar == 2) {
			throw new ScriptException("this script loader is a jarloader!");
		}
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
		isJar = 1;
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
	public ScriptLoader<KEY> loadScript(String filePath)
			throws IOException, ScriptException, InstantiationException, IllegalAccessException {
		if (isJar == 2) {
			throw new ScriptException("this script loader is a jarloader!");
		}
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
		isJar = 1;
		return this;
	}

	/**
	 * 加载jar包
	 * 
	 * @param jarFile
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ScriptException
	 */
	public ScriptLoader<KEY> loadScriptsByJar(String jarName) throws ClassNotFoundException, IOException,
			InstantiationException, IllegalAccessException, ScriptException {
		if (isJar == 1) {
			throw new ScriptException("this script loader is a fileloader!");
		}
		File file = new File(jarName);
		if (!file.exists()) {
			throw new IOException("file not exist:" + jarName);
		}
		JarFile jarFile = new JarFile(file);
		URLClassLoader classLoader = new URLClassLoader(new URL[] { file.toURI().toURL() },
				Thread.currentThread().getContextClassLoader());
		ConcurrentHashMap<KEY, IScript<KEY>> scriptMap_new = new ConcurrentHashMap<KEY, IScript<KEY>>();
		try {
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					String className = entryName.replace("/", ".").substring(0, entryName.indexOf(".class"));
					Class<?> clazz = classLoader.loadClass(className);
					log.info("加载class文件：" + className);
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
		} finally {
			classLoader.close();
			jarFile.close();
		}
		isJar = 2;
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
	public ScriptLoader<KEY> loadScriptsBySourceDir(String dir, ScriptFileType... scriptTypes)
			throws IOException, InstantiationException, IllegalAccessException, ScriptException {
		if (isJar == 2) {
			throw new ScriptException("this script loader is a jarloader!");
		}
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
		isJar = 1;
		return this;
	}
}
