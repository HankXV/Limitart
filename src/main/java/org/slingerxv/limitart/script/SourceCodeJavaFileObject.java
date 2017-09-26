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

import javax.tools.SimpleJavaFileObject;

import org.slingerxv.limitart.util.FileUtil;

/**
 * 源码型JavaFileObject
 * 
 * @author hank
 *
 */
public class SourceCodeJavaFileObject extends SimpleJavaFileObject {

	private String code;

	public SourceCodeJavaFileObject(File sourceFile) throws FileNotFoundException, IOException {
		super(sourceFile.toURI(), Kind.SOURCE);
		this.code = new String(FileUtil.readFile1(sourceFile));
	}

	@Override
	public String getCharContent(boolean ignoreEncodingErrors) {
		return this.code;
	}
}
