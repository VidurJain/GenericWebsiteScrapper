package com.gettify.entities.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.gettify.db.utils.PSLocalMap;
import com.gettify.db.utils.SimpleTableHandler;
import com.gettify.entities.Listing;

public class PricetrendsTableHandler
{
	private static PricetrendsTableHandler instance;

	private SimpleTableHandler tableHandler;

	protected Connection connection;

	private String TABLE_NAME = "PRICETRENDS";

	private PricetrendsTableHandler()
	{
		tableHandler = new SimpleTableHandler();
		connection = tableHandler.getConnection();
	}

	public static PricetrendsTableHandler getInstance()
	{
		if (instance == null)
		{
			instance = new PricetrendsTableHandler();
		}
		return instance;
	}

	/**
	 * utility to save product to table check first if the product exists, and then call this function
	 */
	private ThreadLocal<PreparedStatement> savePricetrendsPsMap = new ThreadLocal<PreparedStatement>();

	public void savePricetrend(Listing l) throws Exception
	{
		String sql = "INSERT INTO " + TABLE_NAME + " (listing_id, price, shipping_price, currency_code, time_added) VALUES (?,?,?,?,?)";
		int i = 1;
		try
		{
			PreparedStatement stmtToInsertPricetrend = PSLocalMap.getPS(connection, savePricetrendsPsMap, sql);
			stmtToInsertPricetrend.setString(i++, l.listingId);
			stmtToInsertPricetrend.setFloat(i++, l.price);
			stmtToInsertPricetrend.setFloat(i++, l.shippingPrice);
			stmtToInsertPricetrend.setString(i++, l.currencyCode);
			stmtToInsertPricetrend.setLong(i++, l.timeAdded);

			if (null == stmtToInsertPricetrend)
				throw new Exception("no PS found");

			stmtToInsertPricetrend.executeUpdate();

		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
