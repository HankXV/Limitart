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
package top.limitart.script;

/**
 * 脚本数据
 * 
 * @author hank
 *
 */
public class ScriptData<KEY> {
	// 脚本实例
	private Script<KEY> scriptInstance;
	// 文件内容MD5
	private String codeMD5;
	// 源码文件路径
	private String filePath;

	/**
	 * 初始化
	 */
	public ScriptData(Script<KEY> scriptInstance, String codeMD5, String filePath) {
		if (scriptInstance != null) {
			this.scriptInstance = scriptInstance;
		}
		if (codeMD5 != null) {
			this.codeMD5 = codeMD5;
		}
		if (filePath != null) {
			this.filePath = filePath;
		}
	}

	/**
	 * 替换实现
	 * 
	 * @param scriptInstance
	 * @param codeMD5
	 */
	public void replace(Script<KEY> scriptInstance, String codeMD5) {
		this.scriptInstance = scriptInstance;
		this.codeMD5 = codeMD5;
	}

	/**
	 * @return the scriptInstance
	 */
	public Script<KEY> getScriptInstance() {
		return scriptInstance;
	}

	/**
	 * @return the codeMD5
	 */
	public String getCodeMD5() {
		return codeMD5;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}
}
