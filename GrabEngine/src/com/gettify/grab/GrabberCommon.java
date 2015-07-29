package com.gettify.grab;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import com.gettify.entities.Listing;
import com.gettify.entities.model.ListingsTableHandler;
import com.gettify.entities.model.PricetrendsTableHandler;
import com.gettify.entities.model.ProductsTableHandler;
import com.gettify.processor.utils.PausableThread;

public abstract class GrabberCommon implements GrabberBase
{
	private volatile boolean isStarted = false;

	private volatile boolean isRunning = false;

	private int MAX_LISTINGS = 1000;

	private LinkedBlockingQueue<Listing> listings;

	private ReaderThread readerThread;

	private ProcessThread processorThread;

	public GrabberCommon()
	{
		super();
		listings = new LinkedBlockingQueue<Listing>(MAX_LISTINGS);
		readerThread = new ReaderThread();
		processorThread = new ProcessThread();
		readerThread.setThreadName("Reader Thread");
		processorThread.setThreadName("Processor Thread");
		this.init();
	}

	public void setGrabberParams(int processName, int readSleep, int processSleep)
	{
		readerThread.setThreadName(processName + " - Reader Thread");
		processorThread.setThreadName(processName + " - Processor Thread");
		readerThread.setThreadSleepout(readSleep);
		processorThread.setThreadSleepout(processSleep);
	}

	@Override
	public void run()
	{
		isStarted = true;
		readerThread.doStart();
		processorThread.doStart();
	}

	@Override
	public void pause()
	{
		readerThread.pause();
		processorThread.pause();
		isRunning = false;
	}

	@Override
	public void unPause()
	{
		readerThread.unPause();
		processorThread.unPause();
		isRunning = true;
	}

	@Override
	public void stop()
	{
		readerThread.doStop();
		processorThread.doStop();
		isRunning = false;
		isStarted = false;
	}

	@Override
	public boolean isPaused()
	{
		return (isStarted && !isRunning);
	}

	@Override
	public boolean isStopped()
	{
		return (!isStarted);
	}

	protected abstract ArrayList<Listing> readObjects();

	/*
	 * productid and listingid needs to be created in this function only
	 */
	protected void processObject(Listing listing)
	{
		System.out.println("Should be implemented here");
	}

	private class ReaderThread extends PausableThread
	{

		@Override
		public void singleRun()
		{
			ArrayList<Listing> list = readObjects();
			if (list == null)
				return;
			for (Listing l : list)
			{
				try
				{
					listings.put(l);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	private class ProcessThread extends PausableThread
	{

		@Override
		public void singleRun()
		{
			Listing listing = listings.poll();
			if (listing != null)
			{
				if (!ListingsTableHandler.getInstance().ifExistsListingByInternalId(listing))
				{
					if (!ProductsTableHandler.getInstance().ifExistsProductByGtin(listing.product))
					{
						if (listing.product.gtin != "")
						{
							listing.product.productId = UUID.randomUUID().toString();
							listing.productId = listing.product.productId;
							listing.listingId = UUID.randomUUID().toString();
							try
							{
								ProductsTableHandler.getInstance().saveProduct(listing.product);
							}
							catch (Exception e)
							{
								// TODO Auto-generated catch block
								listing.product.productId = "";
								e.printStackTrace();
							}
						}
					}
					try
					{
						ListingsTableHandler.getInstance().saveListing(listing);
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try
				{
					if (listing.price >= 0)
					{
						ListingsTableHandler.getInstance().updateListing(listing);
						PricetrendsTableHandler.getInstance().savePricetrend(listing);
					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	};
}
