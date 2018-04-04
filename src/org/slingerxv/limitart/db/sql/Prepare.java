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
package org.slingerxv.limitart.db.sql;


/**
 * SQL构建器
 *
 * @author hank
 * @version 2018/4/14 0014 16:47
 */
public interface Prepare {
    static void main(String[] args) {
        SQL insert = Prepare.insert().into("account").values("id", 1, "name", "hank");
        System.out.println(insert.sql());
        SQL delete = Prepare.delete().from("account").where(Where.create().whereEquals("id", 1));
        System.out.println(delete.sql());
        Update update = Prepare.update().from("account").set("id", 1, "age", 10).where(Where.create().whereEquals("name", "hank"));
        System.out.println(update.sql());
        Select select = Prepare.select("id", "age").from("account").where(Where.create().whereEquals("name", "hank"));
        System.out.println(select.sql());
    }

    /**
     * 开始编写插入语句
     *
     * @return
     */
    static Insert insert() {
        return Insert.start();
    }

    /**
     * 开始编写更新语句
     *
     * @return
     */
    static Update update() {
        return Update.start();
    }

    /**
     * 开始编写删除语句
     *
     * @return
     */
    static Delete delete() {
        return Delete.start();
    }

    /**
     * 开始编写查找语句
     *
     * @return
     */
    static Select select(String... fields) {
        return Select.start(fields);
    }
}
