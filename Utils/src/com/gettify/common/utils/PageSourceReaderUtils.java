package com.gettify.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageSourceReaderUtils
{

	public static String getContent(String uri)
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

	private static StringBuilder getURLContent(URL url) throws IOException
	{
		Utils.log("Fetching url info for " + url);
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

	public static String getParamVal(String url, String param)
	{
		String paramPart = "";
		int i = url.indexOf("?");
		if (i > -1)
			paramPart = url.substring(url.indexOf("?") + 1);
		StringTokenizer st = new StringTokenizer(paramPart, "&");
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (param.equals(token.split("=")[0]))
				return token.split("=")[1];
		}
		return null;
	}

	public static String appendParamToUrl(String url, String param, String value)
	{
		if (url.contains("?"))
			url = url + "&" + param + "=" + value;
		else
			url = url + "?" + param + "=" + value;

		return url;
	}

	public static String removeHtmlTags(String html)
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

	public static ArrayList<String> findImageUrl(String feedContent)
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
			}
		}
		return imageUrls;
	}

	/*
	 * It replaces all &amp;, &nbsp; type strings to blank
	 */
	public static String replaceAmpChars(String str)
	{
		return str.replaceAll("\\&.*?\\;", "");
	}

	public static String replaceHtmlTags(String str)
	{
		return str.replaceAll("\\<.*?\\>", "");
	}

	public static String replaceHtmlComments(String str)
	{
		return str.replaceAll("<!--.*?-->;", "");
	}

	public static void main(String[] args) throws Exception
	{
		URL url = new URL("http://www.letsbuy.com/mobile-phones-tablets-c-254_393");
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String inputLine;

		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);

		in.close();
	}
}
