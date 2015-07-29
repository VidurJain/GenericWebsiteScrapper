package com.gettify.scrape.engines;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.Utils;
import com.gettify.db.tuples.CommonOtherInfoTuple;
import com.gettify.entities.Category;
import com.gettify.entities.Listing;
import com.gettify.entities.Product;
import com.gettify.scrape.urlgenerator.ProductInfoProcessor;

public class AdexMartProductInfoEngine extends ProductInfoProcessor
{

	private static int MERCHANT_ID = Merchants.ADEXMART;

	private static String CURRENCY = "INR";

	private static String PRODUCT_CONDITION = "NEW";

	public AdexMartProductInfoEngine()
	{
		super(10 * 1000, 0);
	}

	protected ArrayList<CommonOtherInfoTuple> getUrlList()
	{
		ArrayList<CommonOtherInfoTuple> list = new ArrayList<CommonOtherInfoTuple>();
		list.add(new CommonOtherInfoTuple("http://adexmart.com/modules/coremanager/modules/filtersearch/filtersearch.json.php?act=filter&ident=276&perpage=15&page=%d",
				"Mobile Phones", "Tablets"));
		// list.add(new
		// CommonOtherInfoTuple("http://adexmart.com/modules/coremanager/modules/filtersearch/filtersearch.json.php?act=filter&ident=16&perpage=15&page=%d","Mobile Phones",
		// "Mobiles"));
		return list;
	}

	protected ArrayList<Listing> manageProductInfo(String content, String url) throws Exception
	{
		String regex = "<li class=\"ajax_block_product" + ".*?" + "<span class=\"availability\">(.*?)</span>" + ".*?" + "<a href=\"(.*?)\".*?title=\"(.*?)\"" + ".*?"
				+ "<img src=\"(.*?)\"" + ".*?" + "<p class=\"product_desc\">[\t\n ]*" + "<a href=.*?>(.*?)</a>" + ".*?" + "<span class=\"price\".*?>(.*?)</span>" + ".*?"
				// + "id_product=([0-9]*)" + ".*?"
				+ "</li>";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNIX_LINES);
		JSONObject obj = new JSONObject(content);
		content = obj.getString("products");
		Matcher matcher = pattern.matcher(content);
		boolean matchFound = false;
		ArrayList<Listing> listingsList = new ArrayList<Listing>();
		while (matcher.find())
		{
			String categoryId = UUID.randomUUID().toString();
			String subCategoryId = UUID.randomUUID().toString();
			String mcatId = UUID.randomUUID().toString();

			String productUrl = matcher.group(2);
			float price = filterPrice(matcher.group(6));
			float shippingPrice = 0; // marking it 0 as of now
			String currencyCode = CURRENCY;
			String listingId = UUID.randomUUID().toString();
			String internalId = matcher.group(4).substring(1, matcher.group(4).indexOf("-")); // decoding this image url
			int affiliateId = MERCHANT_ID;
			String urlFetched = "" + productUrl;
			int available = getAvailability(matcher.group(1));
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
			String imageUrl = "http://adexmart.com" + matcher.group(4);
			String descrObject = matcher.group(5);
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

			matchFound = true;
		}
		if (!matchFound)
		{
			return null;
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
			}
		}
		catch (Exception e)
		{
			return val;
		}
		return Float.parseFloat(Utils.removeCommas(s).trim());
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
		new AdexMartProductInfoEngine();
	}

}
