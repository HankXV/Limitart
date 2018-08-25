package top.limitart.game.org;
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

import top.limitart.base.Nullable;
import top.limitart.game.org.exception.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 组织(职位和权限建议使用枚举构造)
 *
 * @author hank
 */
public abstract class Org<M extends OrgMember> {
    private long OrgId;
    private long creatorId;
    private final Map<Long, M> members = new ConcurrentHashMap<>();

    public abstract int capacity();

    public abstract Job defaultJob();

    public abstract Job creatorJob();

    /**
     * 初始化一个组织
     *
     * @param orgId
     * @param creator
     * @return
     * @throws AlreadyJoinException
     * @throws OrgMaxMemberException
     */
    public void initOrg(long orgId, M creator) throws AlreadyJoinException, OrgMaxMemberException {
        this.OrgId = orgId;
        this.creatorId = creator.getMemberId();
        join(creator);
        creator.setJob(creatorJob());
    }

    /**
     * 加入组织
     *
     * @param member
     * @throws AlreadyJoinException
     * @throws OrgMaxMemberException
     */
    public void join(M member) throws AlreadyJoinException, OrgMaxMemberException {
        if (this.members.containsKey(member.getMemberId())) {
            throw new AlreadyJoinException();
        }
        if (getMemberCount() >= capacity()) {
            throw new OrgMaxMemberException();
        }
        member.setJob(defaultJob());
        this.members.put(member.getMemberId(), member);
    }

    /**
     * 扫描所有成员
     *
     * @param listener
     */
    public void scanMember(Consumer<M> listener) {
        for (M member : this.members.values()) {
            listener.accept(member);
        }
    }

    public Map<Long, M> copyOfMembers() {
        return new HashMap<>(members);
    }

    /**
     * 获取当前成员数量
     *
     * @return
     */
    public int getMemberCount() {
        return this.members.size();
    }

    /**
     * 退出
     *
     * @param member
     * @throws CreatorCanNotQuitException
     */
    public M quit(M member) throws CreatorCanNotQuitException {
        if (member.getMemberId() == this.creatorId) {
            throw new CreatorCanNotQuitException();
        }
        return members.remove(member.getMemberId());
    }

    /**
     * 更改创造者
     *
     * @param handlerId
     * @param extenderId
     * @throws NoAuthException
     * @throws OrgMemberNotExistException
     */
    public void replaceCreator(long handlerId, long extenderId)
            throws NoAuthException, OrgMemberNotExistException {
        if (handlerId != this.creatorId) {
            throw new NoAuthException();
        }
        forceReplaceCreator(extenderId);
    }

    /**
     * 强制更换创造者
     *
     * @param extenderId
     * @throws OrgMemberNotExistException
     */
    public void forceReplaceCreator(long extenderId) throws OrgMemberNotExistException {
        M orgMember = getOrgMember(extenderId);
        if (orgMember == null) {
            throw new OrgMemberNotExistException();
        }
        M oldCreator = getOrgMember(this.creatorId);
        oldCreator.setJob(defaultJob());
        orgMember.setJob(creatorJob());
        this.creatorId = extenderId;
    }

    /**
     * 给予成员职位
     *
     * @param target
     * @param job
     * @return
     * @throws JobNotExistException
     * @throws JobMemberMaxException
     */
    public void giveMemberJob(M target, Job job) throws JobNotExistException, JobMemberMaxException {
        int jobMemberCount = getJobMemberCount(job);
        if (jobMemberCount >= job.maxMember() && job.maxMember() > 0) {
            throw new JobMemberMaxException();
        }
        target.setJob(job);
    }

    /**
     * 获取某职位当前人数
     *
     * @param job
     * @return
     */
    public int getJobMemberCount(Job job) {
        int count = 0;
        for (M member : members.values()) {
            if (member.getJob() == job) {
                ++count;
            }
        }
        return count;
    }

    /**
     * 获取成员
     *
     * @param memberId
     * @return
     */
    public M getOrgMember(long memberId) {
        return this.members.get(memberId);
    }

    /**
     * 是否有权限
     *
     * @param auth
     * @param handler
     * @param target
     * @return
     */
    public boolean hasAuth(Auth auth, M handler, @Nullable M target) {
        if (handler.getMemberId() == this.creatorId) {
            return true;
        }
        if (target != null
                && target.getMemberId() == this.creatorId
                && handler.getMemberId() != this.creatorId) {
            return false;
        }
        Job job = handler.getJob();
        if (target != null) {
            Job job2 = target.getJob();
            if (job.jobClass() <= job2.jobClass()) {
                return false;
            }
        }
        return job.hasAuth(auth);
    }

    public long getOrgId() {
        return OrgId;
    }

    public long getCreatorId() {
        return creatorId;
    }
}
