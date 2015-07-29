package com.gettify.processor.utils;

public class TestPausableThread extends PausableThread
{

	@Override
	public void singleRun()
	{
		// System.out.println("thread will."+System.currentTimeMillis());
	}

	public static void main(String[] args)
	{
		TestPausableThread t = new TestPausableThread();
		t.doStart();
	}

}
