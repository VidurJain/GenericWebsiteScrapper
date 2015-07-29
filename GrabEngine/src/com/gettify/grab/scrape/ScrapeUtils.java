package com.gettify.grab.scrape;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gettify.entities.Category;
import com.gettify.entities.Listing;
import com.gettify.entities.Product;

public class ScrapeUtils
{

	/*
	 * SCARAPESENSE SHOULD BE A TREE INSTEAD PASSED TO THIS IN CURRENT SCENARIO, SCRAPES[0] WILL TELL FIRST LEVEL MATCH
	 */
	public Listing getListings(String uri, ScrapeSense[] scrapes) throws Exception
	{
		System.out.println("getting content for " + uri);
		Listing l = new Listing();
		Product p = new Product();
		Category c = new Category();

		String content = getContent(uri);
		ArrayList<String> elems = new ArrayList<String>();

		for (int i = 0; i < scrapes.length; i++)
		{
			String testReg = scrapes[i].startStr + ".*?" + scrapes[i].closeStr;
			Pattern pTag = Pattern.compile(testReg, Pattern.DOTALL | Pattern.UNIX_LINES);
			Matcher matcher = pTag.matcher(content);
			while (matcher.find())
			{
				// String match = matcher.group();
				String url = matcher.group(1);
				// String toret = match.substring(scrapes[i].startStr.length(), match.length() - scrapes[i].closeStr.length());
				elems.add(url);
			}
		}

		return l;
	}

	public String getContent(String uri)
	{
		try
		{
			URL url = new URL(uri);
			StringBuilder sb = getURLContent(url);
			return sb.toString();
			// System.out.println(sb.toString());
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
		return null;
	}

	public StringBuilder getURLContent(URL url) throws IOException
	{
		URLConnection conn = url.openConnection();
		String encoding = conn.getContentEncoding();
		if (encoding == null)
		{
			encoding = "ISO-8859-1";
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
		StringBuilder sb = new StringBuilder(16384);
		try
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
				sb.append('\n');
			}
		}
		finally
		{
			br.close();
		}
		return sb;
	}

	public String removeHtmlTags(String html)
	{
		String nohtml = "";
		try
		{
			html = html.replaceAll("<br>", " ");
			html = html.replaceAll("<br />", " ");
			html = html.replaceAll("<br/>", " ");
			nohtml = html.replaceAll("\\<.*?>", "");
			// nohtml = nohtml.replaceAll("&nbsp;", "");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return nohtml;
	}

	public ArrayList<String> findImageUrl(String feedContent)
	{
		ArrayList<String> imageUrls = new ArrayList<String>();
		String imageUrl = "";
		String imageTagRegex = "<img.*?>";
		String srcRegex = "src=\".*?\"";
		// better precompile pattern
		Pattern pTag = Pattern.compile(imageTagRegex);
		Matcher matcher = pTag.matcher(feedContent);
		while (matcher.find())
		{
			// better precompile, otherwise do like this
			Pattern pSrc = Pattern.compile(srcRegex);
			Matcher m = pSrc.matcher(matcher.group());
			while (m.find())
			{
				imageUrl = m.group();
				imageUrl = imageUrl.substring(4, imageUrl.length());
				imageUrls.add(imageUrl);
				System.out.println(imageUrl);
			}
		}
		return imageUrls;
	}

	public static void main(String[] args)
	{
		ScrapeUtils util = new ScrapeUtils();
		try
		{
			ArrayList<ScrapeSense> ss = new ArrayList<ScrapeSense>();
			// ss.add(new ScrapeSense("<h1 class=\"prod_name\">", "</h1>", "name"));
			ss.add(new ScrapeSense("<h2 class=\"green\">" + "<a href=\"" + "([^\"]+)" + "\"[^>]*>" + "([^<]+)" + "</a>", "</h2>", "name"));
			// ss.add(new ScrapeSense("<div class=\"search_products\">", "</div>", "name"));
			util.getListings("http://www.letsbuy.com/mobile-phones-mobiles-c-254_88", (ScrapeSense[]) ss.toArray(new ScrapeSense[0]));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			System.out.println("oops");
			e.printStackTrace();
		}
	}
}
