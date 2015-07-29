package com.gettify.db.tuples;

public class ProductURLScrapeInfoTuple
{

	private String productUrl;

	private long crawledTime;

	private int merchantId;

	public ProductURLScrapeInfoTuple()
	{

	}

	public ProductURLScrapeInfoTuple(String productUrl, long crawledTime, int merchantId)
	{
		this.productUrl = productUrl;
		this.crawledTime = crawledTime;
		this.merchantId = merchantId;
	}

	public String getProductUrl()
	{
		return productUrl;
	}

	public void setProductUrl(String productUrl)
	{
		this.productUrl = productUrl;
	}

	public long getCrawledTime()
	{
		return crawledTime;
	}

	public void setCrawledTime(long crawledTime)
	{
		this.crawledTime = crawledTime;
	}

	public int getMerchantId()
	{
		return merchantId;
	}

	public void setMerchantId(int merchantId)
	{
		this.merchantId = merchantId;
	}

	@Override
	public String toString()
	{
		return "ProductURLScrapeInfoTuple [productUrl=" + productUrl + ", crawledTime=" + crawledTime + ", merchantId=" + merchantId + "]";
	}
}
