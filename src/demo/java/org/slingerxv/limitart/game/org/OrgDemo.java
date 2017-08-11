package org.slingerxv.limitart.game.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.game.org.exception.AlreadyJoinException;
import org.slingerxv.limitart.game.org.exception.AuthIDErrorException;
import org.slingerxv.limitart.game.org.exception.JobIDDuplicatedException;
import org.slingerxv.limitart.game.org.exception.JobIDErrorException;
import org.slingerxv.limitart.game.org.exception.JobMemberMaxException;
import org.slingerxv.limitart.game.org.exception.JobNameEmptyException;
import org.slingerxv.limitart.game.org.exception.JobNotExistException;
import org.slingerxv.limitart.game.org.exception.NoAuthException;
import org.slingerxv.limitart.game.org.exception.OrgMaxMemberException;

public class OrgDemo {
	private static Logger log = LoggerFactory.getLogger(OrgDemo.class);

	public static void main(String[] args) throws AlreadyJoinException, OrgMaxMemberException, JobIDDuplicatedException,
			NoAuthException, JobMemberMaxException, AuthIDErrorException, JobNotExistException, JobNameEmptyException,
			JobIDErrorException {
		OrgMember member1 = new OrgMember();
		member1.setMemberId(1);
		OrgMember member2 = new OrgMember();
		member2.setMemberId(2);
		Job job = new Job();
		job.setJobName("总经理");
		job.setJobId(2);
		new Org() {

			@Override
			public int capacity() {
				return 2;
			}
		}.initOrg(1, member1).onRemoveJob((o, j) -> {
			log.info("onRemoveJob:" + j.getJobName());
		}).onQuit((org, member) -> {
			log.info("onQuit:" + member.getMemberId());
		}).onJoin((org, member) -> {
			log.info("onJoin:" + member.getMemberId());
		}).onChangeJob((org, handler, target, oldJob, newJob) -> {
			log.info("onChangeJob,old:" + (oldJob == null ? "null" : oldJob.getJobName()) + ",new:"
					+ (newJob == null ? "null" : newJob.getJobName()));
		}).onChangeCreator((org, oldCreator, newCreator) -> {
			log.info("onChangeCreator,old:" + oldCreator.getMemberId() + ",new:" + newCreator.getMemberId());
		}).onAddJob((o, j) -> {
			log.info("onAddJob:" + j.getJobName());
		}).join(member2).registerJob(member1, job).giveMemberJob(member1, member2, 2).giveMemberJob(member1, member2,
				Job.NONE_JOB_ID);
	}
}
