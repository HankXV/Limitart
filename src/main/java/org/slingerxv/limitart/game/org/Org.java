/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.game.org;

import java.util.concurrent.ConcurrentHashMap;

import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Proc5;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.game.org.authes.ChangeJobAuth;
import org.slingerxv.limitart.game.org.exception.AlreadyJoinException;
import org.slingerxv.limitart.game.org.exception.AuthIDErrorException;
import org.slingerxv.limitart.game.org.exception.CreatorCanNotQuitException;
import org.slingerxv.limitart.game.org.exception.JobIDDuplicatedException;
import org.slingerxv.limitart.game.org.exception.JobIDErrorException;
import org.slingerxv.limitart.game.org.exception.JobMemberMaxException;
import org.slingerxv.limitart.game.org.exception.JobNameEmptyException;
import org.slingerxv.limitart.game.org.exception.JobNotExistException;
import org.slingerxv.limitart.game.org.exception.NoAuthException;
import org.slingerxv.limitart.game.org.exception.OrgMaxMemberException;
import org.slingerxv.limitart.game.org.exception.OrgMemberNotExistException;
import org.slingerxv.limitart.util.Beta;
import org.slingerxv.limitart.util.StringUtil;

/**
 * 组织
 * 
 * @author hank
 *
 */
@Beta
public abstract class Org {
	private long OrgId;
	private long creatorId;
	private ConcurrentHashMap<Integer, Job> jobs = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, OrgMember> members = new ConcurrentHashMap<>();
	private transient Proc3<Org, OrgMember, OrgMember> onChangeCreator;
	private transient Proc5<Org, OrgMember, OrgMember, Job, Job> onChangeJob;
	private transient Proc2<Org, OrgMember> onQuit;
	private transient Proc2<Org, OrgMember> onJoin;
	private transient Proc2<Org, Job> onAddJob;
	private transient Proc2<Org, Job> onRemoveJob;

	/**
	 * 初始化
	 * 
	 * @param orgId
	 * @param creatorId
	 * @throws OrgMaxMemberException
	 * @throws AlreadyJoinException
	 */
	public Org initOrg(long orgId, OrgMember creator) throws AlreadyJoinException, OrgMaxMemberException {
		this.OrgId = orgId;
		this.creatorId = creator.getMemberId();
		join(creator);
		return this;
	}

	/**
	 * 注册职位
	 * 
	 * @param job
	 * @throws JobIDDuplicatedException
	 * @throws NoAuthException
	 * @throws JobNameEmptyException
	 * @throws JobIDErrorException
	 */
	public Org registerJob(OrgMember handler, Job job)
			throws JobIDDuplicatedException, NoAuthException, JobNameEmptyException, JobIDErrorException {
		if (handler.getMemberId() != this.creatorId) {
			throw new NoAuthException();
		}
		if (StringUtil.isEmptyOrNull(job.getJobName())) {
			throw new JobNameEmptyException();
		}
		if (job.getJobId() == Job.NONE_JOB_ID) {
			throw new JobIDErrorException();
		}
		if (this.jobs.containsKey(job.getJobId())) {
			throw new JobIDDuplicatedException();
		}
		this.jobs.put(job.getJobId(), job);
		Procs.invoke(onAddJob, this, job);
		return this;
	}

	/**
	 * 注销职位
	 * 
	 * @param jobName
	 * @throws NoAuthException
	 * @throws JobNotExistException
	 * @throws AuthIDErrorException
	 * @throws JobMemberMaxException
	 */
	public Org unregisterJob(OrgMember handler, int jobId)
			throws NoAuthException, JobMemberMaxException, AuthIDErrorException, JobNotExistException {
		if (handler.getMemberId() != this.creatorId) {
			throw new NoAuthException();
		}
		Job remove = this.jobs.remove(jobId);
		if (remove != null) {
			Procs.invoke(onRemoveJob, this, remove);
			for (OrgMember member : this.members.values()) {
				giveMemberJob(handler, member, Job.NONE_JOB_ID);
			}
		}
		return this;
	}

	/**
	 * 加入
	 * 
	 * @param creator
	 * @throws AlreadyJoinException
	 * @throws OrgMaxMemberException
	 */
	public Org join(OrgMember member) throws AlreadyJoinException, OrgMaxMemberException {
		if (this.members.containsKey(member.getMemberId())) {
			throw new AlreadyJoinException();
		}
		if (getMemberCount() >= capacity()) {
			throw new OrgMaxMemberException();
		}
		this.members.put(member.getMemberId(), member);
		Procs.invoke(onJoin, this, member);
		return this;
	}

	/**
	 * 扫描所有成员
	 * 
	 * @param listener
	 */
	public Org scanMember(Proc1<OrgMember> listener) {
		for (OrgMember member : this.members.values()) {
			listener.run(member);
		}
		return this;
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
	public Org quit(OrgMember member) throws CreatorCanNotQuitException {
		if (member.getMemberId() == this.creatorId) {
			throw new CreatorCanNotQuitException();
		}
		OrgMember remove = members.remove(member.getMemberId());
		if (remove != null) {
			Procs.invoke(onQuit, this, remove);
		}
		return this;
	}

	/**
	 * 更改创造者
	 * 
	 * @param handlerId
	 * @param extenderId
	 * @throws NoAuthException
	 * @throws OrgMemberNotExistException
	 */
	public Org replaceCreator(long handlerId, long extenderId) throws NoAuthException, OrgMemberNotExistException {
		if (handlerId != this.creatorId) {
			throw new NoAuthException();
		}
		forceRepalceCreator(extenderId);
		return this;
	}

	/**
	 * 强制更换创造者
	 * 
	 * @param extenderId
	 * @throws OrgMemberNotExistException
	 */
	public Org forceRepalceCreator(long extenderId) throws OrgMemberNotExistException {
		OrgMember orgMember = getOrgMember(extenderId);
		if (orgMember == null) {
			throw new OrgMemberNotExistException();
		}
		OrgMember oldCreator = getOrgMember(this.creatorId);
		this.creatorId = extenderId;
		Procs.invoke(onChangeCreator, this, oldCreator, orgMember);
		return this;
	}

	/**
	 * 给予成员职位
	 * 
	 * @param handler
	 * @param target
	 * @param job
	 * @throws JobMemberMaxException
	 * @throws AuthIDErrorException
	 * @throws NoAuthException
	 * @throws JobNotExistException
	 */
	public Org giveMemberJob(OrgMember handler, OrgMember target, int jobId)
			throws AuthIDErrorException, NoAuthException, JobNotExistException, JobMemberMaxException {
		if (!hasAuth(new ChangeJobAuth(), handler, target)) {
			throw new NoAuthException();
		}
		Job job = getJob(jobId);
		if (job == null && jobId != Job.NONE_JOB_ID) {
			throw new JobNotExistException();
		}
		int jobMemberCount = getJobMemberCount(jobId);
		if (job != null && jobMemberCount >= job.getMaxMember()) {
			throw new JobMemberMaxException();
		}
		int oldJobId = target.getJobId();
		target.setJobId(jobId);
		Procs.invoke(onChangeJob, this, handler, target, getJob(oldJobId), job);
		return this;
	}

	/**
	 * 获取职位
	 * 
	 * @param jobName
	 * @return
	 */
	private Job getJob(int jobId) {
		return this.jobs.get(jobId);
	}

	/**
	 * 获取某职位当前人数
	 * 
	 * @param job
	 * @return
	 */
	public int getJobMemberCount(int jobId) {
		int count = 0;
		for (OrgMember member : members.values()) {
			if (member.getJobId() == jobId) {
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
	public OrgMember getOrgMember(long memberId) {
		return this.members.get(memberId);
	}

	/**
	 * 是否有权限
	 * 
	 * @param member
	 * @return
	 * @throws AuthIDErrorException
	 */
	protected boolean hasAuth(IAuth auth, OrgMember handler, OrgMember target) throws AuthIDErrorException {
		if (handler.getMemberId() == this.creatorId) {
			return true;
		}
		if (target.getMemberId() == this.creatorId && handler.getMemberId() != this.creatorId) {
			return false;
		}
		Job job = getJob(handler.getJobId());
		if (job == null) {
			return false;
		}
		Job job2 = getJob(target.getJobId());
		if (job2 != null && job.getJobClass() < job2.getJobClass()) {
			return false;
		}
		return job.hasAuth(auth);
	}

	public long getOrgId() {
		return OrgId;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public Org onChangeCreator(Proc3<Org, OrgMember, OrgMember> listener) {
		this.onChangeCreator = listener;
		return this;
	}

	public Org onChangeJob(Proc5<Org, OrgMember, OrgMember, Job, Job> listener) {
		this.onChangeJob = listener;
		return this;
	}

	public Org onQuit(Proc2<Org, OrgMember> listener) {
		this.onQuit = listener;
		return this;
	}

	public Org onJoin(Proc2<Org, OrgMember> listener) {
		this.onJoin = listener;
		return this;
	}

	public Org onAddJob(Proc2<Org, Job> listener) {
		this.onAddJob = listener;
		return this;
	}

	public Org onRemoveJob(Proc2<Org, Job> listener) {
		this.onRemoveJob = listener;
		return this;
	}

	public abstract int capacity();
}
