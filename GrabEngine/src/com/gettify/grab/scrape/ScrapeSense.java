package com.gettify.grab.scrape;

public class ScrapeSense
{
	public String startStr;

	public String closeStr;

	public String field;

	/*
	 * field can be name, cat, subcat, microcat, gtin, gtintype, brand, imageurl, description, internalid, price, listedprice, shipping price, available, currencyCode, url,
	 * affiliateid, expiring
	 */
	public ScrapeSense(String start, String end, String field)
	{
		this.startStr = start;
		this.closeStr = end;
	}
}
