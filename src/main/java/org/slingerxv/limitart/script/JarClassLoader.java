package org.slingerxv.limitart.script;

import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader extends URLClassLoader {

	public JarClassLoader(URL[] urls) {
		super(urls);
	}

}
