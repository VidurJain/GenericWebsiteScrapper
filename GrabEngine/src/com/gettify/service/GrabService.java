package com.gettify.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.gettify.grab.GrabberCommon;
import com.gettify.grab.engines.BestbuyGrabber;
import com.gettify.grab.engines.GoogleproductsUSGrabber;
import com.gettify.service.scheduled.DailyScheduledServices;
import com.gettify.service.scheduled.DailyScheduledServicesJob;

public class GrabService
{

	public void startAllServices()
	{
		ArrayList<GrabberCommon> objects = new ArrayList<GrabberCommon>();

		// LSWalmart lsw = new LSWalmart(); objects.add(lsw); lsw.run();
		BestbuyGrabber bbg = new BestbuyGrabber();
		objects.add(bbg);
		bbg.run();
		GoogleproductsUSGrabber gpusg = new GoogleproductsUSGrabber();
		objects.add(gpusg);
		gpusg.run();

	}

	public void triggerDailyScheduledServices() throws SchedulerException
	{
		System.out.println("Scheduling tasks");
		DailyScheduledServices task = new DailyScheduledServices();
		task.initServices();

		// specify your scheduler task details
		JobDetailImpl job = new JobDetailImpl();
		job.setName("scheduledServicesJob");
		job.setJobClass(DailyScheduledServicesJob.class);

		Map dataMap = job.getJobDataMap();
		dataMap.put("scheduledServicesTask", task);

		// configure the scheduler time
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		trigger.setName("scheduledServices");
		trigger.setStartTime(new Date(System.currentTimeMillis() + 1000));
		trigger.setRepeatCount(SimpleTriggerImpl.REPEAT_INDEFINITELY);
		trigger.setRepeatInterval(24 * 60 * 60 * 1000);

		// schedule it
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();
		scheduler.scheduleJob(job, trigger);
	}

	public static void main(String[] args)
	{
		GrabService gs = new GrabService();
		gs.startAllServices();
		/*
		 * try { gs.triggerDailyScheduledServices(); } catch (SchedulerException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */
	}
}
