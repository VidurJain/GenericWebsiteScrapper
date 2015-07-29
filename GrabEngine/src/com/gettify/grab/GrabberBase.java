package com.gettify.grab;

public interface GrabberBase
{
	/*
	 * initiates grabber with a given seed through which system will start picking listings
	 */
	public void init();

	public void run();

	public void pause();

	public void unPause();

	public void stop();

	public boolean isPaused();

	public boolean isStopped();
}
