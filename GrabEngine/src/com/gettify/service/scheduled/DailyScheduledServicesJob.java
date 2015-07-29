package com.gettify.service.scheduled;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DailyScheduledServicesJob implements Job
{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		Map dataMap = context.getJobDetail().getJobDataMap();
		DailyScheduledServices task = (DailyScheduledServices) dataMap.get("scheduledServicesTask");
		task.startAllServices();
	}

}
