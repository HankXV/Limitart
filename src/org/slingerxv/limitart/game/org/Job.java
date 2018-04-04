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
package org.slingerxv.limitart.game.org;


import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.EnumInterface;

/**
 * 职位(建议使用枚举构造一组Job)
 *
 * @author hank
 */
@EnumInterface
public interface Job {
    /**
     * 职位ID
     *
     * @return
     */
    int jobID();

    /**
     * 获取权限
     *
     * @return
     */
    int getAuth();

    /**
     * 职位最大人数
     *
     * @return
     */
    int maxMember();

    /**
     * 职位级别(越高越牛逼,低等级无法操作高等级的)
     *
     * @return
     */
    int jobClass();

    /**
     * 构建一个权限体系
     *
     * @param auths
     * @return
     */
    default int buildAuth(Auth... auths) {
        int auth = 0;
        if (auths != null) {
            for (Auth temp : auths) {
                if (temp != null) {
                    Conditions.args(
                            temp.authID() >= 0 && temp.authID() <= Integer.SIZE,
                            "auth id must between 0~" + Integer.SIZE + ",your id:" + temp.authID());
                    int authValue = 1 << temp.authID();
                    Conditions.args(
                            (auth & authValue) != authValue, "auth id duplicated:" + temp.authID());
                    auth |= authValue;
                }
            }
        }
        return auth;
    }

    /**
     * 是否拥有权限
     *
     * @param auth
     * @return
     */
    default boolean hasAuth(Auth auth) {
        Conditions.args(
                auth.authID() >= 0 && auth.authID() <= Integer.SIZE,
                "auth id must between 0~" + Integer.SIZE + ",your id:" + auth.authID());
        int value = 1 << auth.authID();
        return (value & getAuth()) == value;
    }
}
