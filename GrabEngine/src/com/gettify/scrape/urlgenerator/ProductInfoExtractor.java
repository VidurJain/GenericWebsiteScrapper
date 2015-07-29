package com.gettify.scrape.urlgenerator;

import java.util.ArrayList;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.PageSourceReaderUtils;
import com.gettify.db.handlers.ProductURLScrapeTableHandler;
import com.gettify.db.tuples.ProductURLScrapeInfoTuple;
import com.gettify.entities.Listing;
import com.gettify.entities.model.ListingsTableHandler;
import com.gettify.entities.model.PricetrendsTableHandler;
import com.gettify.entities.model.ProductsTableHandler;
import com.gettify.processor.utils.Processor;

public abstract class ProductInfoExtractor extends Processor<ProductURLScrapeInfoTuple>
{

	private int merchantId = Merchants.DEFAULT;

	private int startRow = 0;

	private int limitRows = 100;

	@Override
	protected void init()
	{
	}

	public ProductInfoExtractor(int merchantId)
	{
		super();
		this.merchantId = merchantId;
	}

	@Override
	protected ArrayList<ProductURLScrapeInfoTuple> read() throws Exception
	{
		ArrayList<ProductURLScrapeInfoTuple> list = ProductURLScrapeTableHandler.getInstance().getProductUrlsByMerchant(merchantId, startRow, limitRows);
		if (list == null)
		{
			super.stopReaderThread();
			return null;
		}
		startRow = +limitRows;
		return list;
	}

	@Override
	protected void process(ProductURLScrapeInfoTuple object) throws Exception
	{
		String productUrl = object.getProductUrl();
		String content = PageSourceReaderUtils.getContent(productUrl);
		Listing listing = manageProductInfo(content, productUrl);
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

	protected abstract Listing manageProductInfo(String content, String url) throws Exception;
}
