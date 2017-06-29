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
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.util.FileUtil;

/**
 * 二进制方式加载类
 * 
 * @author hank
 *
 */
public class ByteCodeClassLoader extends ClassLoader {
	private static Logger log = LoggerFactory.getLogger(ByteCodeClassLoader.class);

	public ByteCodeClassLoader() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public ByteCodeClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * 通过二进制内容加载类
	 * 
	 * @param className
	 * @param content
	 * @return
	 */
	public Class<?> loadClass(String className, byte[] content) {
		return defineClass(className, content, 0, content.length);
	}

	/**
	 * 通过文件内容加载类
	 * 
	 * @param className
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Class<?> loadClass(String className, File file) throws IOException {
		byte[] readFile1 = FileUtil.readFile1(file);
		return loadClass(className, readFile1);
	}

	/**
	 * 通过输入流加载类
	 * 
	 * @param className
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public Class<?> loadClass(String className, InputStream input) throws IOException {
		byte[] inputStream2ByteArray = FileUtil.inputStream2ByteArray(input);
		return loadClass(className, inputStream2ByteArray);
	}

	/**
	 * 在内存中直接编译源文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Class<?> parseClass(File file) throws IOException {
		List<JavaFileObject> javaFileObjs = new ArrayList<>();
		JavaFileObject javaFileObj = new SourceCodeJavaFileObject(file);
		javaFileObjs.add(javaFileObj);
		return parseClass0(javaFileObjs);
	}

	/**
	 * 在内存中直接编译源文件
	 * 
	 * @param fileURI
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public Class<?> parseClass(URI fileURI, byte[] content) throws IOException {
		List<JavaFileObject> javaFileObjs = new ArrayList<>();
		JavaFileObject javaFileObj = new SourceCodeJavaFileObject(fileURI, content);
		javaFileObjs.add(javaFileObj);
		return parseClass0(javaFileObjs);
	}

	/**
	 * 在内存中直接编译源文件
	 * 
	 * @param fileURI
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public Class<?> parseClass(URI fileURI, InputStream input) throws IOException {
		List<JavaFileObject> javaFileObjs = new ArrayList<>();
		JavaFileObject javaFileObj = new SourceCodeJavaFileObject(fileURI, input);
		javaFileObjs.add(javaFileObj);
		return parseClass0(javaFileObjs);
	}

	private Class<?> parseClass0(List<JavaFileObject> fileObjs) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		try (ForwardingClassFileManager fileManager = new ForwardingClassFileManager(
				compiler.getStandardFileManager(diagnostics, null, null))) {
			List<String> options = new ArrayList<>();
			options.add("-encoding");
			options.add("UTF-8");
			options.add("-classpath");
			options.add(System.getProperty("java.class.path"));
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null,
					fileObjs);
			if (!task.call()) {
				diagnostics.getDiagnostics().forEach(item -> log.error(item.toString()));
				return null;
			}
			Class<?> mainClass = null;
			for (int i = 0; i < fileManager.getJavaClassObjects().size(); ++i) {
				JavaClassObject obj = fileManager.getJavaClassObjects().get(i);
				String className = fileManager.getClassNames().get(i);
				Class<?> loadClass = loadClass(className, obj.getBytes());
				if (Modifier.isPublic(loadClass.getModifiers()) && !className.contains("$")) {
					mainClass = loadClass;
					log.info("main class:" + className);
				}
				log.info("load class:" + className);
			}
			return mainClass;
		}
	}
}
