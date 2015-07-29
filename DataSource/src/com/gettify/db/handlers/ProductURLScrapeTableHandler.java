package com.gettify.db.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gettify.db.tuples.ProductURLScrapeInfoTuple;
import com.gettify.db.utils.PSLocalMap;
import com.gettify.db.utils.SimpleTableHandler;

public class ProductURLScrapeTableHandler
{

	private static ProductURLScrapeTableHandler instance;

	private SimpleTableHandler tableHandler;

	protected Connection connection;

	private String TABLE_NAME = "ProductUrlScrape";

	private ProductURLScrapeTableHandler()
	{
		tableHandler = new SimpleTableHandler();
		connection = tableHandler.getConnection();
	}

	public static ProductURLScrapeTableHandler getInstance()
	{
		if (instance == null)
		{
			instance = new ProductURLScrapeTableHandler();
		}
		return instance;
	}

	/**
	 * utility to store a uri in the table
	 */
	private ThreadLocal<PreparedStatement> insertProductUri = new ThreadLocal<PreparedStatement>();

	public void insertProductUri(ProductURLScrapeInfoTuple tuple) throws Exception
	{
		String sql = "INSERT INTO " + TABLE_NAME + " (product_uri, time_updated, merchant_id) VALUES (?,?,?)";
		int i = 1;
		try
		{
			PreparedStatement stmtToInsertProductUri = PSLocalMap.getPS(connection, insertProductUri, sql);
			stmtToInsertProductUri.setString(i++, tuple.getProductUrl());
			stmtToInsertProductUri.setLong(i++, tuple.getCrawledTime());
			stmtToInsertProductUri.setInt(i++, tuple.getMerchantId());

			if (null == stmtToInsertProductUri)
				throw new Exception("no PS found");

			stmtToInsertProductUri.executeUpdate();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * utility to update table if uri is present and time_updated is less than 24 hrs
	 */
	private ThreadLocal<PreparedStatement> updateProductUri = new ThreadLocal<PreparedStatement>();

	public void updateProductUri(ProductURLScrapeInfoTuple tuple) throws Exception
	{
		String sql = "UPDATE " + TABLE_NAME + " SET time_updated=? where product_uri=?";
		int i = 1;
		try
		{
			PreparedStatement stmtToUpdateProductUri = PSLocalMap.getPS(connection, updateProductUri, sql);
			stmtToUpdateProductUri.setLong(i++, tuple.getCrawledTime());
			stmtToUpdateProductUri.setString(i++, tuple.getProductUrl());

			if (null == stmtToUpdateProductUri)
				throw new Exception("no PS found");

			stmtToUpdateProductUri.executeUpdate();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * check if the url already exists in table
	 */
	private ThreadLocal<PreparedStatement> checkProductUrlPsMap = new ThreadLocal<PreparedStatement>();

	public ProductURLScrapeInfoTuple productUrlExists(String uri)
	{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE product_uri=? ";
		int i = 1;
		ProductURLScrapeInfoTuple tuple = null;
		try
		{
			PreparedStatement stmtToGetProductUri = PSLocalMap.getPS(connection, checkProductUrlPsMap, sql);
			stmtToGetProductUri.setString(i++, uri);
			ResultSet rs = stmtToGetProductUri.executeQuery();
			while (rs.next())
			{
				tuple = getProductUriFromRS(rs);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return tuple;
	}

	/**
	 * get product URL's between time range
	 */
	private ThreadLocal<PreparedStatement> getProductUrlsMap = new ThreadLocal<PreparedStatement>();

	public ArrayList<ProductURLScrapeInfoTuple> getProductUrls(long fromTS, long toTS)
	{
		ArrayList<ProductURLScrapeInfoTuple> list = new ArrayList<ProductURLScrapeInfoTuple>();
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE time_updated >=? and time_updated <= ?";
		int i = 1;
		ProductURLScrapeInfoTuple tuple = null;
		try
		{
			PreparedStatement stmtToGetProductUri = PSLocalMap.getPS(connection, getProductUrlsMap, sql);
			stmtToGetProductUri.setLong(i++, fromTS);
			stmtToGetProductUri.setLong(i++, toTS);
			ResultSet rs = stmtToGetProductUri.executeQuery();
			while (rs.next())
			{
				tuple = getProductUriFromRS(rs);
				list.add(tuple);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * get product URL's for a merchant
	 */
	private ThreadLocal<PreparedStatement> getProductUrlsByMerchantMap = new ThreadLocal<PreparedStatement>();

	public ArrayList<ProductURLScrapeInfoTuple> getProductUrlsByMerchant(int merchantId, int start, int limit)
	{
		ArrayList<ProductURLScrapeInfoTuple> list = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE merchant_id =? LIMIT ?,?";
		int i = 1;
		ProductURLScrapeInfoTuple tuple = null;
		try
		{
			PreparedStatement stmtToGetProductUri = PSLocalMap.getPS(connection, getProductUrlsByMerchantMap, sql);
			stmtToGetProductUri.setInt(i++, merchantId);
			stmtToGetProductUri.setInt(i++, start);
			stmtToGetProductUri.setInt(i++, limit);
			ResultSet rs = stmtToGetProductUri.executeQuery();
			list = new ArrayList<ProductURLScrapeInfoTuple>();
			while (rs.next())
			{
				tuple = getProductUriFromRS(rs);
				list.add(tuple);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * function to get ProductURLScrapeInfoTuple from ProductURLScrapeTableHandler ResultSet
	 */
	private ProductURLScrapeInfoTuple getProductUriFromRS(ResultSet rs) throws SQLException
	{
		ProductURLScrapeInfoTuple tuple = new ProductURLScrapeInfoTuple();
		tuple.setProductUrl(rs.getString("product_uri"));
		tuple.setCrawledTime(rs.getLong("time_updated"));
		tuple.setMerchantId(rs.getInt("merchant_id"));
		return tuple;
	}

}
