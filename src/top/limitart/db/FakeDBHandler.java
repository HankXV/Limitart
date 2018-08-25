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
package top.limitart.db;

import top.limitart.db.sql.Delete;
import top.limitart.db.sql.Insert;
import top.limitart.db.sql.Select;
import top.limitart.db.sql.Update;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 不做任何操作的数据库操作器
 *
 * @author hank
 * @version 2018/4/14 0014 16:47
 */
public class FakeDBHandler implements DBHandler {
    @Override
    public DataSource dataSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> selectList(Select select, ResultHandler<T> handler) throws SQLException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public <T> T selectOne(Select select, ResultHandler<T> handler) throws SQLException {
        return null;
    }

    @Override
    public int insert(Insert insert) throws SQLException {
        return 1;
    }

    @Override
    public int update(Update update) throws SQLException {
        return 1;
    }

    @Override
    public int delete(Delete delete) throws SQLException {
        return 1;
    }

    @Override
    public void close() throws Exception {
    }
}
