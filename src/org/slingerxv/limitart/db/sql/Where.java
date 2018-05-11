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

import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.NotNull;
import org.slingerxv.limitart.base.Triple;
import org.slingerxv.limitart.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL构建器Where部件
 *
 * @author hank
 * @version 2018/4/14 0014 16:47
 */
public class Where {
    private final List<Triple<Condition, String, Object>> wheres = new ArrayList<>();
    private int conditionValueCount;

    public static @NotNull
    Where create() {
        return new Where();
    }

    private Where() {
    }

    private void addWhere(Condition condition, String fieldName, Object value) {
        if (condition != Condition.QUOTE && !condition.logicRelation()) {
            Conditions.args(StringUtil.notEmpty(fieldName), "field :%s", fieldName);
        }
        if (wheres.isEmpty()) {
            Conditions.args(condition != Condition.AND && condition != Condition.OR, "don't start with and(or)!");
        } else {
            Triple<Condition, String, Object> where = wheres.get(wheres.size() - 1);
            Conditions.args(condition.logicRelation() != where.getA().logicRelation(), "illegal condition,%s", condition.name());
        }
        if (value != null) {
            ++conditionValueCount;
        }
        wheres.add(Triple.ofImmutable(condition, fieldName, value));
    }


    public Object[] getConditionValues() {
        int count = 0;
        Object[] objects = new Object[conditionValueCount];
        for (Triple<Condition, String, Object> t : wheres) {
            if (t.getC() == null) {
                continue;
            }
            objects[count++] = t.getC();
        }
        return objects;
    }

    /**
     * 等于
     *
     * @param fieldName
     * @param value
     * @return
     */
    public Where whereEquals(@NotNull String fieldName, @NotNull Object value) {
        addWhere(Condition.EQUALS, fieldName, value);
        return this;
    }

    /**
     * 不等于
     *
     * @param fieldName
     * @param value
     * @return
     */
    public Where whereNotEquals(@NotNull String fieldName, @NotNull Object value) {
        addWhere(Condition.NOT_EQUALS, fieldName, value);
        return this;
    }

    /**
     * 大于
     *
     * @param fieldName
     * @param value
     * @return
     */
    public Where whereGreaterThan(@NotNull String fieldName, @NotNull Object value) {
        addWhere(Condition.GREATER_THAN, fieldName, value);
        return this;
    }

    /**
     * 大于等于
     *
     * @param fieldName
     * @param value
     * @return
     */
    public Where whereEqualOrGreaterThan(@NotNull String fieldName, @NotNull Object value) {
        addWhere(Condition.EQUAL_OR_GREATER_THAN, fieldName, value);
        return this;
    }

    /**
     * 小于
     *
     * @param fieldName
     * @param value
     * @return
     */
    public Where whereLessThan(@NotNull String fieldName, @NotNull Object value) {
        addWhere(Condition.LESS_THAN, fieldName, value);
        return this;
    }

    /**
     * 小于等于
     *
     * @param fieldName
     * @param value
     * @return
     */
    public Where whereEqualOrLessThan(@NotNull String fieldName, @NotNull Object value) {
        addWhere(Condition.EQUAL_OR_LESS_THAN, fieldName, value);
        return this;
    }

    /**
     * 为空
     *
     * @param fieldName
     * @return
     */
    public Where whereNull(@NotNull String fieldName) {
        addWhere(Condition.NULL, fieldName, null);
        return this;
    }

    /**
     * 不为空
     *
     * @param fieldName
     * @return
     */
    public Where whereNotNull(@NotNull String fieldName) {
        addWhere(Condition.NOT_NULL, fieldName, null);
        return this;
    }

    /**
     * 括号
     *
     * @param another
     * @return
     */
    public Where quote(@NotNull Where another) {
        addWhere(Condition.QUOTE, null, another);
        return this;
    }

    /**
     * 并且
     *
     * @return
     */
    public Where and() {
        addWhere(Condition.AND, null, null);
        return this;
    }

    /**
     * 或者
     *
     * @return
     */
    public Where or() {
        addWhere(Condition.OR, null, null);
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        wheres.forEach(t -> {
            Condition condition = t.getA();
            String fieldName = t.getB();
            Object value = t.getC();
            if (condition == Condition.QUOTE) {
                builder.append("(").append(((Where) value).build()).append(")");
            } else if (condition.logicRelation()) {
                builder.append(condition.symbol());
            } else {
                builder.append("`").append(fieldName).append('`').append(condition.symbol());
                if (value != null) {
                    builder.append("?");
                }
            }
            builder.append(" ");
        });
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private enum Condition {
        //相等
        EQUALS {
            @Override
            String symbol() {
                return "=";
            }
        },
        //不等
        NOT_EQUALS {
            @Override
            String symbol() {
                return "<>";
            }
        },
        //大于
        GREATER_THAN {
            @Override
            String symbol() {
                return ">";
            }
        },
        //大于等于
        EQUAL_OR_GREATER_THAN {
            @Override
            String symbol() {
                return ">=";
            }
        },
        //小于
        LESS_THAN {
            @Override
            String symbol() {
                return "<";
            }
        },
        //小于等于
        EQUAL_OR_LESS_THAN {
            @Override
            String symbol() {
                return "<=";
            }
        },
        //为空
        NULL {
            @Override
            String symbol() {
                return "IS NULL";
            }
        },
        //不为空
        NOT_NULL {
            @Override
            String symbol() {
                return "IS NOT NULL";
            }
        },
        //括号
        QUOTE {
            @Override
            String symbol() {
                return null;
            }
        },
        //并且
        AND {
            @Override
            String symbol() {
                return "AND";
            }

            @Override
            boolean logicRelation() {
                return true;
            }
        },
        //或者
        OR {
            @Override
            String symbol() {
                return "OR";
            }

            @Override
            boolean logicRelation() {
                return true;
            }
        },;

        abstract String symbol();

        /**
         * 是否是关系表达式
         *
         * @return
         */
        boolean logicRelation() {
            return false;
        }
    }
}
