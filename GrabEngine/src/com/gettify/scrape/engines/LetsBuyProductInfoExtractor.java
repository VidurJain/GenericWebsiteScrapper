package com.gettify.scrape.engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.PageSourceReaderUtils;
import com.gettify.common.utils.Utils;
import com.gettify.entities.Category;
import com.gettify.entities.Listing;
import com.gettify.entities.Product;
import com.gettify.grab.scrape.ScrapeSense;
import com.gettify.scrape.urlgenerator.ProductInfoExtractor;

public class LetsBuyProductInfoExtractor extends ProductInfoExtractor
{

	private static int MERCHANT_ID = Merchants.LETSBUY;

	private static String CURRENCY = "INR";

	private static String PRODUCT_CONDITION = "NEW";

	public LetsBuyProductInfoExtractor()
	{
		super(MERCHANT_ID);
	}

	public void start()
	{

	}

	@Override
	protected Listing manageProductInfo(String content, String url) throws Exception
	{

		String categoryName = getCategory(content);
		String subCategoryName = getSubCategory(content);
		String mCatName = getMicroCategory(content);

		String categoryId = UUID.randomUUID().toString();
		String subCategoryId = UUID.randomUUID().toString();
		String mcatId = UUID.randomUUID().toString();

		float price = getPrice(content);
		float shippingPrice = getShippingPrice(content);
		String currencyCode = CURRENCY;
		String listingId = UUID.randomUUID().toString();
		String internalId = getInternalId(content);
		int affiliateId = MERCHANT_ID;
		int available = getAvailability(content);
		String productCondition = PRODUCT_CONDITION;
		String urlFetched = url;
		String productName = getProductName(content);
		String title = productName;

		String productId = UUID.randomUUID().toString();
		String brand = mCatName;
		long timeAdded = System.currentTimeMillis();
		String gtin = getGtin(content);
		String gtinType = getGtinType(content);
		String imageUrl = getImageUrl(content);
		JSONObject descrObject = getDescr(content);
		JSONObject specsObject = getSpecs(content);

		// make the product
		Product product = new Product(productId, productName, brand, gtin, gtinType, imageUrl, descrObject.toString(), timeAdded, mcatId, specsObject.toString());

		// make a category
		Category category = new Category(categoryId, categoryName, subCategoryId, subCategoryName, mcatId, mCatName);

		// make the listing
		Listing listing = new Listing(listingId, productId, internalId, title, price, price, shippingPrice, available, currencyCode, urlFetched, productCondition, imageUrl,
				affiliateId, -1, timeAdded);

		listing.product = product;
		listing.category = category;

		return listing;
	}

	private float getPrice(String content)
	{
		ScrapeSense ss = getPriceScrape();
		String price = filterPrice(getValue(ss, content, "-1"));
		return Float.parseFloat(price);
	}

	private ScrapeSense getPriceScrape()
	{
		return new ScrapeSense("<span class=\"offer_price\">", "</span>", "name");
	}

	private String filterPrice(String priceHtml)
	{
		String s = new String();
		for (int i = 0; i < priceHtml.length(); i++)
		{
			if (Character.isDigit(priceHtml.charAt(i)))
			{
				s = priceHtml.substring(i);
				break;
			}
		}
		return Utils.removeCommas(s).trim();
	}

	private float getShippingPrice(String content)
	{
		return 0;
	}

	private String getProductName(String content)
	{
		ScrapeSense ss = getProductNameScrape();
		String productName = getValue(ss, content, "NA");
		return productName;
	}

	private ScrapeSense getProductNameScrape()
	{
		return new ScrapeSense("<h1 class=\"prod_name\">", "</h1>", "name");
	}

	private String getGtin(String content)
	{
		return "";
	}

	private String getGtinType(String content)
	{
		return "";
	}

	private String getImageUrl(String content)
	{
		ScrapeSense ss = getImageScrap();
		return getValue(ss, content, "");
	}

	private ScrapeSense getImageScrap()
	{
		return new ScrapeSense("<img id=\"productMainImage\" src=\"" + "([^\"]+)" + "\"[^>]*>", "/>", "name");
	}

	private String getInternalId(String content)
	{
		ScrapeSense ss = getInternalIdScrape();
		return getValue(ss, content, getProductName(content));
	}

	private ScrapeSense getInternalIdScrape()
	{
		return new ScrapeSense("<span class=\"nodisplay addToProductID\">", "</span>", "name");
	}

	private String getCategory(String content)
	{
		ScrapeSense ss = getCategoryScrape();
		return getValue(ss, content, "");
	}

	private ScrapeSense getCategoryScrape()
	{
		return new ScrapeSense("<p class=\"breadcrumb\">" + "<a.*?</a>" + "<a href=.*?>" + "(.*?)" + "</a>", "</p>", "name");
	}

	private String getSubCategory(String content)
	{
		ScrapeSense ss = getSubCategoryScrape();
		return getValue(ss, content, "");
	}

	private ScrapeSense getSubCategoryScrape()
	{
		return new ScrapeSense("<p class=\"breadcrumb\">" + "<a.*?</a>" + "<a.*?</a>" + "<a href=.*?>" + "(.*?)" + "</a>", "</p>", "name");
	}

	private String getMicroCategory(String content)
	{
		ScrapeSense ss = getMicroCategoryScrape();
		return getValue(ss, content, "");
	}

	private ScrapeSense getMicroCategoryScrape()
	{
		return new ScrapeSense("<p class=\"breadcrumb\">" + "<a.*?</a>" + "<a.*?</a>" + "<a.*?</a>" + "<a href=.*?>" + "(.*?)" + "</a>", "</p>", "name");
	}

	private int getAvailability(String content)
	{
		ScrapeSense ss = getAvailabilityScrape();
		String cartOptions = getValue(ss, content, "");
		if (cartOptions.toLowerCase().contains("out of stock"))
			return 0;
		if (cartOptions.toLowerCase().contains("buy it now"))
			return 1;
		return 2;
	}

	private ScrapeSense getAvailabilityScrape()
	{
		return new ScrapeSense("<div class=\"add_to_cart\">", "</div>", "");
	}

	private JSONObject getDescr(String content) throws JSONException
	{
		JSONObject obj = new JSONObject();
		obj.put("Key Features", getFeatureList(content));
		obj.put("Overview", getOverviewList(content));
		return obj;
	}

	private ArrayList<String> getFeatureList(String content)
	{
		ArrayList<String> keyFeaturesList = new ArrayList<String>();
		String featureHtml = getValue(new ScrapeSense("<div id=\"product-feature\">", "</div>", "name"), content, "");
		getAllValues(new ScrapeSense("&bull;", "</span>", "name"), featureHtml, keyFeaturesList);
		return keyFeaturesList;
	}

	private ArrayList<String> getOverviewList(String content)
	{
		ArrayList<String> overviewFeaturesList = new ArrayList<String>();
		String featureHtml = getValue(new ScrapeSense("<div id=\"product-overview\">", "</div>", "name"), content, "");
		featureHtml = PageSourceReaderUtils.replaceHtmlTags(featureHtml);
		// featureHtml = PageSourceReaderUtils.replaceAmpChars(featureHtml);
		overviewFeaturesList.add(featureHtml.trim());
		return overviewFeaturesList;
	}

	private JSONObject getSpecs(String content) throws JSONException
	{
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		JSONObject obj = getJsonFromPattern1(content, map);
		// product page follows other pattern if obj is null
		if (obj == null)
			obj = getJsonFromPattern2(content, map);
		return obj;
	}

	private JSONObject getJsonFromPattern1(String content, HashMap<String, ArrayList<String>> map)
	{
		ScrapeSense ss = new ScrapeSense("<span style=\"color: black;\">", "</span>", "name");
		String regex = ss.startStr + "(.*?)" + ss.closeStr;
		Pattern patternStyle1 = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher matcherStyle1 = patternStyle1.matcher(content);
		// this regex will be the header
		String headerRegex = "<b>" + ss.startStr + "(.*?)" + ss.closeStr;
		Pattern headerPattern = Pattern.compile(headerRegex, Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher headerMatcher = headerPattern.matcher(content);
		if (!headerMatcher.find())
			return null; // if pattern foes not match, return null
		String headerVal = headerMatcher.group(1);
		String key = "";
		String value = "";
		ArrayList<String> list = new ArrayList<String>();
		map.put(key, list);
		while (matcherStyle1.find())
		{
			if (headerVal.equals(matcherStyle1.group(1)))
			{
				key = headerVal;
				if (headerMatcher.find())
					headerVal = headerMatcher.group(1);
				list = new ArrayList<String>();
				map.put(key, list);
				continue;
			}
			if (matcherStyle1.group(1).contains(":"))
				value += matcherStyle1.group(1);
			else
			{
				list = map.get(key);
				list.add(value + matcherStyle1.group(1));
				value = "";
			}
		}
		return new JSONObject(map);
	}

	private JSONObject getJsonFromPattern2(String content, HashMap<String, ArrayList<String>> map)
	{
		String specsRegex = "<table .*? class=\"specification_table\">" + "(.*?)" + "</table>";
		Pattern specsPattern = Pattern.compile(specsRegex, Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher specsMatcher = specsPattern.matcher(content);
		String specContent = "";
		if (specsMatcher.find())
			specContent = specsMatcher.group(1);
		else
			return null;

		specContent = specContent.replaceAll("class=\"specheadingtxt\">", "class=\"specheadingtxt\">" + "#");

		String innerRegex = "<td.*?>(.*?)</td>";
		Pattern innerPattern = Pattern.compile(innerRegex, Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher innerMatcher = innerPattern.matcher(specContent);
		String key = "";
		String value = "";
		while (innerMatcher.find())
		{
			String element = innerMatcher.group(1);
			int junkHtmlIndexStart = element.indexOf("&");
			junkHtmlIndexStart = (junkHtmlIndexStart == -1 ? element.length() : junkHtmlIndexStart);
			element = element.substring(0, junkHtmlIndexStart);
			element = element.trim();
			if (element.startsWith("#"))
			{
				ArrayList<String> list = new ArrayList<String>();
				key = element.substring(1);
				map.put(key, list);
			}
			else
			{
				if (value.equals(""))
					value += element;
				else
				{
					value = value + ":" + element;
					ArrayList<String> list = map.get(key);
					list.add(value);
					value = "";
				}
			}
		}
		return new JSONObject(map);
	}

	private <T> T getValue(ScrapeSense ss, String content, T defValue)
	{
		String regex = ss.startStr + "(.*?)" + ss.closeStr;
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher matcher = pattern.matcher(content);
		T value = defValue;
		if (matcher.find())
		{
			value = (T) (matcher.group(1));
		}
		return value;
	}

	private <T> ArrayList<T> getAllValues(ScrapeSense ss, String content, ArrayList<T> defList)
	{
		String regex = ss.startStr + "(.*?)" + ss.closeStr;
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.UNIX_LINES);
		Matcher matcher = pattern.matcher(content);
		ArrayList<T> list = defList;
		while (matcher.find())
		{
			list.add((T) (matcher.group(1)));
		}
		return list;
	}

	public static void main(String[] args)
	{
		LetsBuyProductInfoExtractor engine = new LetsBuyProductInfoExtractor();
		engine.start();
	}

}
