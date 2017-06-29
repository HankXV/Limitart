package com.limitart.game.org.listener;

import com.limitart.game.org.Job;
import com.limitart.game.org.Org;
import com.limitart.game.org.OrgMember;

/**
 * 组织事件监听器
 * 
 * @author hank
 *
 */
public interface IOrgEventListener {
	public void onChangeCreator(Org org, OrgMember oldCreator, OrgMember newCreator);

	public void onChangeJob(Org org, OrgMember handler, OrgMember target, Job oldJob, Job newJob);

	public void onQuit(Org org, OrgMember quiter);

	public void onJoin(Org org, OrgMember joiner);

	public void onAddJob(Org org, Job job);

	public void onRemoveJob(Org org, Job job);
}
