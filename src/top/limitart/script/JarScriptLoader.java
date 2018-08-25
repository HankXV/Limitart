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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * jar包加载器
 *
 * @param <KEY>
 * @author hank
 */
public class JarScriptLoader<KEY> extends AbstractScriptLoader<KEY> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JarScriptLoader.class);

    /**
     * 加载jar包
     *
     * @param jarPath
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws ScriptConstructException
     * @throws ScriptKeyDuplicatedException
     */
    public void loadScriptsByJar(String jarPath) throws ClassNotFoundException, IOException, InstantiationException,
            IllegalAccessException, SecurityException, IllegalArgumentException,
            ScriptConstructException, ScriptKeyDuplicatedException {
        File file = new File(jarPath);
        if (!file.exists()) {
            throw new IOException("file not exist:" + jarPath);
        }
        try (JarClassLoader newLoader = new JarClassLoader(new URL[]{file.toURI().toURL()})) {
            LOGGER.info("create class loader:" + newLoader.getClass().getName() + ",parent:"
                    + newLoader.getParent().getClass().getName());
            LOGGER.info("current thread loader:" + Thread.currentThread().getContextClassLoader().getClass().getName());
            LOGGER.info("system class loader:" + ClassLoader.getSystemClassLoader().getClass().getName());
            try (JarFile jarFile = new JarFile(file)) {
                Enumeration<JarEntry> entrys = jarFile.entries();
                while (entrys.hasMoreElements()) {
                    JarEntry jarEntry = entrys.nextElement();
                    String entryName = jarEntry.getName();
                    if (!entryName.endsWith(".class")) {
                        continue;
                    }
                    String className = entryName.replace("/", ".").substring(0, entryName.indexOf(".class"));
                    Class<?> clazz = newLoader.loadClass(className);
                    LOGGER.info("load class：" + className);
                    if (clazz == null) {
                        throw new ClassNotFoundException(className);
                    }
                    if (className.contains("$")) {
                        continue;
                    }
                    Class<?> superclass = clazz.getSuperclass();
                    if (!superclass.isInterface()) {
                        LOGGER.warn(
                                "CAUTION!!!!parent better be INTERFACE,if your script's parent is CLASS,reference field must be PUBLIC!!!");
                    }
                    Object newInstance = clazz.newInstance();
                    if (!(newInstance instanceof Script)) {
                        throw new ScriptConstructException(clazz);
                    }
                    @SuppressWarnings("unchecked")
                    Script<KEY> script = (Script<KEY>) newInstance;
                    registerScriptData(script, null, entryName);
                }
            }
        }
    }
}
