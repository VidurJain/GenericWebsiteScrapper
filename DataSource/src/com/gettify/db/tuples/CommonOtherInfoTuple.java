package com.gettify.db.tuples;

public class CommonOtherInfoTuple
{

	private String url;

	private String category;

	private String subcategory;

	public CommonOtherInfoTuple(String url, String category, String subcategory)
	{
		this.url = url;
		this.category = category;
		this.subcategory = subcategory;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getSubcategory()
	{
		return subcategory;
	}

	public void setSubcategory(String subcategory)
	{
		this.subcategory = subcategory;
	}

}
