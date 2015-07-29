package com.gettify.scrape.engines;

import java.util.ArrayList;

import com.gettify.common.utils.Merchants;
import com.gettify.grab.scrape.ScrapeSense;
import com.gettify.scrape.urlgenerator.URLScrape;

public class LetsBuyURLFetcherEngine extends URLScrape
{

	private static int MERCHANT_ID = Merchants.LETSBUY;

	public LetsBuyURLFetcherEngine()
	{
		super(getUrlList(), getScrapeSense(), MERCHANT_ID);
	}

	public void start()
	{

	}

	private static ScrapeSense[] getScrapeSense()
	{
		ArrayList<ScrapeSense> ss = new ArrayList<ScrapeSense>();
		ss.add(new ScrapeSense("<h2 class=\"green\">" + "<a href=\"" + "([^\"]+)" + "\"[^>]*>" + "([^<]+)" + "</a>", "</h2>", "name"));
		return (ScrapeSense[]) ss.toArray(new ScrapeSense[0]);
	}

	private static String getUrl()
	{
		return "http://www.letsbuy.com/mobile-phones-mobiles-c-254_88?page=%d";
	}

	private static ArrayList<String> getUrlList()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("http://www.letsbuy.com/mobile-phones-tablets-c-254_393?page=%d");
		list.add("http://www.letsbuy.com/mobile-phones-mobiles-c-254_88?page=%d");
		return list;
	}

	public static void main(String[] args)
	{
		LetsBuyURLFetcherEngine engine = new LetsBuyURLFetcherEngine();
		engine.start();
	}
}
