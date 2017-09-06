/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.i18n;

/**
 * U3D语言枚举
 * 
 * @author hank
 *
 */
public enum UnityLanguages {
	/**
	 * 
	 */
	Afrikaans(0),
	/**
	 * 
	 */
	Arabic(1),
	/**
	 * 
	 */
	Basque(2),
	/**
	 * 
	 */
	Belarusian(3),
	/**
	 * 
	 */
	Bulgarian(4),
	/**
	 * 
	 */
	Catalan(5),
	/**
	 * 
	 */
	Chinese(6),
	/**
	 * 
	 */
	Czech(7),
	/**
	 * 
	 */
	Danish(8),
	/**
	 * 
	 */
	Dutch(9),
	/**
	 * 
	 */
	English(10),
	/**
	 * 
	 */
	Estonian(11),
	/**
	 * 
	 */
	Faroese(12),
	/**
	 * 
	 */
	Finnish(13),
	/**
	 * 
	 */
	French(14),
	/**
	 * 
	 */
	German(15),
	/**
	 * 
	 */
	Greek(16),
	/**
	 * 
	 */
	Hebrew(17),
	/**
	 * 
	 */
	Hugarian(18),
	/**
	 * 
	 */
	Hungarian(18),
	/**
	 * 
	 */
	Icelandic(19),

	/**
	 * 
	 */
	Indonesian(20),
	/**
	* 
	*/
	Italian(21),
	/**
	* 
	*/
	Japanese(22),
	/**
	* 
	*/
	Korean(23),
	/**
	* 
	*/
	Latvian(24),
	/**
	* 
	*/
	Lithuanian(25),
	/**
	* 
	*/
	Norwegian(26),
	/**
	* 
	*/
	Polish(27),
	/**
	* 
	*/
	Portuguese(28),
	/**
	* 
	*/
	Romanian(29),
	/**
	* 
	*/
	Russian(30),
	/**
	* 
	*/
	SerboCroatian(31),
	/**
	* 
	*/
	Slovak(32),
	/**
	* 
	*/
	Slovenian(33),
	/**
	* 
	*/
	Spanish(34),
	/**
	* 
	*/
	Swedish(35),
	/**
	* 
	*/
	Thai(36),
	/**
	* 
	*/
	Turkish(37),
	/**
	* 
	*/
	Ukrainian(38),
	/**
	* 
	*/
	Vietnamese(39),
	/**
	* 
	*/
	ChineseSimplified(40),
	/**
	* 
	*/
	ChineseTraditional(41),
	/**
	* 
	*/
	Unknown(42);
	private int value;

	private UnityLanguages(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static UnityLanguages getTypeByValue(int value) {
		for (UnityLanguages lan : values()) {
			if (lan.getValue() == value) {
				return lan;
			}
		}
		return null;
	}
}
