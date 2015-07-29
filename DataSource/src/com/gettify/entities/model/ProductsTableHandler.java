package com.gettify.entities.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gettify.db.utils.PSLocalMap;
import com.gettify.db.utils.SimpleTableHandler;
import com.gettify.entities.Category;
import com.gettify.entities.Product;

public class ProductsTableHandler
{
	private static ProductsTableHandler instance;

	private SimpleTableHandler tableHandler;

	protected Connection connection;

	private String TABLE_NAME = "PRODUCTS";

	private String CAT_TABLE_NAME = "CATEGORIES";

	private String SUBCAT_TABLE_NAME = "SUBCATEGORIES";

	private String MICROCAT_TABLE_NAME = "MICROCATEGORIES";

	private ProductsTableHandler()
	{
		tableHandler = new SimpleTableHandler();
		connection = tableHandler.getConnection();
	}

	public static ProductsTableHandler getInstance()
	{
		if (instance == null)
		{
			instance = new ProductsTableHandler();
		}
		return instance;
	}

	/**
	 * utility to save product to table check first if the product exists, and then call this function
	 */
	private ThreadLocal<PreparedStatement> saveProductPsMap = new ThreadLocal<PreparedStatement>();

	public void saveProduct(Product p) throws Exception
	{
		String sql = "INSERT INTO " + TABLE_NAME
				+ " (product_id, product_name, brand, gtin, gtin_type, image_url, time_added, mcat_id, specs, description) VALUES (?,?,?,?,?,?,?,?,?,?)";
		int i = 1;
		try
		{
			PreparedStatement stmtToInsertProduct = PSLocalMap.getPS(connection, saveProductPsMap, sql);
			stmtToInsertProduct.setString(i++, p.productId);
			stmtToInsertProduct.setString(i++, p.productName);
			stmtToInsertProduct.setString(i++, p.brand);
			stmtToInsertProduct.setString(i++, p.gtin);
			stmtToInsertProduct.setString(i++, p.gtinType);
			stmtToInsertProduct.setString(i++, p.imageUrl);
			stmtToInsertProduct.setLong(i++, p.timeAdded);
			stmtToInsertProduct.setString(i++, p.mcatid);
			stmtToInsertProduct.setBlob(i++, p.getBlobFromString(p.specs));
			stmtToInsertProduct.setBlob(i++, p.getBlobFromString(p.description));

			if (null == stmtToInsertProduct)
				throw new Exception("no PS found");

			stmtToInsertProduct.executeUpdate();

		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ThreadLocal<PreparedStatement> getProductByIdPsMap = new ThreadLocal<PreparedStatement>();

	public Product getProductById(String productId)
	{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE product_id=?";
		int i = 1;
		Product p = null;
		try
		{
			PreparedStatement stmtToGetListing = PSLocalMap.getPS(connection, getProductByIdPsMap, sql);
			stmtToGetListing.setString(i++, productId);
			ResultSet rs = stmtToGetListing.executeQuery();
			while (rs.next())
			{
				p = getProductFromRS(rs);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

	/*
	 * checkProductId by gtin. If already exists, replace productId with one of that exists
	 */
	private ThreadLocal<PreparedStatement> checkProductByGtin = new ThreadLocal<PreparedStatement>();

	public boolean ifExistsProductByGtin(Product product)
	{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE gtin=?";
		int i = 1;
		Product p = null;
		try
		{
			PreparedStatement stmtToGetListing = PSLocalMap.getPS(connection, checkProductByGtin, sql);
			stmtToGetListing.setString(i++, product.gtin);
			ResultSet rs = stmtToGetListing.executeQuery();
			while (rs.next())
			{
				p = getProductFromRS(rs);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (p != null && p.gtin.equals(product.gtin))
		{
			product.productId = p.productId;
			return true;
		}
		return false;
	}

	/**
	 * function to get Listing from ListingsTable ResultSet
	 */
	private Product getProductFromRS(ResultSet rs) throws SQLException
	{
		Product p = new Product();
		p.productId = rs.getString("product_id");
		p.productName = rs.getString("product_name");
		p.brand = rs.getString("brand");
		p.gtin = rs.getString("gtin");
		p.gtinType = rs.getString("gtin_type");
		p.imageUrl = rs.getString("image_url");
		p.timeAdded = rs.getLong("time_added");
		p.mcatid = rs.getString("mcat_id");
		p.specs = p.getStringFromBlob(rs.getBlob("specs"));
		p.description = p.getStringFromBlob(rs.getBlob("description"));
		return p;
	}

	// HANDLE CATEGORIES INFORMATIONS
	/**
	 * utility to save categories to table check first if the product exists, and then call this function
	 */
	private ThreadLocal<PreparedStatement> saveCategoryPsMap = new ThreadLocal<PreparedStatement>();

	public void saveCategory(Category c) throws Exception
	{
		String sql = "INSERT INTO " + CAT_TABLE_NAME + " (cat_id, name) VALUES (?,?)";
		int i = 1;
		try
		{
			PreparedStatement stmtToInsertCat = PSLocalMap.getPS(connection, saveCategoryPsMap, sql);
			stmtToInsertCat.setString(i++, c.catId);
			stmtToInsertCat.setString(i++, c.catName);

			if (null == stmtToInsertCat)
				throw new Exception("no PS found");

			stmtToInsertCat.executeUpdate();

		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * checkProductId by gtin. If already exists, replace productId with one of that exists
	 */
	private ThreadLocal<PreparedStatement> checkCatByName = new ThreadLocal<PreparedStatement>();

	public Boolean ifExistsCatByName(Category c)
	{
		String sql = "SELECT * FROM " + CAT_TABLE_NAME + " WHERE name=?";
		int i = 1;
		Category cat = null;
		try
		{
			PreparedStatement stmtToGetCat = PSLocalMap.getPS(connection, checkCatByName, sql);
			stmtToGetCat.setString(i++, c.catName);
			ResultSet rs = stmtToGetCat.executeQuery();
			while (rs.next())
			{
				cat = getCategoryFromRS(rs);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cat != null && c.catName.equals(cat.catName))
		{
			c.catId = cat.catId;
			return true;
		}
		return false;
	}

	/**
	 * function to get Category from CategoryTable ResultSet
	 */
	private Category getCategoryFromRS(ResultSet rs) throws SQLException
	{
		Category c = new Category();
		c.catId = rs.getString("cat_id");
		c.catName = rs.getString("name");
		return c;
	}

	/**
	 * utility to save subcategories to table check first if the product exists, and then call this function
	 */
	private ThreadLocal<PreparedStatement> saveSubCategoryPsMap = new ThreadLocal<PreparedStatement>();

	public void saveSubCategory(Category c) throws Exception
	{
		String sql = "INSERT INTO " + SUBCAT_TABLE_NAME + " (cat_id, subcat_id, name) VALUES (?,?,?)";
		int i = 1;
		try
		{
			PreparedStatement stmtToInsertCat = PSLocalMap.getPS(connection, saveSubCategoryPsMap, sql);
			stmtToInsertCat.setString(i++, c.catId);
			stmtToInsertCat.setString(i++, c.subCatId);
			stmtToInsertCat.setString(i++, c.subCatName);

			if (null == stmtToInsertCat)
				throw new Exception("no PS found");

			stmtToInsertCat.executeUpdate();

		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * checkProductId by gtin. If already exists, replace productId with one of that exists
	 */
	private ThreadLocal<PreparedStatement> checkSubCatByName = new ThreadLocal<PreparedStatement>();

	public Boolean ifExistsSubCatByName(Category c)
	{
		String sql = "SELECT * FROM " + SUBCAT_TABLE_NAME + " WHERE cat_id=? AND name=? ";
		int i = 1;
		Category cat = null;
		try
		{
			PreparedStatement stmtToGetCat = PSLocalMap.getPS(connection, checkSubCatByName, sql);
			stmtToGetCat.setString(i++, c.catId);
			stmtToGetCat.setString(i++, c.subCatName);
			ResultSet rs = stmtToGetCat.executeQuery();
			while (rs.next())
			{
				cat = getSubCategoryFromRS(rs);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cat != null && c.subCatName.equals(cat.subCatName))
		{
			c.subCatId = cat.subCatId;
			return true;
		}
		return false;
	}

	/**
	 * function to get Category from CategoryTable ResultSet
	 */
	private Category getSubCategoryFromRS(ResultSet rs) throws SQLException
	{
		Category c = new Category();
		c.subCatId = rs.getString("subcat_id");
		c.subCatName = rs.getString("name");
		return c;
	}

	/**
	 * utility to save microcategories to table check first if the product exists, and then call this function
	 */
	private ThreadLocal<PreparedStatement> saveMicroCategoryPsMap = new ThreadLocal<PreparedStatement>();

	public void saveMicroCategory(Category c) throws Exception
	{
		String sql = "INSERT INTO " + MICROCAT_TABLE_NAME + " (subcat_id, mcat_id, name) VALUES (?,?,?)";
		int i = 1;
		try
		{
			PreparedStatement stmtToInsertCat = PSLocalMap.getPS(connection, saveMicroCategoryPsMap, sql);
			stmtToInsertCat.setString(i++, c.subCatId);
			stmtToInsertCat.setString(i++, c.microCatId);
			stmtToInsertCat.setString(i++, c.microCatName);

			if (null == stmtToInsertCat)
				throw new Exception("no PS found");

			stmtToInsertCat.executeUpdate();

		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * checkProductId by gtin. If already exists, replace productId with one of that exists
	 */
	private ThreadLocal<PreparedStatement> checkMicroCatByName = new ThreadLocal<PreparedStatement>();

	public Boolean ifExistsMicroCatByName(Category c)
	{
		String sql = "SELECT * FROM " + SUBCAT_TABLE_NAME + " WHERE subcat_id=? AND name=? ";
		int i = 1;
		Category cat = null;
		try
		{
			PreparedStatement stmtToGetCat = PSLocalMap.getPS(connection, checkMicroCatByName, sql);
			stmtToGetCat.setString(i++, c.subCatId);
			stmtToGetCat.setString(i++, c.microCatName);
			ResultSet rs = stmtToGetCat.executeQuery();
			while (rs.next())
			{
				cat = getMicroCategoryFromRS(rs);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cat != null && c.microCatName.equals(cat.microCatName))
		{
			c.microCatId = cat.microCatId;
			return true;
		}
		return false;
	}

	/**
	 * function to get Category from CategoryTable ResultSet
	 */
	private Category getMicroCategoryFromRS(ResultSet rs) throws SQLException
	{
		Category c = new Category();
		c.microCatId = rs.getString("mcat_id");
		c.microCatName = rs.getString("name");
		return c;
	}

	public static void main(String[] args) throws Exception
	{
		Product p = new Product();
		p.productId = "Temp";
		p.productName = "TempName";
		p.binding = "none";
		p.brand = "Local";
		p.gtin = "SS";
		p.gtinType = "Local";
		p.imageUrl = "none";
		p.timeAdded = System.currentTimeMillis();
		p.mcatid = "micro";
		p.specs = "specifcations matter";
		p.description = "none";
		ProductsTableHandler.getInstance().saveProduct(p);
		p = ProductsTableHandler.getInstance().getProductById("Temp");
		System.out.println(p.specs);
	}
}
