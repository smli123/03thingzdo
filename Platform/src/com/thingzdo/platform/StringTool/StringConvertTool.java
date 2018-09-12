package com.thingzdo.platform.StringTool;

public class StringConvertTool {
	public static String Byte2String(byte[] input)
	{
		String strRet = new String(input);
		strRet = strRet.substring(0, strRet.indexOf('\u0000'));
		return strRet;
	}
}
