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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * 编译后字节码文件管理器
 * 
 * @author hank
 *
 */
public class ForwardingClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	private List<String> classNames = new ArrayList<>();
	private List<JavaClassObject> javaClassObjects = new ArrayList<>();

	/**
	 * @param fileManager
	 */
	protected ForwardingClassFileManager(StandardJavaFileManager fileManager) {
		super(fileManager);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
			FileObject sibling) throws IOException {
		JavaClassObject javaClassObject = new JavaClassObject(className, kind);
		classNames.add(className);
		javaClassObjects.add(javaClassObject);
		return javaClassObject;
	}

	/**
	 * @return the javaClassObjects
	 */
	public List<JavaClassObject> getJavaClassObjects() {
		return javaClassObjects;
	}

	public List<String> getClassNames() {
		return classNames;
	}
}