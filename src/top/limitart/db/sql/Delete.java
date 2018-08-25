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

/**
 * 删除
 *
 * @author hank
 * @version 2018/4/14 0014 17:34
 */
public class Delete implements SQL {
    private static final String TEMPLATE = "DELETE FROM `%s` WHERE %s;";
    private String tableName;
    private Where where;

    public static Delete start() {
        return new Delete();
    }

    private Delete() {
    }

    public Delete from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Delete where(Where where) {
        this.where = where;
        return this;
    }

    @Override
    public String sql() {
        return String.format(TEMPLATE, tableName, where.build());
    }

    @Override
    public Object[] params() {
        return where.getConditionValues();
    }
}
