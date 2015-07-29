package com.gettify.grab.engines;

import com.gettify.common.utils.Merchants;
import com.gettify.grab.linkshare.LSFileDumper;

public class LSWalmart extends LSFileDumper
{

	@Override
	protected String getFileName()
	{
		// TODO Auto-generated method stub
		return "2149_2787695_mp.txt";
	}

	public static void main(String[] args)
	{
		LSWalmart ls1 = new LSWalmart();
		ls1.init();
	}

	@Override
	protected int getAffiliateId()
	{
		return Merchants.LS_WALMART;
	}

}
