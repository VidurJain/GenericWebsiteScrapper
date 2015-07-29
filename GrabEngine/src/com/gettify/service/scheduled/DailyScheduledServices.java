package com.gettify.service.scheduled;

import java.util.ArrayList;

import com.gettify.grab.GrabberCommon;
import com.gettify.grab.engines.LSWalmart;

public class DailyScheduledServices
{

	ArrayList<GrabberCommon> objects = null;

	public void initServices()
	{
		objects = new ArrayList<GrabberCommon>();
		// LSWalmart lsw = new LSWalmart(); objects.add(lsw);
	}

	public void startAllServices()
	{
		for (int i = 0; i < objects.size(); i++)
		{
			GrabberCommon obj = objects.get(i);
			if (obj.isStopped())
				obj.run();
		}
	}
}
