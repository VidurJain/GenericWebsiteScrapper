package com.gettify.db.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleTableHandler
{
	private Connection conn = null;

	String userName = "gettify";

	String password = "Gettify123";

	String url = "jdbc:mysql://localhost/GETTIFY_SERVER";

	public SimpleTableHandler()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			System.out.println("Database connection established");
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			System.out.println("test");
			e.printStackTrace();
		}
	}

	public Connection getConnection()
	{
		return conn;
	}

}
