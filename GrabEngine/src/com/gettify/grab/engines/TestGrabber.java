package com.gettify.grab.engines;

import java.util.ArrayList;

import com.gettify.entities.Listing;
import com.gettify.grab.GrabberCommon;

public class TestGrabber extends GrabberCommon
{

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected ArrayList<Listing> readObjects()
	{
		System.out.println("reading objects");
		ArrayList<Listing> arr = new ArrayList<Listing>();
		arr.add(new Listing());
		arr.add(new Listing());
		return arr;
	}

	public static void main(String[] args)
	{
		TestGrabber test = new TestGrabber();
		test.run();
	}

}
