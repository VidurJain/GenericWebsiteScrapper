package com.gettify.grab.engines;

import com.gettify.common.utils.Merchants;
import com.gettify.grab.linkshare.LSFileDumper;

public class LSWorldOfWatches extends LSFileDumper
{

	@Override
	protected String getFileName()
	{
		// TODO Auto-generated method stub
		return "24522_2787695_mp.txt";
	}

	public static void main(String[] args)
	{
		LSWorldOfWatches lswow = new LSWorldOfWatches();
		lswow.init();
	}

	@Override
	protected int getAffiliateId()
	{
		return Merchants.LS_WORLDOFWATCHES;
	}
}
