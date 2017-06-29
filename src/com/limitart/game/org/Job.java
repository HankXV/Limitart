package com.limitart.game.org;

import com.limitart.game.org.exception.AuthIDDuplicatedException;
import com.limitart.game.org.exception.AuthIDErrorException;

/**
 * 职位
 * 
 * @author hank
 *
 */
public class Job {
	public transient static final int NONE_JOB_ID = 0;
	public transient static final int DEFAULT_MAX_MEMBER = 10;
	public transient static final int DEFAULT_JOB_CLASS = 0;
	private int jobId = NONE_JOB_ID;
	private int jobClass = DEFAULT_JOB_CLASS;
	private String jobName;
	private int maxMember = DEFAULT_MAX_MEMBER;
	private int auth = 0;

	/**
	 * 添加一系列权限
	 * 
	 * @param auths
	 * @throws AuthIDErrorException
	 * @throws AuthIDDuplicatedException
	 */
	public void addAuthes(IAuth... auths) throws AuthIDErrorException, AuthIDDuplicatedException {
		if (auths != null) {
			for (IAuth temp : auths) {
				if (temp != null) {
					if (temp.getAuthID() < 0 || temp.getAuthID() > Integer.SIZE) {
						throw new AuthIDErrorException(
								"auth id must between 0~" + Integer.SIZE + ",your id:" + temp.getAuthID());
					}
					int authValue = 1 << temp.getAuthID();
					if ((this.auth & authValue) == authValue) {
						throw new AuthIDDuplicatedException("auth id duplicated:" + temp.getAuthID());
					}
					this.auth |= authValue;
				}
			}
		}
	}

	/**
	 * 添加一个权限
	 * 
	 * @param auth
	 * @throws AuthIDErrorException
	 */
	public void addAuth(IAuth auth) throws AuthIDErrorException {
		if (auth.getAuthID() < 0 || auth.getAuthID() > Integer.SIZE) {
			throw new AuthIDErrorException("auth id must between 0~" + Integer.SIZE + ",your id:" + auth.getAuthID());
		}
		this.auth |= (1 << auth.getAuthID());
	}

	/**
	 * 删除权限
	 * 
	 * @param auth
	 * @throws AuthIDErrorException
	 */
	public void removeAuth(IAuth auth) throws AuthIDErrorException {
		if (auth.getAuthID() < 0 || auth.getAuthID() > Integer.SIZE) {
			throw new AuthIDErrorException("auth id must between 0~" + Integer.SIZE + ",your id:" + auth.getAuthID());
		}
		int mask = ~(1 << auth.getAuthID());
		this.auth &= mask;
	}

	/**
	 * 是否拥有权限
	 * 
	 * @param auth
	 * @return
	 * @throws AuthIDErrorException
	 */
	public boolean hasAuth(IAuth auth) throws AuthIDErrorException {
		if (auth.getAuthID() < 0 || auth.getAuthID() > Integer.SIZE) {
			throw new AuthIDErrorException("auth id must between 0~" + Integer.SIZE + ",your id:" + auth.getAuthID());
		}
		int value = 1 << auth.getAuthID();
		return (value & this.auth) == value;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public int getJobClass() {
		return jobClass;
	}

	public void setJobClass(int jobClass) {
		this.jobClass = jobClass;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public int getMaxMember() {
		return maxMember;
	}

	public void setMaxMember(int maxMember) {
		this.maxMember = maxMember;
	}

	public int getAuth() {
		return auth;
	}

	public void setAuth(int auth) {
		this.auth = auth;
	}

}
