package com.gettify.scrape.engines;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gettify.common.utils.Merchants;
import com.gettify.common.utils.PageSourceReaderUtils;
import com.gettify.common.utils.Utils;
import com.gettify.scrape.urlgenerator.ProductUrlExtractor;

public class LetsBuyUrlExtractorEngine extends ProductUrlExtractor
{
	private static int MERCHANT_ID = Merchants.LETSBUY;

	public LetsBuyUrlExtractorEngine(ArrayList<String> uriList, int merchantId)
	{
		super(uriList, merchantId);
	}

	private static ArrayList<String> getUrlList()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("http://www.letsbuy.com/newarch/letsbuy_new/index.php/search/filterResult?c=254_393&pg=%d");
		return list;
	}

	protected ArrayList<String> getProductUrlList(String urlToFetch) throws JSONException
	{
		String content = PageSourceReaderUtils.getContent(urlToFetch);
		// String getCategoryParam = PageSourceReaderUtils.getParamVal(urlToFetch,"c");
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
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++)
		{
			JSONObject productObj = (JSONObject) array.get(i);
			String productUrl = (String) productObj.get("url");
			// String productId = (String) productObj.get("products_id");
			// String basicUrl = "http://letsbuy.com/newarch/letsbuy_new/index.php/search/filterResult";
			// basicUrl = PageSourceReaderUtils.appendParamToUrl(basicUrl, "c",getCategoryParam);
			// basicUrl = PageSourceReaderUtils.appendParamToUrl(basicUrl, "p",productId);
			list.add(productUrl);
		}
		return list;
	}

	public static void main(String[] args)
	{

		new LetsBuyUrlExtractorEngine(getUrlList(), MERCHANT_ID);
	}
}
