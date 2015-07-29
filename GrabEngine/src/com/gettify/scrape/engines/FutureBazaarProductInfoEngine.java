package com.gettify.scrape.engines;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.Utils;
import com.gettify.db.tuples.CommonOtherInfoTuple;
import com.gettify.entities.Category;
import com.gettify.entities.Listing;
import com.gettify.entities.Product;
import com.gettify.scrape.urlgenerator.ProductInfoProcessor;

public class FutureBazaarProductInfoEngine extends ProductInfoProcessor
{

	private static int MERCHANT_ID = Merchants.FUTUREBAZAAR;

	private static String CURRENCY = "INR";

	private static String PRODUCT_CONDITION = "NEW";

	Set<String> updatedProductsSet = new HashSet<String>();

	public FutureBazaarProductInfoEngine()
	{
		super(10 * 1000, 0);
	}

	protected ArrayList<CommonOtherInfoTuple> getUrlList()
	{
		ArrayList<CommonOtherInfoTuple> list = new ArrayList<CommonOtherInfoTuple>();
		list.add(new CommonOtherInfoTuple("http://www.futurebazaar.com/tablets/ch/2468/?pg=%d", "Mobile Phones", "Tablets"));
		return list;
	}

	protected ArrayList<Listing> manageProductInfo(String content, String url) throws Exception
	{
		String regex = "<div class=\"item_img ca\">[\t\n ]*<a href=\"(.*?)\">[\t\n ]*" + "<img src=\"(.*?)\".*?title=\"(.*?)\".*?</a>[\t\n ]*"
				+ "<a href=\"(.*?)\".*?<span class=\"offer_price.*?<span>(.*?)</span>.*?" + "<input type=\"hidden\" name=\"product_id\" value=\"(.*?)\"/>.*?</div>";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher matcher = pattern.matcher(content);
		boolean matchFound = false;
		ArrayList<Listing> listingsList = new ArrayList<Listing>();
		while (matcher.find())
		{
			String categoryId = UUID.randomUUID().toString();
			String subCategoryId = UUID.randomUUID().toString();
			String mcatId = UUID.randomUUID().toString();

			String productUrl = matcher.group(1);
			float price = Float.parseFloat(Utils.removeCommas(matcher.group(5)));
			float shippingPrice = 0; // marking it 0 as of now
			String currencyCode = CURRENCY;
			String listingId = UUID.randomUUID().toString();
			String internalId = matcher.group(6);
			int affiliateId = MERCHANT_ID;
			String urlFetched = "http://www.futurebazaar.com" + productUrl;
			String quickLookUrl = "http://www.futurebazaar.com" + matcher.group(4); // no use as of now
			int available = 1; // future bazaar product for now is assumed to
								// have all in stock products
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

			if (updatedProductsSet.contains(internalId))
			{
				updatedProductsSet.remove(internalId);
				continue;
			}
			else
				updatedProductsSet.add(internalId);

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
		if (updatedProductsSet.size() == 0)
		{
			return null; // case where page is updated
		}
		if (!matchFound)
		{
			return null;
		}
		return listingsList;
	}

	public static void main(String[] args)
	{
		new FutureBazaarProductInfoEngine();
	}
}
