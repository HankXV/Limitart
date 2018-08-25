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
package top.limitart.db.sql;

import top.limitart.base.Conditions;

/**
 * 更新
 *
 * @author hank
 * @version 2018/4/14 0014 17:34
 */
public class Update implements SQL {
    private static final String TEMPLATE = "UPDATE `%s` SET %s WHERE %s;";
    private String tableName;
    private String[] setFields;
    private Object[] setValues;
    private Where where;

    public static Update start() {
        return new Update();
    }

    private Update() {
    }

    public Update from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * @param sets
     * @return
     */
    public Update set(Object... sets) {
        Conditions.args(sets != null && sets.length > 0 && (sets.length & 1) == 0, "length must be 2n");
        int length = sets.length >> 1;
        setFields = new String[length];
        setValues = new Object[length];
        for (int i = 0; i < length; i++) {
            setFields[i] = sets[i << 1].toString();
            setValues[i] = sets[(i << 1) + 1];
        }
        return this;
    }

    public Update where(Where where) {
        this.where = where;
        return this;
    }

    @Override
    public String sql() {
        StringBuilder nameBuilder = new StringBuilder();
        for (String name : setFields) {
            nameBuilder.append("`").append(name).append("`").append("=").
                    append("?").append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.length() - 1);
        return String.format(TEMPLATE, tableName, nameBuilder.toString(), where.build());
    }

    @Override
    public Object[] params() {
        Object[] conditionValues = where.getConditionValues();
        Object[] result = new Object[setFields.length + conditionValues.length];
        System.arraycopy(setValues, 0, result, 0, setValues.length);
        System.arraycopy(conditionValues, 0, result, setValues.length, conditionValues.length);
        return result;
    }
}
