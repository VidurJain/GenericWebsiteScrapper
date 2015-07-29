package com.gettify.scrape.urlgenerator;

import java.util.ArrayList;

import com.gettify.common.utils.PageSourceReaderUtils;
import com.gettify.common.utils.Utils;
import com.gettify.db.tuples.CommonOtherInfoTuple;
import com.gettify.entities.Listing;
import com.gettify.entities.model.ListingsTableHandler;
import com.gettify.entities.model.PricetrendsTableHandler;
import com.gettify.entities.model.ProductsTableHandler;
import com.gettify.processor.utils.Processor;

public abstract class ProductInfoProcessor extends Processor<Listing>
{

	private int initReadPoint = 1;

	private int readPoint;

	private int offset;

	private static int DEFAULT_OFFSET = 1;

	private static int DEFAULT_READ_POINT = 1;

	private static int urlParsed = 0;

	private ArrayList<CommonOtherInfoTuple> uriList;

	private static String uri;

	private static String category;

	private static String subcategory;

	private static int SLEEP_AFTER_READING_URLS = 30 * 1000;

	private static int SLEEP_BETWEEN_READING_URLS = 30 * 1000;

	public ProductInfoProcessor()
	{
		super();
	}

	public ProductInfoProcessor(int readThreadSleepTime, int processorThreadSleeptime)
	{
		super(readThreadSleepTime, processorThreadSleeptime);
	}

	public ProductInfoProcessor(int readThreadSleepTime, int processorThreadSleeptime, int readPoint, int offset)
	{
		super(readThreadSleepTime, processorThreadSleeptime);
		this.readPoint = readPoint;
		this.initReadPoint = readPoint;
		this.offset = offset;
	}

	@Override
	protected void init()
	{
		this.uriList = getUrlList();
		uri = uriList.get(urlParsed).getUrl();
		category = uriList.get(urlParsed).getCategory();
		subcategory = uriList.get(urlParsed).getSubcategory();
		this.readPoint = DEFAULT_READ_POINT;
		this.offset = DEFAULT_OFFSET;
	}

	private void setBasicValuesOnUrlChange()
	{
		urlParsed++;
		if (uriList.size() > urlParsed)
		{
			uri = uriList.get(urlParsed).getUrl();
			category = uriList.get(urlParsed).getCategory();
			subcategory = uriList.get(urlParsed).getSubcategory();
		}
		else
			uri = null;
		readPoint = initReadPoint;
	}

	protected abstract ArrayList<CommonOtherInfoTuple> getUrlList();

	protected abstract ArrayList<Listing> manageProductInfo(String content, String url) throws Exception;

	@Override
	protected ArrayList<Listing> read() throws Exception
	{
		if (uri == null)
		{
			Utils.log("Sleeping for " + SLEEP_AFTER_READING_URLS + " as nothing to read");
			Thread.sleep(SLEEP_AFTER_READING_URLS);
			super.stopReaderThread();
			return null;
		}
		String urlToFetch = String.format(uri, readPoint);
		String content = PageSourceReaderUtils.getContent(urlToFetch);
		ArrayList<Listing> listing = manageProductInfo(content, urlToFetch);
		if (listing == null)
		{
			Utils.log("Sleeping one time for " + SLEEP_BETWEEN_READING_URLS + " as nothing to read in uri = " + uri);
			Thread.sleep(SLEEP_BETWEEN_READING_URLS);
			setBasicValuesOnUrlChange();
			return null;
		}
		readPoint = readPoint + offset;
		return listing;
	}

	@Override
	protected void process(Listing object) throws Exception
	{
		Listing listing = object;
		if (listing != null)
		{
			if (!ListingsTableHandler.getInstance().ifExistsListingByInternalId(listing))
			{
				ProductsTableHandler.getInstance().saveProduct(listing.product);
				ListingsTableHandler.getInstance().saveListing(listing);
			}
			if (listing.price >= 0)
			{
				ListingsTableHandler.getInstance().updateListing(listing);
				PricetrendsTableHandler.getInstance().savePricetrend(listing);
			}
			if (!ProductsTableHandler.getInstance().ifExistsCatByName(listing.category))
				ProductsTableHandler.getInstance().saveCategory(listing.category);

			if (!ProductsTableHandler.getInstance().ifExistsSubCatByName(listing.category))
				ProductsTableHandler.getInstance().saveSubCategory(listing.category);

			if (!ProductsTableHandler.getInstance().ifExistsMicroCatByName(listing.category))
				ProductsTableHandler.getInstance().saveMicroCategory(listing.category);

		}
	}

	public String getCategory()
	{
		return category;
	}

	public String getSubCategory()
	{
		return subcategory;
	}
}
