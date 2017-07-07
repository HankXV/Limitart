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
	void onChangeCreator(Org org, OrgMember oldCreator, OrgMember newCreator);

	void onChangeJob(Org org, OrgMember handler, OrgMember target, Job oldJob, Job newJob);

	void onQuit(Org org, OrgMember quiter);

	void onJoin(Org org, OrgMember joiner);

	void onAddJob(Org org, Job job);

	void onRemoveJob(Org org, Job job);
}
