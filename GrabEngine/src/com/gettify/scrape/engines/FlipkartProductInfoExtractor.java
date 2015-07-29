package com.gettify.scrape.engines;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.PageSourceReaderUtils;
import com.gettify.common.utils.Utils;
import com.gettify.db.tuples.CommonOtherInfoTuple;
import com.gettify.entities.Category;
import com.gettify.entities.Listing;
import com.gettify.entities.Product;
import com.gettify.scrape.urlgenerator.ProductInfoProcessor;

public class FlipkartProductInfoExtractor extends ProductInfoProcessor
{

	private static int MERCHANT_ID = Merchants.FLIPKART;

	private static String CURRENCY = "INR";

	private static String PRODUCT_CONDITION = "NEW";

	private static int readPoint = 0;

	private static int offset = 20;

	public FlipkartProductInfoExtractor()
	{
		super(10 * 1000, 0, readPoint, offset);
	}

	protected ArrayList<CommonOtherInfoTuple> getUrlList()
	{
		ArrayList<CommonOtherInfoTuple> list = new ArrayList<CommonOtherInfoTuple>();
		list.add(new CommonOtherInfoTuple("http://www.flipkart.com/mobiles/tablet-20278?response-type=json&layout=list&inf-start=%d", "Mobile Phones", "Tablets"));
		/*
		 * list.add(new CommonOtherInfoTuple( "http://www.flipkart.com/cameras/accessories/digital-photo-frame-20070?response-type=json&layout=list&inf-start=%d" , "Cameras",
		 * "Accessories"));
		 */
		return list;
	}

	protected ArrayList<Listing> manageProductInfo(String content, String url) throws Exception
	{
		JSONObject obj = new JSONObject(content);
		content = obj.getString("html");
		String regex = "<div class=\\\"fk-srch-item fk-inf-scroll-item\\\">" + ".*?" + "<a href=\\\"(.*?)\\\">" + ".*?" + "<img src=\\\"(.*?)\\\"" + ".*?" + "title=\\\"(.*?)\\\""
				+ ".*?" + "<\\/img>" + ".*?" + "Price:.*?\\\">" + "(.*?)" + "<\\/.*?>" + ".*?" + "<div class=\\\"line ship-det\\\">" + "(.*?)" + "<\\/div>" + ".*?" + "<\\/div>";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher matcher = pattern.matcher(content);

		boolean matchFound = false;
		ArrayList<Listing> listingsList = new ArrayList<Listing>();
		while (matcher.find())
		{
			matchFound = true;
			String categoryId = UUID.randomUUID().toString();
			String subCategoryId = UUID.randomUUID().toString();
			String mcatId = UUID.randomUUID().toString();

			String productUrl = matcher.group(1);
			float price = filterPrice(matcher.group(4));
			float shippingPrice = 0; // marking it 0 as of now
			String currencyCode = CURRENCY;
			String listingId = UUID.randomUUID().toString();
			String internalId = PageSourceReaderUtils.getParamVal(productUrl, "pid");
			int affiliateId = MERCHANT_ID;
			String urlFetched = "http://www.flipkart.com" + productUrl;

			int available = getAvailability(matcher.group(5));
			String productCondition = PRODUCT_CONDITION;
			String productName = matcher.group(3);
			String title = productName;

			String categoryName = super.getCategory();
			String subCategoryName = super.getSubCategory();
			int temp = productName.indexOf(" ");
			String mCatName = productName.substring(0, temp == -1 ? productName.length() : temp);

			String productId = UUID.randomUUID().toString();
			String brand = mCatName;
			long timeAdded = System.currentTimeMillis();
			String gtin = "";
			String gtinType = "";
			String imageUrl = matcher.group(2);
			String descrObject = "NA";
			String specsObject = "NA"; // no specs right now

			// make the product
			Product product = new Product(productId, productName, brand, gtin, gtinType, imageUrl, descrObject.toString(), timeAdded, mcatId, specsObject.toString());

			// make a category
			Category category = new Category(categoryId, categoryName, subCategoryId, subCategoryName, mcatId, mCatName);

			// make the listing
			Listing listing = new Listing(listingId, productId, internalId, title, price, price, shippingPrice, available, currencyCode, urlFetched, productCondition, imageUrl,
					affiliateId, -1, timeAdded);

			listing.product = product;
			listing.category = category;

			listingsList.add(listing);
		}
		if (!matchFound)
		{
			return null; // start reading next
		}
		return listingsList;
	}

	private float filterPrice(String priceHtml)
	{
		String s = new String();
		float val = -1;
		try
		{
			for (int i = 0; i < priceHtml.length(); i++)
			{
				if (Character.isDigit(priceHtml.charAt(i)))
				{
					s = priceHtml.substring(i);
					break;
				}
				val = Float.parseFloat(Utils.removeCommas(s).trim());
			}
		}
		catch (Exception e)
		{
			return val;
		}
		return val;
	}

	private int getAvailability(String status)
	{
		status = status.toLowerCase();
		if (status.contains("in stock") || status.contains("available"))
			return 1;
		else if (status.contains("out of stock"))
			return 0;
		else
			return 2;
	}

	public static void main(String[] args)
	{
		new FlipkartProductInfoExtractor();
	}

}
