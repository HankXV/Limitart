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
package ${_package};

import top.limitart.net.binary.*;

/**
 * ${_explain}
 * 
 * @author limitart
 *
 */
public class ${_name} {
	private static final byte PACKAGE_ID = ${_id};

<#list _metas as _meta>
   	/**
	 * ${_meta._explain}
	 *
	 * @author limitart
	 *
	 */
	public static class ${_meta._name} extends BinaryMeta {

	<#list _meta._fields as _field>
		<#if (_field._isList==1)>
		/**
		 * ${_field._explain}
		 */
		public java.util.List<${_field._type}> ${_field._name} = new java.util.ArrayList<>();
		<#else>
		/**
		 * ${_field._explain}
		 */
		public ${_field._type} ${_field._name};
		</#if>
	</#list>
	}
</#list>

<#list _messages as _message>
	/**
	 * ${_message._explain}
	 *
	 * @author limitart
	 *
	 */
	public static class ${_message._name} extends BinaryMessage {

	<#list _message._fields as _field>
		<#if (_field._isList==1)>
		/**
		 * ${_field._explain}
		 */
		public java.util.List<${_field._type}> ${_field._name} = new java.util.ArrayList<>();
		<#else>
		/**
		 * ${_field._explain}
		 */
		public ${_field._type} ${_field._name};
		</#if>
	</#list>

		@Override
		public short messageID() {
			return BinaryMessages.createID(PACKAGE_ID, ${_message._messageID});
		}

	}
</#list>
}
