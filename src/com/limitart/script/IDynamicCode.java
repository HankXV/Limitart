package com.limitart.script;

public abstract class IDynamicCode implements IScript<Integer> {
	@Override
	public Integer getScriptId() {
		return 0;
	}

	public abstract void execute();
}
