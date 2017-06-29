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
package org.slingerxv.limitart.script;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.base.Conditions;

/**
 * 服务器脚本管理
 *
 * @param <KEY> 脚本Id
 * @author hank
 */
public abstract class AbstractScriptLoader<KEY> {
    private static Logger log = LoggerFactory.getLogger(AbstractScriptLoader.class);
    private Map<KEY, ScriptData<KEY>> scriptMap = new ConcurrentHashMap<>();
    private Map<String, KEY> pathMap = new ConcurrentHashMap<>();

    /**
     * 遍历所有脚本
     *
     * @param action 函数
     */
    public void foreach(BiConsumer<? super KEY, ? super Script<KEY>> action) {
        for (Entry<KEY, ScriptData<KEY>> entry : scriptMap.entrySet()) {
            action.accept(entry.getKey(), entry.getValue().getScriptInstance());
        }
    }

    /**
     * 获取脚本
     *
     * @param scriptId
     * @return
     * @throws ScriptNotExistException
     */
    @SuppressWarnings("unchecked")
    public <T extends Script<KEY>> T getScript(KEY scriptId) throws ScriptNotExistException {
        ScriptData<KEY> scriptData = scriptMap.get(scriptId);
        if (scriptData == null) {
            throw new ScriptNotExistException(scriptId);
        }
        return (T) scriptData.getScriptInstance();
    }

    /**
     * 注册脚本数据
     *
     * @param scriptInstance
     * @param codeMD5
     * @param filePath
     * @throws ScriptKeyDuplicatedException
     */
    protected void registerScriptData(Script<KEY> scriptInstance, String codeMD5, String filePath)
            throws ScriptKeyDuplicatedException {
        KEY key = scriptInstance.key();
        boolean replace = false;
        if (scriptMap.containsKey(key)) {
            replace = true;
            ScriptData<KEY> scriptData = scriptMap.get(key);
            // 这里 由于是不同加载器，所以比较类名就行了
            if (!scriptData.getScriptInstance().getClass().getName().equals(scriptInstance.getClass().getName())) {
                throw new ScriptKeyDuplicatedException(scriptData.getScriptInstance().getClass(),
                        scriptInstance.getClass(), scriptInstance.key());
            }
        }
        scriptMap.put(key, new ScriptData<>(scriptInstance, codeMD5, filePath));
        if (filePath != null) {
            pathMap.put(filePath, key);
        }
        if (replace) {
            log.info("replace script data on script:" + scriptInstance.getClass().getName());
        } else {
            log.info("register new script data on script:" + scriptInstance.getClass().getName());
        }
    }

    /**
     * 对比MD5是否相同
     *
     * @param key
     * @param codeMD5
     * @return
     * @throws ScriptNotExistException
     */
    protected boolean isSameCode(KEY key, String codeMD5) throws ScriptNotExistException {
        Conditions.notNull(key, "key");
        Conditions.notNull(codeMD5, "codeMD5");
        ScriptData<KEY> scriptData = scriptMap.get(key);
        if (scriptData == null) {
            throw new ScriptNotExistException(key);
        }
        return codeMD5.equals(scriptData.getCodeMD5());
    }

    /**
     * 获取脚本文件所在位置
     *
     * @param key
     * @return
     * @throws ScriptNotExistException
     */
    protected String getFilePath(KEY key) throws ScriptNotExistException {
        Conditions.notNull(key, "key");
        ScriptData<KEY> scriptData = scriptMap.get(key);
        if (scriptData == null) {
            throw new ScriptNotExistException(key);
        }
        return scriptData.getFilePath();
    }

    protected KEY getScriptKey(File file) {
        return pathMap.get(getFilePath(file));
    }

    protected KEY getScriptKey(String filePath) {
        return pathMap.get(filePath);
    }

    protected String getFilePath(File file) {
        return file.getAbsolutePath();
    }

}
