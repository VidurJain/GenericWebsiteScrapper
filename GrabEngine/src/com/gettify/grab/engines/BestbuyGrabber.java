package com.gettify.grab.engines;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gettify.common.utils.Merchants;
import com.gettify.entities.Listing;
import com.gettify.entities.Product;
import com.gettify.grab.GrabberCommon;

public class BestbuyGrabber extends GrabberCommon
{
	private String API_KEY = "mk7swu7mjn6a953mmz55th89";

	private final String pagesUrl = "http://api.remix.bestbuy.com/v1/products?apiKey=%s&page=%d";

	private int affiliateId = Merchants.BESTBUY;

	private int totalPages;

	private int currentPage;

	@Override
	public void init()
	{
		affiliateId = Merchants.BESTBUY;
		totalPages = 1;
		currentPage = 1;
		System.out.println("setting for " + this.affiliateId);
		this.setGrabberParams(affiliateId, 10000, 100);
	}

	@Override
	protected ArrayList<Listing> readObjects()
	{
		ArrayList<Listing> crawledListings = new ArrayList<Listing>();
		String urlToFetch = String.format(pagesUrl, API_KEY, currentPage);

		// RESET CURRENT PAGE IF ONE LOOP IS COMPLETE
		if (currentPage > totalPages)
			currentPage = 0;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			System.out.println("downloading " + urlToFetch);
			DocumentBuilder builder = factory.newDocumentBuilder();

			URL url = new URL(urlToFetch);
			InputStream stream = url.openStream();
			Document doc = builder.parse(stream);
			// normalize text representation
			doc.getDocumentElement().normalize();

			// GET PRODUCTS INFORMATION
			String tpages = doc.getDocumentElement().getAttribute("totalPages");
			totalPages = (int) Integer.valueOf(tpages);

			NodeList productsList = doc.getElementsByTagName("product");
			for (int j = 0; j < productsList.getLength(); j++)
			{
				try
				{
					Element pro = (Element) productsList.item(j);
					if (pro.getNodeType() == Node.ELEMENT_NODE)
					{
						Product product = new Product();
						try
						{
							product.productName = ((Node) ((Element) pro.getElementsByTagName("name").item(0)).getChildNodes().item(0)).getNodeValue().trim();

						}
						catch (Exception e)
						{
						}
						try
						{
							product.binding = ((Node) ((Element) pro.getElementsByTagName("type").item(0)).getChildNodes().item(0)).getNodeValue().trim();

						}
						catch (Exception e)
						{
						}
						try
						{
							product.brand = ((Node) ((Element) pro.getElementsByTagName("manufacturer").item(0)).getChildNodes().item(0)).getNodeValue().trim();

						}
						catch (Exception e)
						{
						}
						try
						{
							product.gtin = ((Node) ((Element) pro.getElementsByTagName("upc").item(0)).getChildNodes().item(0)).getNodeValue().trim();
							product.gtinType = "GTIN";
						}
						catch (Exception e)
						{
						}
						try
						{
							product.imageUrl = ((Node) ((Element) pro.getElementsByTagName("image").item(0)).getChildNodes().item(0)).getNodeValue().trim();

						}
						catch (Exception e)
						{
						}
						product.timeAdded = System.currentTimeMillis();

						Listing listing = new Listing();
						listing.product = product;
						listing.title = product.productName;
						try
						{
							listing.internalId = ((Node) ((Element) pro.getElementsByTagName("productId").item(0)).getChildNodes().item(0)).getNodeValue().trim();
						}
						catch (Exception e)
						{
						}
						try
						{
							listing.product.description = ((Node) ((Element) pro.getElementsByTagName("shortDescription").item(0)).getChildNodes().item(0)).getNodeValue().trim();
						}
						catch (Exception e)
						{
						}
						try
						{
							String price = ((Node) ((Element) pro.getElementsByTagName("salePrice").item(0)).getChildNodes().item(0)).getNodeValue().trim();
							listing.price = Float.valueOf(price);
							listing.currencyCode = "USD";
						}
						catch (Exception e)
						{
						}
						try
						{
							String price = ((Node) ((Element) pro.getElementsByTagName("regularPrice").item(0)).getChildNodes().item(0)).getNodeValue().trim();
							listing.listedPrice = Float.valueOf(price);
						}
						catch (Exception e)
						{
						}
						try
						{
							String price = ((Node) ((Element) pro.getElementsByTagName("shippingCost").item(0)).getChildNodes().item(0)).getNodeValue().trim();
							listing.shippingPrice = Float.valueOf(price);
						}
						catch (Exception e)
						{
						}
						try
						{
							listing.urlFetched = ((Node) ((Element) pro.getElementsByTagName("url").item(0)).getChildNodes().item(0)).getNodeValue().trim();
						}
						catch (Exception e)
						{
						}
						try
						{
							String preowned = ((Node) ((Element) pro.getElementsByTagName("preowned").item(0)).getChildNodes().item(0)).getNodeValue().trim();
							if (preowned == "false")
							{
								listing.productCondition = "used";
							}
							else
							{
								listing.productCondition = "new";
							}
						}
						catch (Exception e)
						{
							System.out.println("Exception while getting condition" + e);
						}
						listing.affiliateId = affiliateId;
						listing.imageUrl = product.imageUrl;
						listing.timeAdded = product.timeAdded;

						crawledListings.add(listing);
						// DEBUG STRING
						// System.out.println(product.debugString());
					}
				}

				catch (Exception e)
				{
					System.out.println("exception while read");
					e.printStackTrace();
				}

			}

		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * fetch content from this url and loop through to add all items to list for this page
		 */
		currentPage++;
		return crawledListings;
	}

	public static void main(String[] args)
	{
		BestbuyGrabber b = new BestbuyGrabber();
		b.run();
	}

}
