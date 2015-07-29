package com.gettify.scrape.engines;

import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.PageSourceReaderUtils;
import com.gettify.common.utils.Utils;
import com.gettify.db.tuples.CommonOtherInfoTuple;
import com.gettify.entities.Category;
import com.gettify.entities.Listing;
import com.gettify.entities.Product;
import com.gettify.scrape.urlgenerator.ProductInfoProcessor;

public class InfiBeamProductInfoEngine extends ProductInfoProcessor
{

	private static int MERCHANT_ID = Merchants.INFIBEAM;

	private static String CURRENCY = "INR";

	private static String PRODUCT_CONDITION = "NEW";

	public InfiBeamProductInfoEngine()
	{
		super(10 * 1000, 0);
	}

	protected ArrayList<CommonOtherInfoTuple> getUrlList()
	{
		ArrayList<CommonOtherInfoTuple> list = new ArrayList<CommonOtherInfoTuple>();
		list.add(new CommonOtherInfoTuple("http://www.infibeam.com/Portable_Electronics/Search_ajax.action?bodyType=Tablet&store=Portable_Electronics&page=%d", "Mobile Phones",
				"Tablets"));
		return list;
	}

	protected ArrayList<Listing> manageProductInfo(String content, String url) throws Exception
	{
		String regex = "<li>[\t\n ]*<a href=\"(.*?)\"" + ".*?" + "<img src=\"(.*?)\"" + ".*?" + "<span class=\"title\">(.*?)</span>" + ".*?" + "<div class=\"price\">" + "(.*?)"
				+ "</div>" + ".*?" + "</li>";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher matcher = pattern.matcher(content);
		boolean matchFound = false;
		ArrayList<Listing> listingsList = new ArrayList<Listing>();
		while (matcher.find())
		{
			String categoryId = UUID.randomUUID().toString();
			String subCategoryId = UUID.randomUUID().toString();
			String mcatId = UUID.randomUUID().toString();
			String productUrl = matcher.group(1).replace("&amp;", "&");
			String priceString = PageSourceReaderUtils.removeHtmlTags(matcher.group(4)).trim();
			float price = filterPrice(priceString);
			float shippingPrice = 0; // marking it 0 as of now
			String currencyCode = CURRENCY;
			String listingId = UUID.randomUUID().toString();
			String internalId = PageSourceReaderUtils.getParamVal(productUrl, "listingId");
			int affiliateId = MERCHANT_ID;
			String urlFetched = "http://www.infibeam.com" + productUrl;
			int available = 1; // TODO: assuming it to be avaliable for now,
								// need to be fetched from the content of
								// product url
			String productCondition = PRODUCT_CONDITION;
			String productName = matcher.group(3).trim();
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
		priceHtml = priceHtml.replaceAll("[\n\t ]", ";");
		int index = priceHtml.lastIndexOf(";");
		return Float.parseFloat(Utils.removeCommas(priceHtml.substring(index + 1)));
	}

	public static void main(String[] args)
	{
		new InfiBeamProductInfoEngine();
	}

}
