package com.gettify.entities.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gettify.db.utils.PSLocalMap;
import com.gettify.db.utils.SimpleTableHandler;
import com.gettify.entities.Listing;

public class ListingsTableHandler
{
	private static ListingsTableHandler instance;

	private SimpleTableHandler tableHandler;

	protected Connection connection;

	private String TABLE_NAME = "LISTINGS";

	private ListingsTableHandler()
	{
		tableHandler = new SimpleTableHandler();
		connection = tableHandler.getConnection();
	}

	public static ListingsTableHandler getInstance()
	{
		if (instance == null)
		{
			instance = new ListingsTableHandler();
		}
		return instance;
	}

	/**
	 * utility to save listing to table checks first if the listing exists,
	 */
	private ThreadLocal<PreparedStatement> saveListingPsMap = new ThreadLocal<PreparedStatement>();

	public void saveListing(Listing l) throws Exception
	{
		String sql = "INSERT INTO "
				+ TABLE_NAME
				+ " (listing_id, product_id, internal_id, title, price, shipping_price, available, currency_code, url_fetched, product_condition, image_url, affiliate_id, time_expiring, time_added) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		int i = 1;
		try
		{
			PreparedStatement stmtToInsertListing = PSLocalMap.getPS(connection, saveListingPsMap, sql);
			stmtToInsertListing.setString(i++, l.listingId);
			stmtToInsertListing.setString(i++, l.productId);
			stmtToInsertListing.setString(i++, l.internalId);
			stmtToInsertListing.setString(i++, l.title);
			stmtToInsertListing.setFloat(i++, l.listedPrice);
			stmtToInsertListing.setFloat(i++, l.shippingPrice);
			stmtToInsertListing.setInt(i++, l.available);
			stmtToInsertListing.setString(i++, l.currencyCode);
			stmtToInsertListing.setString(i++, l.urlFetched);
			stmtToInsertListing.setString(i++, l.productCondition);
			stmtToInsertListing.setString(i++, l.imageUrl);
			stmtToInsertListing.setInt(i++, l.affiliateId);
			stmtToInsertListing.setLong(i++, l.timeExpiring);
			stmtToInsertListing.setLong(i++, l.timeAdded);

			if (null == stmtToInsertListing)
				throw new Exception("no PS found");

			stmtToInsertListing.executeUpdate();

		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * utility to save listing to table checks first if the listing exists,
	 */
	private ThreadLocal<PreparedStatement> updateListingPsMap = new ThreadLocal<PreparedStatement>();

	public void updateListing(Listing l) throws Exception
	{
		String sql = "UPDATE " + TABLE_NAME + " SET price=?, shipping_price=?, available=?, currency_code=?, time_expiring=? WHERE listing_id=?";
		int i = 1;
		try
		{
			PreparedStatement stmtToUpdateListing = PSLocalMap.getPS(connection, updateListingPsMap, sql);
			stmtToUpdateListing.setFloat(i++, l.listedPrice);
			stmtToUpdateListing.setFloat(i++, l.shippingPrice);
			stmtToUpdateListing.setInt(i++, l.available);
			stmtToUpdateListing.setString(i++, l.currencyCode);
			stmtToUpdateListing.setLong(i++, l.timeExpiring);
			stmtToUpdateListing.setString(i++, l.listingId);
			if (null == stmtToUpdateListing)
				throw new Exception("no PS found");

			stmtToUpdateListing.executeUpdate();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private ThreadLocal<PreparedStatement> getListingByIdPsMap = new ThreadLocal<PreparedStatement>();

	public Listing getListingById(int listingId)
	{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE listing_id=?";
		int i = 1;
		Listing l = null;
		try
		{
			PreparedStatement stmtToGetListing = PSLocalMap.getPS(connection, getListingByIdPsMap, sql);
			stmtToGetListing.setInt(i++, listingId);
			ResultSet rs = stmtToGetListing.executeQuery();
			while (rs.next())
			{
				l = getListingFromRS(rs);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l;
	}

	/**
	 * check if listing with corresponding internal id exists. Replace listingId with one of that exists
	 */
	private ThreadLocal<PreparedStatement> checkListingByInternalIdPsMap = new ThreadLocal<PreparedStatement>();

	public boolean ifExistsListingByInternalId(Listing listing)
	{
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE internal_id=? AND affiliate_id=?";
		int i = 1;
		Listing l = null;
		try
		{
			PreparedStatement stmtToGetListing = PSLocalMap.getPS(connection, checkListingByInternalIdPsMap, sql);
			stmtToGetListing.setString(i++, listing.internalId);
			stmtToGetListing.setInt(i++, listing.affiliateId);
			ResultSet rs = stmtToGetListing.executeQuery();
			while (rs.next())
			{
				l = getListingFromRS(rs);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (l != null && l.internalId.equals(listing.internalId))
		{
			listing.listingId = l.listingId;
			return true;
		}
		return false;
	}

	/**
	 * function to get Listing from ListingsTable ResultSet
	 */
	private Listing getListingFromRS(ResultSet rs) throws SQLException
	{
		Listing l = new Listing();
		l.listingId = rs.getString("listing_id");
		l.productId = rs.getString("product_id");
		l.internalId = rs.getString("internal_id");
		l.title = rs.getString("title");
		l.price = rs.getFloat("price");
		l.shippingPrice = rs.getFloat("shipping_price");
		l.available = rs.getInt("available");
		l.currencyCode = rs.getString("currency_code");
		l.urlFetched = rs.getString("url_fetched");
		l.productCondition = rs.getString("product_condition");
		l.imageUrl = rs.getString("image_url");
		l.affiliateId = rs.getInt("affiliate_id");
		l.timeExpiring = rs.getLong("time_expiring");
		l.timeAdded = rs.getLong("time_added");
		return l;
	}

}
