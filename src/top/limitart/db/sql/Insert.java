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
 * 插入
 *
 * @author hank
 * @version 2018/4/14 0014 16:49
 */
public class Insert implements SQL {
    private static final String TEMPLATE = "INSERT INTO `%s`(%s)VALUES(%s);";
    private String tableName;
    private String[] fieldNames;
    private Object[] fieldValues;

    public static Insert start() {
        return new Insert();
    }

    private Insert() {
    }

    public Insert into(String table) {
        this.tableName = table;
        return this;
    }

    /**
     * name,value,name,value....
     *
     * @param values
     * @return
     */
    public Insert values(Object... values) {
        Conditions.args(values != null && values.length > 0 && (values.length & 1) == 0, "length must be 2n");
        int length = values.length >> 1;
        fieldNames = new String[length];
        fieldValues = new Object[length];
        for (int i = 0; i < length; i++) {
            fieldNames[i] = values[i << 1].toString();
            fieldValues[i] = values[(i << 1) + 1];
        }
        return this;
    }

    @Override
    public String sql() {
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder pos = new StringBuilder();
        for (String name : fieldNames) {
            nameBuilder.append("`").append(name).append("`").append(",");
            pos.append("?").append(",");
        }
        nameBuilder.deleteCharAt(nameBuilder.length() - 1);
        pos.deleteCharAt(pos.length() - 1);
        return String.format(Insert.TEMPLATE, tableName, nameBuilder.toString(), pos.toString());
    }

    @Override
    public Object[] params() {
        return fieldValues;
    }
}
