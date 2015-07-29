package com.gettify.grab.engines;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gettify.common.utils.JsonReader;
import com.gettify.common.utils.Merchants;
import com.gettify.entities.Listing;
import com.gettify.entities.Product;
import com.gettify.grab.GrabberCommon;

public class GoogleproductsUSGrabber extends GrabberCommon
{
	private String API_KEY = "AIzaSyA0nKM1brRS62CiOqoLOgtURSCem065x_g";

	private final String pagesUrl = "https://www.googleapis.com/shopping/search/v1/public/products?key=%s&country=US&startIndex=%d&maxResults=%d&alt=json";

	private int affiliateId = Merchants.GOOGLE_PRODUCTS;

	private int totalItems;

	private int startIndex;

	private int itemsPerPage;

	@Override
	public void init()
	{
		totalItems = 1;
		startIndex = 1;
		itemsPerPage = 25;
		affiliateId = Merchants.GOOGLE_PRODUCTS;
		System.out.println("setting for " + this.affiliateId);
		this.setGrabberParams(affiliateId, 50000, 100);
	}

	@Override
	protected ArrayList<Listing> readObjects()
	{
		System.out.println("reading objects now");
		ArrayList<Listing> crawledListings = new ArrayList<Listing>();
		String urlToFetch = String.format(pagesUrl, API_KEY, startIndex, itemsPerPage);
		// RESET START INDEX IF ONE LOOP IS COMPLETE
		if (startIndex > totalItems)
			startIndex = 1;

		try
		{
			System.out.println("downloading " + urlToFetch);
			JSONObject js = JsonReader.readJsonFromUrl(urlToFetch);

			totalItems = js.getInt("totalItems");
			JSONArray items = js.getJSONArray("items");
			for (int j = 0; j < items.length(); j++)
			{
				try
				{
					JSONObject i = items.getJSONObject(j);
					Product product = new Product();
					try
					{
						product.productName = ((JSONObject) i.get("product")).getString("title");
					}
					catch (Exception e)
					{
					}
					try
					{
						product.brand = ((JSONObject) i.get("product")).getString("brand");
					}
					catch (Exception e)
					{
					}
					try
					{
						product.gtin = ((JSONObject) i.get("product")).getString("gtin");
						product.gtinType = "GTIN";
					}
					catch (Exception e)
					{
					}
					try
					{
						product.imageUrl = ((JSONObject) i.get("product")).getJSONArray("images").getString(0);
					}
					catch (Exception e)
					{
					}
					product.timeAdded = System.currentTimeMillis();

					Listing listing = new Listing();
					listing.product = product;
					try
					{
						listing.internalId = ((JSONObject) i.get("product")).getString("googleId");
					}
					catch (Exception e)
					{
					}
					try
					{
						listing.title = product.productName;
					}
					catch (Exception e)
					{
					}
					try
					{
						listing.product.description = ((JSONObject) i.get("product")).getString("description");
					}
					catch (Exception e)
					{
					}
					try
					{
						listing.listedPrice = (float) ((JSONObject) ((JSONObject) i.get("product")).get("inventories")).getDouble("price");
						listing.price = listing.listedPrice;
					}
					catch (Exception e)
					{
					}
					try
					{
						listing.shippingPrice = (float) ((JSONObject) ((JSONObject) i.get("product")).get("inventories")).getDouble("shipping");
					}
					catch (Exception e)
					{
					}
					try
					{
						listing.currencyCode = ((JSONObject) ((JSONObject) i.get("product")).get("inventories")).getString("currency");
					}
					catch (Exception e)
					{
					}
					try
					{
						listing.urlFetched = ((JSONObject) i.get("product")).getString("link");
					}
					catch (Exception e)
					{
					}
					try
					{
						listing.productCondition = ((JSONObject) i.get("product")).getString("condition");
					}
					catch (Exception e)
					{
					}
					listing.imageUrl = product.imageUrl;
					try
					{
						String author = ((JSONObject) ((JSONObject) i.get("product")).get("author")).getString("name");
						// listing.affiliateId = author.toLowerCase();//TODO: parag, check this
					}
					catch (Exception e)
					{
					}
					listing.timeAdded = product.timeAdded;
					crawledListings.add(listing);
				}
				catch (Exception e)
				{
					System.out.println("exception while read" + e);
				}

			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		startIndex += itemsPerPage;
		return crawledListings;
	}

	public static void main(String[] args)
	{
		GoogleproductsUSGrabber gpg = new GoogleproductsUSGrabber();
		gpg.run();
	}

}
