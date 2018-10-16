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
package top.limitart.dat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.Conditions;
import top.limitart.base.Func1;
import top.limitart.util.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 静态数据集
 *
 * @author hank
 * @version 2018/10/16 0016 14:51
 */
public class DataSet {
    private static Logger LOGGER = LoggerFactory.getLogger(DataSet.class);
    private Map<Class<? extends DataMeta>, DataContainer> dats = new HashMap<>();


    public <T extends DataMeta> DataContainer<T> getContainer(Class<T> beanClass) {
        return dats.get(beanClass);
    }

    public static DataSet withDir(File dir, String fileType, Func1<File, String> file2ClassType, Func1<DataMeta, DataMeta> reviser) throws Exception {
        return new DataSet().load(dir, fileType, file2ClassType, reviser);
    }

    public DataSet load(File dir, String fileType, Func1<File, String> file2ClassType, Func1<DataMeta, DataMeta> reviser) throws Exception {
        List<File> files = FileUtil.getFiles(dir, fileType);
        return load(files, file2ClassType, reviser);
    }

    public DataSet load(List<File> files, Func1<File, String> file2ClassType, Func1<DataMeta, DataMeta> reviser) throws Exception {
        for (File file : files) {
            load(file, file2ClassType, reviser);
        }
        return this;
    }

    public <T extends DataMeta, R extends T> DataSet load(File file, Func1<File, String> file2ClassType, Func1<T, R> reviser) throws Exception {
        String run = file2ClassType.run(file);
        Class<?> aClass = Class.forName(run);
        return load((Class<T>) aClass, file, reviser);
    }

    public <T extends DataMeta, R extends T> DataSet load(Class<T> type, File file, Func1<T, R> reviser) throws Exception {
        return load(type, FileUtil.readFile1(file), reviser);
    }

    public <T extends DataMeta, R extends T> DataSet load(Class<T> type, InputStream inputStream, Func1<T, R> reviser) throws Exception {
        return load(type, FileUtil.inputStream2ByteArray(inputStream), reviser);
    }

    public <T extends DataMeta, R extends T> DataSet load(Class<T> type, byte[] bytes, Func1<T, R> reviser) throws Exception {
        Conditions.args(!dats.containsKey(type), "type {} duplicated", type.getName());
        List<T> dataMetas = Dats.readDatBin(type, bytes);
        for (T dataMeta : dataMetas) {
            load(dataMeta, reviser);
        }
        LOGGER.info("load data {} success!", type.getName());
        return this;
    }

    public void forEach(Consumer<Class<? extends DataMeta>> consumer) {
        dats.keySet().forEach(consumer);
    }

    public <T extends DataMeta, R extends T> DataSet load(T t, Func1<T, R> reviser) throws Exception {
        Class<? extends DataMeta> aClass = t.getClass();
        dats.putIfAbsent(aClass, new DataContainer());
        DataContainer container = dats.get(aClass);
        Field primaryField = t.getClass().getDeclaredFields()[0];
        primaryField.setAccessible(true);
        Object p = primaryField.get(t);
        container.putIfAbsent(p, Conditions.notNull(reviser.run(t)));
        return this;
    }

}
