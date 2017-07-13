package org.slingerxv.limitart.script.define;

public abstract class IDynamicCode implements IScript<Integer> {
	@Override
	public Integer getScriptId() {
		return 0;
	}

	public abstract void execute();
}
