package com.gettify.common.utils;

import java.util.Date;

public class Utils
{
	public static long MILLISECONDSINADAY = 24 * 60 * 60 * 1000l;

	public static int MILLISECONDSINAHOUR = 60 * 60 * 1000;

	public static void log(String str, Exception e)
	{
		System.out.println(getDate() + ":" + str);
		if (e != null)
			e.printStackTrace();
	}

	public static void log(String str)
	{
		log(str, null);
	}

	public static Date getDate(long time)
	{
		return new Date(time);
	}

	public static Date getDate()
	{
		return getDate(System.currentTimeMillis());
	}

	public static String removeCommas(String str)
	{
		return str.replaceAll(",", "");
	}
}
