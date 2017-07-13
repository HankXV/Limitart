package org.slingerxv.limitart.script.constant;

public enum ScriptFileType {
	JAVA("java"), GROOVY("groovy"),;
	private String value;

	ScriptFileType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static ScriptFileType getTypeByValue(String value) {
		for (ScriptFileType type : ScriptFileType.values()) {
			if (type.getValue().equals(value)) {
				return type;
			}
		}
		return null;
	}
}
