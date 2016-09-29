package com.dyn.admin.proxy;

import java.util.Map;

public interface Proxy {
	public Map<String, ?> getKeyBindings();

	public void init();

	public void renderGUI();
}