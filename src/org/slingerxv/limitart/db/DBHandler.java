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
package org.slingerxv.limitart.db;

import org.slingerxv.limitart.db.sql.Delete;
import org.slingerxv.limitart.db.sql.Insert;
import org.slingerxv.limitart.db.sql.Select;
import org.slingerxv.limitart.db.sql.Update;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库操作器
 *
 * @author hank
 * @version 2018/4/14 0014 16:47
 */
public interface DBHandler extends AutoCloseable {
    /**
     * 创建一个实际的数据库
     *
     * @param dataSource
     * @param batchNum
     * @return
     */
    static DBHandler createReal(DBDataSource dataSource, int batchNum) {
        return new SimpleDBHandler(dataSource, batchNum);
    }

    /**
     * 创建一个无任何操作的数据库(兼容接口用)
     *
     * @return
     */
    static DBHandler createFake() {
        return new FakeDBHandler();
    }

    /**
     * 获取数据源
     *
     * @return
     */
    DataSource dataSource();

    /**
     * 获取链接
     *
     * @return
     * @throws SQLException
     */
    default Connection connection() throws SQLException {
        return dataSource().getConnection();
    }

    /**
     * 查找
     *
     * @param handler
     * @param <T>
     * @return
     * @throws SQLException
     */
    <T> List<T> selectList(Select select, ResultHandler<T> handler) throws SQLException;

    /**
     * 查找单个实例
     *
     * @param select
     * @param handler
     * @param <T>
     * @return
     * @throws SQLException
     */
    <T> T selectOne(Select select, ResultHandler<T> handler) throws SQLException;

    /**
     * 插入
     *
     * @return
     * @throws SQLException
     */
    int insert(Insert insert) throws SQLException;

    /**
     * 更新
     *
     * @return
     * @throws SQLException
     */
    int update(Update update) throws SQLException;

    /**
     * 删除
     *
     * @return
     * @throws SQLException
     */
    int delete(Delete delete) throws SQLException;
}
