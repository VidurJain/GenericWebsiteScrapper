package com.gettify.entities;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

public class Product
{
	public String productId = "";

	public String productName = "";

	public String binding = "";

	public String brand = "";

	public String gtin = "";

	public String gtinType = "";

	public String imageUrl = "";

	public String description = "";

	public Long timeAdded = -1l;

	public String mcatid;

	public String specs;

	public Product()
	{
	}

	public Product(String productId, String productName, String brand, String gtin, String gtinType, String imageUrl, String description, Long timeAdded, String mcatid,
			String specs)
	{
		this.productId = productId;
		this.productName = productName;
		this.brand = brand;
		this.gtin = gtin;
		this.gtinType = gtinType;
		this.imageUrl = imageUrl;
		this.description = description;
		this.timeAdded = timeAdded;
		this.mcatid = mcatid;
		this.specs = specs;
	}

	public String debugString()
	{
		StringBuilder sb = new StringBuilder("");
		Field[] fields = Product.class.getFields();
		for (int i = 0; i < fields.length; i++)
		{
			String name = fields[i].getName();
			try
			{
				Field field = this.getClass().getDeclaredField(name);
				String val = String.valueOf(field.get(this));
				sb.append("{").append(name).append(" : ").append(val).append("}, ");
			}
			catch (SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NoSuchFieldException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public String getStringFromBlob(Blob blob) throws SQLException
	{
		if (blob != null)
			return new String(blob.getBytes(1, (int) blob.length()));
		return null;
	}

	public Blob getBlobFromString(String str) throws Exception
	{
		if (str != null)
			return new SerialBlob(str.getBytes());
		return null;
	}

	@Override
	public String toString()
	{
		return "Product [productId=" + productId + ", productName=" + productName + ", brand=" + brand + ", gtin=" + gtin + ", gtinType=" + gtinType + ", imageUrl=" + imageUrl
				+ ", description=" + description + ", timeAdded=" + timeAdded + ", mcatid=" + mcatid + ", specs=" + specs + "]";
	}
}
