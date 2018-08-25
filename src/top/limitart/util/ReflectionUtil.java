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
package top.limitart.util;

import top.limitart.base.Test1;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 反射工具
 *
 * @author hank
 */
public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    /**
     * 通过反射字段拷贝
     *
     * @param oldOne
     * @param newOne
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void copyBean(Object oldOne, Object newOne)
            throws IllegalArgumentException, IllegalAccessException {
        Field[] declaredFields = oldOne.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            field.set(newOne, field.get(oldOne));
        }
    }

    public static List<Field> getFields(Class<?> clazz, boolean isSuper) {
        return getFields(clazz, isSuper, field -> !isStatic(field));
    }

    /**
     * 获取类所有字段
     *
     * @param clazz
     * @param isSuper 是否迭代检查超类
     * @return
     */
    public static List<Field> getFields(Class<?> clazz, boolean isSuper, Test1<Field> filter) {
        List<Field> list = new ArrayList<>();
        Class<?> nextClass = clazz;
        do {
            Field[] declaredFields = nextClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (!filter.test(field)) {
                    continue;
                }
                list.add(field);
            }
            nextClass = nextClass.getSuperclass();
        } while (nextClass != Object.class && isSuper);
        return list;
    }

    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    public static boolean isPrivate(Field field) {
        return Modifier.isPrivate(field.getModifiers());
    }

    public static boolean isPublic(Field field) {
        return Modifier.isPublic(field.getModifiers());
    }

    public static boolean isTransient(Field field) {
        return Modifier.isTransient(field.getModifiers());
    }

    public static boolean isProtected(Field field) {
        return Modifier.isProtected(field.getModifiers());
    }

    public static boolean isFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }

    public static boolean isVolatile(Field field) {
        return Modifier.isVolatile(field.getModifiers());
    }

    public static List<Class<?>> getClassesBySuperClass(String packageName, Class<?> superClass)
            throws IOException, ReflectiveOperationException {
        return getClasses(
                packageName, Thread.currentThread().getContextClassLoader(), (clazz) -> superClass.isAssignableFrom(clazz) && !(superClass.equals(clazz)));
    }

    /**
     * 扫描包里符合条件的类
     *
     * @param packageName
     * @param superClass
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static List<Class<?>> getClassesBySuperClass(String packageName, ClassLoader classLoader, Class<?> superClass)
            throws IOException, ReflectiveOperationException {
        return getClasses(
                packageName, classLoader, (clazz) -> superClass.isAssignableFrom(clazz) && !(superClass.equals(clazz)));
    }

    public static List<Class<?>> getClasses(String packageName, ClassLoader classLoader, Test1<Class<?>> filter)
            throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs = classLoader.getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                File dir = new File(filePath);
                List<File> aClass = FileUtil.getFiles(dir, "class");
                for (File file : aClass) {
                    String className = file.getPath().replace(dir.getPath(), "").substring(1).replace('\\', '.').replace(".class", "");
                    Class<?> loadClass;
                    try {
                        loadClass = classLoader
                                .loadClass(className);
                    } catch (ClassNotFoundException e) {
                        continue;
                    }
                    if (filter != null && !filter.test(loadClass)) {
                        continue;
                    }
                    classes.add(loadClass);
                }
            } else if ("jar".equals(protocol)) {
                JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.charAt(0) == '/') {
                        name = name.substring(1);
                    }
                    if (!name.startsWith(packageDirName)) {
                        continue;
                    }
                    int idx = name.lastIndexOf('/');
                    if (idx != -1) {
                        packageName = name.substring(0, idx).replace('/', '.');
                    }
                    if (!name.endsWith(".class") || entry.isDirectory()) {
                        continue;
                    }
                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                    Class<?> loadClass = classLoader.loadClass(packageName + '.' + className);
                    if (filter != null && !filter.test(loadClass)) {
                        continue;
                    }
                    classes.add(loadClass);
                }
            }
        }
        return classes;
    }

    /**
     * 获取方法全名（避免重载情况）
     */
    public static String getMethodOverloadName(Method method) {
        StringBuilder paramsBuffer = new StringBuilder();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> cl : parameterTypes) {
            String name = cl.getName();
            paramsBuffer.append(name).append(",");
        }
        if (paramsBuffer.length() > 0) {
            paramsBuffer.deleteCharAt(paramsBuffer.length() - 1);
        }
        return method.getName() + "(" + paramsBuffer.toString() + ")";
    }

    /**
     * 获取类的包名
     *
     * @param clazz
     * @return
     */
    public static String getPackageName(Class<?> clazz) {
        return getPackageName(clazz.getName());
    }

    /**
     * 获取类的包名
     *
     * @param classFullName
     * @return
     */
    public static String getPackageName(String classFullName) {
        int lastDot = classFullName.lastIndexOf('.');
        return (lastDot < 0) ? "" : classFullName.substring(0, lastDot);
    }

    /**
     * 创建代理
     *
     * @param interfaceType
     * @param handler
     * @return
     */
    public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException("need interface");
        }
        Object object =
                Proxy.newProxyInstance(
                        interfaceType.getClassLoader(), new Class<?>[]{interfaceType}, handler);
        return interfaceType.cast(object);
    }
}
