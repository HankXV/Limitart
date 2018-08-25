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
 * 脚本编译测试
 * 
 * @author hank
 *
 */
public class ScriptSourceDemo implements Runnable {

	private class ScriptSourceInnerPrivate {
		public void say() {
			System.out.println("hello inner");
		}
	}

	@Override
	public void run() {
		System.out.println("hello limitart");
		((Runnable) () -> System.out.println("inner")).run();
		ScriptSourceInnerPrivate temp = new ScriptSourceInnerPrivate();
		temp.say();
	}

	public class ScriptSourceDemoInnerPublic {

	}
}

class ScriptSourceDemoPrivate {
}
