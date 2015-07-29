package com.gettify.scrape.engines;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.Utils;
import com.gettify.db.tuples.CommonOtherInfoTuple;
import com.gettify.entities.Category;
import com.gettify.entities.Listing;
import com.gettify.entities.Product;
import com.gettify.scrape.urlgenerator.ProductInfoProcessor;

public class LetsBuyProductInfoEngine extends ProductInfoProcessor
{

	private static int MERCHANT_ID = Merchants.LETSBUY;

	private static String CURRENCY = "INR";

	private static String PRODUCT_CONDITION = "NEW";

	public LetsBuyProductInfoEngine()
	{
		super(10 * 1000, 0);
	}

	protected ArrayList<CommonOtherInfoTuple> getUrlList()
	{
		ArrayList<CommonOtherInfoTuple> list = new ArrayList<CommonOtherInfoTuple>();
		list.add(new CommonOtherInfoTuple("http://www.letsbuy.com/newarch/letsbuy_new/index.php/search/filterResult?c=254_393&pg=%d", "Mobile Phones", "Tablets"));
		/*
		 * list.add(new CommonOtherInfoTuple( "http://www.letsbuy.com/newarch/letsbuy_new/index.php/search/filterResult?c=255_485&pg=%d", "Digital Cameras", "Semi SLR"));
		 */
		return list;
	}

	protected ArrayList<Listing> manageProductInfo(String content, String url) throws Exception
	{
		JSONObject obj = new JSONObject(content);
		JSONArray array = null;
		try
		{
			array = obj.getJSONArray("result");
		}
		catch (Exception e)
		{
			Utils.log("Reached at the end of url. Start reading next url", e);
			return null;
		}
		ArrayList<Listing> listingsList = new ArrayList<Listing>();
		for (int i = 0; i < array.length(); i++)
		{
			JSONObject infoObj = (JSONObject) array.get(i);

			String categoryId = UUID.randomUUID().toString();
			String subCategoryId = UUID.randomUUID().toString();
			String mcatId = UUID.randomUUID().toString();

			float price = Float.parseFloat(infoObj.getString("products_price"));
			float shippingPrice = 0; // marking it 0 as of now
			String currencyCode = CURRENCY;
			String listingId = UUID.randomUUID().toString();
			String internalId = infoObj.getString("products_id");
			int affiliateId = MERCHANT_ID;
			String urlFetched = infoObj.getString("url");

			int available = infoObj.getInt("product_status");
			String productCondition = PRODUCT_CONDITION;
			String productName = infoObj.getString("products_name");
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
			String imageUrl = infoObj.getString("image_url");
			String descrObject = infoObj.getString("products_description");
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
		return listingsList;
	}

	public static void main(String[] args)
	{
		new LetsBuyProductInfoEngine();
	}

}
