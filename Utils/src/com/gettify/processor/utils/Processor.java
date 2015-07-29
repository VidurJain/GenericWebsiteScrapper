package com.gettify.processor.utils;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import com.gettify.common.utils.Utils;

public abstract class Processor<E>
{
	private ProcessThread processorThread;

	private ReaderThread readerThread;

	private LinkedBlockingQueue<E> queue;

	private static int DEFAULT_Q_SIZE = 1000;

	private static volatile boolean readerThreadStopped = false;

	private static volatile boolean processorThreadStopped = false;

	private static int DEFAULT_READ_THREAD_SLEEP = 5 * 60 * 1000; // 5 minutes

	private static int DEFAULT_PROCESSOR_THREAD_SLEEP = 5 * 60 * 1000; // 5
																		// minutes

	private int processorThreadSleepTime = -1;

	private int readThreadSleepTime = -1;

	public Processor(int size)
	{
		this(size, DEFAULT_READ_THREAD_SLEEP, DEFAULT_PROCESSOR_THREAD_SLEEP);
	}

	public Processor()
	{
		this(DEFAULT_Q_SIZE, DEFAULT_READ_THREAD_SLEEP, DEFAULT_PROCESSOR_THREAD_SLEEP);
	}

	public Processor(int readThreadSleepTime, int processorThreadSleeptime)
	{
		this(DEFAULT_Q_SIZE, readThreadSleepTime, processorThreadSleeptime);
	}

	public Processor(int size, int readThreadSleepTime, int processorThreadSleeptime)
	{
		init();
		queue = new LinkedBlockingQueue<E>(size);
		readerThread = new ReaderThread();
		processorThread = new ProcessThread();
		readerThread.doStart();
		processorThread.doStart();
		this.readThreadSleepTime = readThreadSleepTime;
		this.processorThreadSleepTime = processorThreadSleeptime;
	}

	protected abstract void init();

	private class ReaderThread extends PausableThread
	{
		@Override
		public void singleRun()
		{
			try
			{
				ArrayList<E> list = read();
				if (list == null)
					return;
				for (E l : list)
				{
					try
					{
						queue.put(l);
					}
					catch (InterruptedException e)
					{
						Utils.log("Reader Thread Interrupted", e);
					}
				}
				if (readThreadSleepTime > 0)
				{
					Utils.log("Reader thread Sleeping for " + readThreadSleepTime);
					Thread.sleep(readThreadSleepTime);
				}
			}
			catch (Exception e)
			{
				Utils.log("Exception while reading thread", e);
			}
		}
	}

	protected abstract ArrayList<E> read() throws Exception;

	private class ProcessThread extends PausableThread
	{

		@Override
		public void singleRun()
		{
			E object = queue.poll();
			try
			{
				if (object != null)
					process(object);
				if (queue.isEmpty() && isReaderThreadStopped())
					stopProcessingThread();
				if (processorThreadSleepTime > 0)
				{
					Utils.log("Processor thread Sleeping for " + processorThreadSleepTime);
					Thread.sleep(processorThreadSleepTime);
				}
			}
			catch (Exception e)
			{
				Utils.log("Exception while processing object" + object, e);
			}
		}

	}

	protected abstract void process(E object) throws Exception;

	public void stopReaderThread()
	{
		Utils.log("Stopping reader Threads");
		if (readerThread != null)
		{
			readerThread.doStop();
			readerThread = null;
			readerThreadStopped = true;
		}
		Utils.log("Reader Threads stopped");
	}

	public void stopProcessingThread()
	{
		Utils.log("Processor reader Threads");
		if (processorThread != null)
		{
			processorThread.doStop();
			processorThread = null;
			processorThreadStopped = true;
		}
		Utils.log("Processor Threads stopped");
	}

	public boolean isReaderThreadStopped()
	{
		return readerThreadStopped;
	}

	public boolean isProcessorThreadStopped()
	{
		return processorThreadStopped;
	}
}