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

import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.db.sql.Delete;
import org.slingerxv.limitart.db.sql.Insert;
import org.slingerxv.limitart.db.sql.Select;
import org.slingerxv.limitart.db.sql.Update;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作器
 *
 * @author hank
 * @version 2018/4/14 0014 16:47
 */
public class SimpleDBHandler implements DBHandler {
    private DataSource dataSource;
    //批量操作的值
    private int batchNum;

    /**
     * @param dataSource
     * @param batchNum
     */
    public SimpleDBHandler(DBDataSource dataSource, int batchNum) {
        this.dataSource = dataSource;
        this.batchNum = batchNum;
    }


    @Override
    public DataSource dataSource() {
        return this.dataSource;
    }

    @Override
    public <T> List<T> selectList(Select select, ResultHandler<T> handler) throws SQLException {
        try (Connection con = connection(); PreparedStatement preparedStatement = select.toStatement(con); ResultSet resultSet =
                preparedStatement.executeQuery()) {
            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(handler.parse(resultSet));
            }
            return result;
        }
    }

    @Override
    public <T> T selectOne(Select select, ResultHandler<T> handler) throws SQLException {
        try (Connection con = connection(); PreparedStatement preparedStatement = select.toStatement(con); ResultSet resultSet =
                preparedStatement.executeQuery()) {
            if (resultSet.last()) {
                Conditions.args(resultSet.getRow() == 1, "multi result? sql:{}", preparedStatement.toString());
                return handler.parse(resultSet);
            }
            return null;
        }
    }

    @Override
    public int insert(Insert insert) throws SQLException {
        //TODO 缓存 批量
        return 0;
    }

    @Override
    public int update(Update update) throws SQLException {
        //TODO 缓存 批量
        return 0;
    }

    @Override
    public int delete(Delete delete) throws SQLException {
        try (Connection con = connection(); PreparedStatement statement = delete.toStatement(con)) {
            return statement.executeUpdate();
        }
    }

    @Override
    public void close() throws Exception {
        throw new UnsupportedOperationException();
    }
}
