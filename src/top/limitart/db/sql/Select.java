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
 * 查找
 *
 * @author hank
 * @version 2018/4/14 0014 17:34
 */
public class Select implements SQL {
    private static final String TEMPLATE = "SELECT %s FROM `%s` %s;";
    private final String[] fields;
    private String tableName;
    private Where where;

    public static Select start(String... fields) {
        return new Select(fields);
    }

    private Select(String... fields) {
        Conditions.args(fields != null && fields.length > 0);
        this.fields = fields;
    }

    public Select from(String tableName) {
        this.tableName = tableName;
        return this;
    }


    public Select where(Where where) {
        this.where = where;
        return this;
    }

    @Override
    public String sql() {
        StringBuilder fieldPos = new StringBuilder();
        for (String fieldName : fields) {
            fieldPos.append("`").append(fieldName).append("`").append(",");
        }
        fieldPos.deleteCharAt(fieldPos.length() - 1);
        return String.format(
                TEMPLATE, fieldPos.toString(), tableName, where == null ? "" : "WHERE " + where.build());
    }

    @Override
    public Object[] params() {
        return where == null ? null : where.getConditionValues();
    }
}
