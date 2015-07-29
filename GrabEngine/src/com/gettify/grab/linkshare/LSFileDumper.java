package com.gettify.grab.linkshare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.gettify.entities.Listing;
import com.gettify.entities.Product;
import com.gettify.entities.model.ListingsTableHandler;
import com.gettify.entities.model.PricetrendsTableHandler;
import com.gettify.entities.model.ProductsTableHandler;
import com.gettify.files.utils.BufferedFileSplitter;

public abstract class LSFileDumper
{
	BufferedFileSplitter bfs;

	Boolean isEnded = false;

	private static String fileRoot = "/mnt/lsfiles/";

	private int readLines = 0;

	protected abstract String getFileName();

	protected abstract int getAffiliateId();

	public void init()
	{
		System.out.println(fileRoot);
		File file = new File(fileRoot + getFileName());
		System.out.println("reading file for " + getAffiliateId() + " " + getFileName());
		try
		{
			bfs = new BufferedFileSplitter(file);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			isEnded = false;
		}
		this.dumpFile();
	}

	public void dumpFile()
	{
		ArrayList<Listing> listings = getFilePart();
		int i = 0;
		while (listings != null)
		{
			Listing listing = listings.get(i);
			i++;
			if (listing != null)
			{
				System.out.println("getting listing for " + listing.title);
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
			if (i == listings.size())
			{
				listings = getFilePart();
				i = 0;
			}
		}
	}

	public ArrayList<Listing> getFilePart()
	{
		ArrayList<Listing> crawledListings = new ArrayList<Listing>();
		try
		{
			ArrayList<String> strngs = bfs.getNextRecords(10);
			if (strngs.size() == 0)
			{
				return null;
			}
			for (String str : strngs)
			{
				try
				{
					String[] entities = str.split("\\|");
					if (entities[0].contains("HDR"))
						continue;
					for (int i = 0; i < entities.length; i++)
					{
						entities[i] = entities[i].trim();
					}

					Product p = new Product();
					p.productName = entities[1];
					p.binding = entities[3];
					if (entities[4] != "")
					{
						p.binding = entities[4];
					}
					p.brand = entities[15];
					p.gtin = entities[23];
					p.gtinType = "UPC";
					p.imageUrl = entities[6];
					p.timeAdded = System.currentTimeMillis();

					Listing l = new Listing();
					l.product = p;
					l.internalId = String.valueOf(entities[0]);
					l.title = p.productName;
					p.description = entities[8];
					if (entities[9] == "")
					{
						p.description = entities[9];
					}
					if (!entities[13].equals("") || entities[13].length() > 0)
					{
						try
						{
							l.price = Float.parseFloat(entities[13]);
							l.listedPrice = l.price;
						}
						catch (NumberFormatException e)
						{
							System.out.println("number format exception");
						}
					}
					if (!entities[12].equals("") || entities[12].length() > 0)
					{
						try
						{
							l.listedPrice = Float.parseFloat(entities[12]);
						}
						catch (NumberFormatException e)
						{
							// System.out.println("number format exception");
						}
					}
					if (!entities[17].equals("") || entities[17].length() > 0)
					{
						l.shippingPrice = Float.parseFloat(entities[17]);
					}
					else
					{
						l.shippingPrice = 0f;
					}

					// TODO: AVAILABILITY??

					l.currencyCode = entities[25];
					l.urlFetched = entities[5];
					// TODO: l.productCondition = "";
					l.imageUrl = entities[6];
					l.affiliateId = this.getAffiliateId();
					// TODO: timeExpiring
					/*
					 * if(entities[15]!="") { l.timeExpiring = entities[15]; }
					 */
					l.timeAdded = p.timeAdded;
					crawledListings.add(l);
				}
				catch (Exception e)
				{
					System.out.println("exception while read " + e);
					e.printStackTrace();
				}
			}

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crawledListings;
	}

}
