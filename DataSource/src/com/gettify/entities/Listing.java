package com.gettify.entities;

import java.lang.reflect.Field;

import com.gettify.common.utils.Merchants;

public class Listing
{
	public String listingId = "";

	public String productId = "";

	// internal productId of the engine to be grabbed
	public String internalId = "";

	public String title = "";

	public Product product = null;

	public Category category = null;

	public float listedPrice = -1;

	public float price = -1;

	public float shippingPrice = -1;

	// 0 is unavailable, 1 is available, 2 is we have no information
	public int available = 2;

	public String currencyCode = "";

	public String urlFetched = "";

	public String productCondition = "";

	public String imageUrl = "";

	public int affiliateId = Merchants.DEFAULT;

	public long timeExpiring = -1;

	public long timeAdded = -1;

	public Listing()
	{
	}

	public Listing(String listingId, String productId, String internalId, String title, float listedPrice, float price, float shippingPrice, int available, String currencyCode,
			String urlFetched, String productCondition, String imageUrl, int affiliateId, long timeExpiring, long timeAdded)
	{
		this.listingId = listingId;
		this.productId = productId;
		this.internalId = internalId;
		this.title = title;
		this.listedPrice = listedPrice;
		this.price = price;
		this.shippingPrice = shippingPrice;
		this.available = available;
		this.currencyCode = currencyCode;
		this.urlFetched = urlFetched;
		this.productCondition = productCondition;
		this.imageUrl = imageUrl;
		this.affiliateId = affiliateId;
		this.timeExpiring = timeExpiring;
		this.timeAdded = timeAdded;
	}

	public String debugString()
	{
		StringBuilder sb = new StringBuilder("");
		Field[] fields = this.getClass().getFields();
		for (int i = 0; i < fields.length; i++)
		{
			String name = fields[i].getName();
			try
			{
				Field field = this.getClass().getDeclaredField(name);
				String val = String.valueOf(field.get(this));
				sb.append("{").append(name).append(" : ").append(val).append("}, ");
			}
			catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NoSuchFieldException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	@Override
	public String toString()
	{
		return "Listing [listingId=" + listingId + ", productId=" + productId + ", internalId=" + internalId + ", title=" + title + ", product=" + product + ", category="
				+ category + ", listedPrice=" + listedPrice + ", price=" + price + ", shippingPrice=" + shippingPrice + ", available=" + available + ", currencyCode="
				+ currencyCode + ", urlFetched=" + urlFetched + ", productCondition=" + productCondition + ", imageUrl=" + imageUrl + ", affiliateId=" + affiliateId
				+ ", timeExpiring=" + timeExpiring + ", timeAdded=" + timeAdded + "]";
	}
}
