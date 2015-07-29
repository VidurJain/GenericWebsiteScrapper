package com.gettify.scrape.urlgenerator;

import java.util.ArrayList;

import org.json.JSONException;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.Utils;
import com.gettify.db.handlers.ProductURLScrapeTableHandler;
import com.gettify.db.tuples.ProductURLScrapeInfoTuple;
import com.gettify.processor.utils.Processor;

public abstract class ProductUrlExtractor extends Processor<ProductURLScrapeInfoTuple>
{

	private static long startCronTime;

	private static ProductURLScrapeTableHandler dbInstance = ProductURLScrapeTableHandler.getInstance();

	private static int MAX_URL_REPEAT_LIMIT = 10;

	private static int REPEAT_URL = 0;

	private volatile boolean keepReading = true;

	private static int pageRead = 1;

	private static int urlParsed = 0;

	private ArrayList<String> uriList;

	private static String uri;

	private int merchantId = Merchants.DEFAULT;

	public ProductUrlExtractor(ArrayList<String> uriList, int merchantId)
	{
		super();
		this.uriList = uriList;
		this.merchantId = merchantId;
		uri = uriList.get(urlParsed);
	}

	@Override
	protected void init()
	{
		startCronTime = System.currentTimeMillis();
	}

	private void setBasicValuesOnUrlChange()
	{
		urlParsed++;
		if (uriList.size() > urlParsed)
			uri = uriList.get(urlParsed);
		else
			uri = null;
		pageRead = 1;
		REPEAT_URL = 0;
		keepReading = true;
		startCronTime = System.currentTimeMillis();
	}

	protected abstract ArrayList<String> getProductUrlList(String urlToFetch) throws JSONException;

	@Override
	protected ArrayList<ProductURLScrapeInfoTuple> read() throws Exception
	{
		if (uri == null)
		{
			Utils.log("Sleeping for 20 secs as nothing to read");
			Thread.sleep(20 * 1000);
			super.stopReaderThread();
			return null;
		}
		if (!keepReading)
		{
			Utils.log("Sleeping one time for 30 secs as nothing to read in uri = " + uri);
			Thread.sleep(30 * 1000);
			setBasicValuesOnUrlChange();
			return null;
		}
		String urlToFetch = String.format(uri, pageRead);
		Utils.log("Reading " + urlToFetch);
		ArrayList<String> urlList = getProductUrlList(urlToFetch);
		if (urlList == null)
		{
			keepReading = false;
			return null;
		}
		ArrayList<ProductURLScrapeInfoTuple> list = new ArrayList<ProductURLScrapeInfoTuple>();
		for (int i = 0; i < urlList.size(); i++)
		{
			list.add(new ProductURLScrapeInfoTuple(urlList.get(i), startCronTime, merchantId));
		}
		pageRead++;
		return list;
	}

	@Override
	protected void process(ProductURLScrapeInfoTuple object) throws Exception
	{
		ProductURLScrapeInfoTuple tuple = dbInstance.productUrlExists(object.getProductUrl());
		if (tuple == null)
			dbInstance.insertProductUri(object);
		else
		{
			// update table when it is crawled before 20 hours
			if (System.currentTimeMillis() - tuple.getCrawledTime() > (Utils.MILLISECONDSINADAY - 4 * Utils.MILLISECONDSINAHOUR))
				dbInstance.updateProductUri(tuple);
			else
				Utils.log("Product was updated before 20 hours. " + object);
			if (tuple.getCrawledTime() == startCronTime)
				REPEAT_URL++;
			if (REPEAT_URL == MAX_URL_REPEAT_LIMIT)
			{
				Utils.log("REPEAT_URL in this cron has reached its maximum limit. Looking for new urls");
				keepReading = false;
			}
		}

	}

}
