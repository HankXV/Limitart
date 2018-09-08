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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * SQL
 *
 * @author hank
 * @version 2018/4/14 0014 17:27
 */
public interface SQL {
    /**
     * sql模版
     *
     * @return
     */
    String sql();

    /**
     * 模版相应的参数
     *
     * @return
     */
    Object[] params();

    /**
     * 转化为PreparedStatement
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    default PreparedStatement toStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql());
        Object[] params = params();
        if (params() != null && params().length > 0) {
            for (int i = 1; i <= params.length; ++i) {
                preparedStatement.setObject(i, params[i - 1]);
            }
        }
        return preparedStatement;
    }
}
