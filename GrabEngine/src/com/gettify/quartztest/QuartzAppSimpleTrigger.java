package com.gettify.quartztest;

import java.util.Date;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.core.SchedulerSignalerImpl;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class QuartzAppSimpleTrigger
{
	public static void main(String[] args) throws Exception
	{
		RunMeTask task = new RunMeTask();

		// specify your sceduler task details
		JobDetailImpl job = new JobDetailImpl();
		job.setName("runMeJob");
		job.setJobClass(RunMeJob.class);

		Map dataMap = job.getJobDataMap();
		dataMap.put("runMeTask", task);

		// configure the scheduler time
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		trigger.setName("runMeJobTesting");
		trigger.setStartTime(new Date(System.currentTimeMillis() + 1000));
		trigger.setRepeatCount(SimpleTriggerImpl.REPEAT_INDEFINITELY);
		trigger.setRepeatInterval(30000);

		// schedule it
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.scheduleJob(job, trigger);

	}
}
