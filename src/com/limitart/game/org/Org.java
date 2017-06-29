package com.limitart.game.org;

import java.util.concurrent.ConcurrentHashMap;

import com.limitart.game.org.authes.ChangeJobAuth;
import com.limitart.game.org.exception.AlreadyJoinException;
import com.limitart.game.org.exception.AuthIDErrorException;
import com.limitart.game.org.exception.CreatorCanNotQuitException;
import com.limitart.game.org.exception.JobIDDuplicatedException;
import com.limitart.game.org.exception.JobIDErrorException;
import com.limitart.game.org.exception.JobMemberMaxException;
import com.limitart.game.org.exception.JobNameEmptyException;
import com.limitart.game.org.exception.JobNotExistException;
import com.limitart.game.org.exception.NoAuthException;
import com.limitart.game.org.exception.OrgMaxMemberException;
import com.limitart.game.org.exception.OrgMemberNotExistException;
import com.limitart.game.org.listener.IOrgEventListener;
import com.limitart.game.org.listener.IOrgMemberScaner;
import com.limitart.util.StringUtil;

/**
 * 组织
 * 
 * @author hank
 *
 */
public abstract class Org {
	private long OrgId;
	private long creatorId;
	private transient IOrgEventListener listener;
	private ConcurrentHashMap<Integer, Job> jobs = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, OrgMember> members = new ConcurrentHashMap<>();

	/**
	 * 初始化
	 * 
	 * @param orgId
	 * @param creatorId
	 * @throws OrgMaxMemberException
	 * @throws AlreadyJoinException
	 */
	public void initOrg(long orgId, OrgMember creator, IOrgEventListener listener)
			throws AlreadyJoinException, OrgMaxMemberException {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		this.OrgId = orgId;
		this.creatorId = creator.getMemberId();
		this.listener = listener;
		join(creator);
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
	public void registerJob(OrgMember handler, Job job)
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
		this.listener.onAddJob(this, job);
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
	public void unregisterJob(OrgMember handler, int jobId)
			throws NoAuthException, JobMemberMaxException, AuthIDErrorException, JobNotExistException {
		if (handler.getMemberId() != this.creatorId) {
			throw new NoAuthException();
		}
		Job remove = this.jobs.remove(jobId);
		if (remove != null) {
			this.listener.onRemoveJob(this, remove);
			for (OrgMember member : this.members.values()) {
				giveMemberJob(handler, member, Job.NONE_JOB_ID);
			}
		}
	}

	/**
	 * 加入
	 * 
	 * @param creator
	 * @throws AlreadyJoinException
	 * @throws OrgMaxMemberException
	 */
	public void join(OrgMember member) throws AlreadyJoinException, OrgMaxMemberException {
		if (this.members.containsKey(member.getMemberId())) {
			throw new AlreadyJoinException();
		}
		if (getMemberCount() >= capacity()) {
			throw new OrgMaxMemberException();
		}
		this.members.put(member.getMemberId(), member);
		this.listener.onJoin(this, member);
	}

	/**
	 * 扫描所有成员
	 * 
	 * @param listener
	 */
	public void scanMember(IOrgMemberScaner listener) {
		for (OrgMember member : this.members.values()) {
			listener.scan(member);
		}
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
	public void quit(OrgMember member) throws CreatorCanNotQuitException {
		if (member.getMemberId() == this.creatorId) {
			throw new CreatorCanNotQuitException();
		}
		OrgMember remove = members.remove(member.getMemberId());
		if (remove != null) {
			this.listener.onQuit(this, remove);
		}
	}

	/**
	 * 更改创造者
	 * 
	 * @param handlerId
	 * @param extenderId
	 * @throws NoAuthException
	 * @throws OrgMemberNotExistException
	 */
	public void replaceCreator(long handlerId, long extenderId) throws NoAuthException, OrgMemberNotExistException {
		if (handlerId != this.creatorId) {
			throw new NoAuthException();
		}
		forceRepalceCreator(extenderId);
	}

	/**
	 * 强制更换创造者
	 * 
	 * @param extenderId
	 * @throws OrgMemberNotExistException
	 */
	public void forceRepalceCreator(long extenderId) throws OrgMemberNotExistException {
		OrgMember orgMember = getOrgMember(extenderId);
		if (orgMember == null) {
			throw new OrgMemberNotExistException();
		}
		OrgMember oldCreator = getOrgMember(this.creatorId);
		this.creatorId = extenderId;
		this.listener.onChangeCreator(this, oldCreator, orgMember);
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
	public void giveMemberJob(OrgMember handler, OrgMember target, int jobId)
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
		this.listener.onChangeJob(this, handler, target, getJob(oldJobId), job);
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

	public abstract int capacity();
}
