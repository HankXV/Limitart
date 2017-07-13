package org.slingerxv.limitart.game.org.listener;

import org.slingerxv.limitart.game.org.Job;
import org.slingerxv.limitart.game.org.Org;
import org.slingerxv.limitart.game.org.OrgMember;

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
