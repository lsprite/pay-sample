package com.nemo.wxpay_library;

import java.math.BigDecimal;

public class StringUtil {
	// 转换成价格1.00
	public static String toPrice(String str) {
		try {
			if (str == null || str.equals("")) {
				return "0";
			} else {
				BigDecimal bd = new BigDecimal(str.replace("￥", ""));
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				return bd.toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}

	// 转换成int
	public static String toInt(String str) {
		try {
			if (str == null || str.equals("")) {
				return "0";
			} else {
				BigDecimal bd = new BigDecimal(str.replace("￥", ""));
				bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
				return bd.toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}

	}

	// 钱的元转换成分
	public static String toFen(String str) {
		return toInt((Float.parseFloat(toPrice(str))) * 100 + "");
	}
}
