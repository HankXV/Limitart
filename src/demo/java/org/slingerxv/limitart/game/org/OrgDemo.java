package org.slingerxv.limitart.game.org;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.game.org.exception.AlreadyJoinException;
import org.slingerxv.limitart.game.org.exception.AuthIDErrorException;
import org.slingerxv.limitart.game.org.exception.JobIDDuplicatedException;
import org.slingerxv.limitart.game.org.exception.JobIDErrorException;
import org.slingerxv.limitart.game.org.exception.JobMemberMaxException;
import org.slingerxv.limitart.game.org.exception.JobNameEmptyException;
import org.slingerxv.limitart.game.org.exception.JobNotExistException;
import org.slingerxv.limitart.game.org.exception.NoAuthException;
import org.slingerxv.limitart.game.org.exception.OrgMaxMemberException;
import org.slingerxv.limitart.game.org.listener.IOrgEventListener;


public class OrgDemo {
	private static Logger log = LogManager.getLogger();

	public static void main(String[] args) throws AlreadyJoinException, OrgMaxMemberException, JobIDDuplicatedException,
			NoAuthException, JobMemberMaxException, AuthIDErrorException, JobNotExistException {
		Org org = new Org() {

			@Override
			public int capacity() {
				return 2;
			}
		};
		OrgMember member1 = new OrgMember();
		member1.setMemberId(1);
		OrgMember member2 = new OrgMember();
		member2.setMemberId(2);
		org.initOrg(1, member1, new IOrgEventListener() {

			@Override
			public void onRemoveJob(Org org, Job job) {
				log.info("onRemoveJob:" + job.getJobName());
			}

			@Override
			public void onQuit(Org org, OrgMember quiter) {
				log.info("onQuit:" + quiter.getMemberId());
			}

			@Override
			public void onJoin(Org org, OrgMember joiner) {
				log.info("onJoin:" + joiner.getMemberId());
			}

			@Override
			public void onChangeJob(Org org, OrgMember handler, OrgMember target, Job oldJob, Job newJob) {
				log.info("onChangeJob,old:" + (oldJob == null ? "null" : oldJob.getJobName()) + ",new:"
						+ (newJob == null ? "null" : newJob.getJobName()));
			}

			@Override
			public void onChangeCreator(Org org, OrgMember oldCreator, OrgMember newCreator) {
				log.info("onChangeCreator,old:" + oldCreator.getMemberId() + ",new:" + newCreator.getMemberId());
			}

			@Override
			public void onAddJob(Org org, Job job) {
				log.info("onAddJob:" + job.getJobName());
			}
		});
		org.join(member2);
		Job job = new Job();
		job.setJobName("总经理");
		job.setJobId(2);

		try {
			org.registerJob(member1, job);
		} catch (JobNameEmptyException | JobIDErrorException e) {
			e.printStackTrace();
		}
		org.giveMemberJob(member1, member2, 2);
		org.giveMemberJob(member1, member2, Job.NONE_JOB_ID);
	}
}
