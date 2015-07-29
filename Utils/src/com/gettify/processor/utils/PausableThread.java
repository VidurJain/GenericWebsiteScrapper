package com.gettify.processor.utils;

public abstract class PausableThread extends Thread
{
	private volatile boolean isStop = true;

	private volatile boolean paused = false;

	private int thread_sleepout = 1000;

	private String threadName = "DEFAULT_THREAD";

	public abstract void singleRun();

	public void setThreadName(String name)
	{
		this.threadName = name;
	}

	public String getThreadName()
	{
		return threadName;
	}

	/**
	 * use this for affiliates having rate limit
	 * 
	 * @param threadSleepout
	 */
	public void setThreadSleepout(int threadSleepout)
	{
		this.thread_sleepout = threadSleepout;
	}

	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
		{
			try
			{
				if (isStop)
				{
					System.out.println(threadName + " stopped");
					break;
				}
				if (paused)
				{
					Thread.sleep(thread_sleepout);
					continue;
				}
				this.singleRun();
				if (!isStop)
					Thread.sleep(thread_sleepout);
			}
			catch (Exception e)
			{
				System.out.println("Exception in Pausable Thread (" + threadName + ") - " + e);
			}
		}
	}

	public void pause()
	{
		if (!paused)
		{
			paused = true;
		}
	}

	public void unPause()
	{
		if (paused)
		{
			paused = false;
		}
	}

	public void doStop()
	{
		if (!isStop)
		{
			isStop = true;
			System.out.println(threadName + " will stop");
		}
	}

	public void doStart()
	{
		if (isStop)
		{
			isStop = false;
			System.out.println(threadName + " started");
		}
		this.start();
	}
}
