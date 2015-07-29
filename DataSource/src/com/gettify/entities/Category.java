package com.gettify.entities;

public class Category
{
	public String catId = "";

	public String catName = "";

	public String subCatId = "";

	public String subCatName = "";

	public String microCatId = "";

	public String microCatName = "";

	public Category()
	{
	}

	public Category(String catId, String catName, String subCatId, String subCatName, String microCatId, String microCatName)
	{
		this.catId = catId;
		this.catName = catName;
		this.subCatId = subCatId;
		this.subCatName = subCatName;
		this.microCatId = microCatId;
		this.microCatName = microCatName;
	}

	@Override
	public String toString()
	{
		return "Category [catId=" + catId + ", catName=" + catName + ", subCatId=" + subCatId + ", subCatName=" + subCatName + ", microCatId=" + microCatId + ", microCatName="
				+ microCatName + "]";
	}
}
