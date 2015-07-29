package com.gettify.scrape.urlgenerator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.PageSourceReaderUtils;
import com.gettify.common.utils.Utils;
import com.gettify.db.handlers.ProductURLScrapeTableHandler;
import com.gettify.db.tuples.ProductURLScrapeInfoTuple;
import com.gettify.grab.scrape.ScrapeSense;
import com.gettify.processor.utils.Processor;

public abstract class URLScrape extends Processor<ProductURLScrapeInfoTuple>
{

	private volatile boolean keepReading = true;

	private ArrayList<String> uriList;

	private static int urlParsed = 0;

	private static String uri;

	private ScrapeSense[] scrapes;

	private int merchantId = Merchants.DEFAULT;

	private static int pageRead = 1;

	private static long startCronTime;

	private static ProductURLScrapeTableHandler dbInstance = ProductURLScrapeTableHandler.getInstance();

	private static int MAX_URL_REPEAT_LIMIT = 10;

	private static int REPEAT_URL = 0;

	public URLScrape(ArrayList<String> uriList, ScrapeSense[] scrapes, int merchantId)
	{
		super();
		this.uriList = uriList;
		this.scrapes = scrapes;
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
		uri = uriList.get(urlParsed);
		pageRead = 1;
		REPEAT_URL = 0;
		keepReading = true;
		startCronTime = System.currentTimeMillis();
	}

	@Override
	protected ArrayList<ProductURLScrapeInfoTuple> read() throws Exception
	{
		if (uri == null)
		{
			Utils.log("Sleeping for 60 secs as nothing to read");
			Thread.sleep(60 * 1000);
			super.stopReaderThread();
		}
		if (!keepReading)
		{
			Utils.log("Sleeping one time for 60 secs as nothing to read in uri = " + uri);
			Thread.sleep(60 * 1000);
			setBasicValuesOnUrlChange();
			return null;
		}
		Utils.log("Reading page = " + pageRead + " from uri=" + String.format(uri, pageRead));
		String content = PageSourceReaderUtils.getContent(String.format(uri, pageRead));
		ArrayList<ProductURLScrapeInfoTuple> list = new ArrayList<ProductURLScrapeInfoTuple>();
		for (int i = 0; i < scrapes.length; i++)
		{
			String regex = scrapes[i].startStr + ".*?" + scrapes[i].closeStr;
			Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNIX_LINES);
			Matcher matcher = pattern.matcher(content);
			while (matcher.find())
			{
				String productUrl = matcher.group(1);
				list.add(new ProductURLScrapeInfoTuple(productUrl, startCronTime, merchantId));
				System.out.println(productUrl);
			}
		}
		pageRead++;
		Utils.log("Read page = " + pageRead + " from uri=" + uriList);
		return list;
	}

	@Override
	protected void process(ProductURLScrapeInfoTuple object) throws Exception
	{
		if (object == null)
			return;
		ProductURLScrapeInfoTuple tuple = dbInstance.productUrlExists(object.getProductUrl());
		if (tuple == null)
			dbInstance.insertProductUri(object);
		else
		{
			// update table when it is crawled before 20 hours
			if (System.currentTimeMillis() - tuple.getCrawledTime() > (Utils.MILLISECONDSINADAY - 4 * Utils.MILLISECONDSINAHOUR))
				dbInstance.updateProductUri(tuple);
			else
				Utils.log("Object was updated before 20 hours" + object);
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
