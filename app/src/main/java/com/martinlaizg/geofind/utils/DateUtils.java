package com.martinlaizg.geofind.utils;

import java.sql.Date;
import java.util.Calendar;

public class DateUtils {

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final long TIME_TO_EXPIRE = 15 * 60 * 1000; // 15min * 60s * 1000ms

	public static boolean isDateExpire(Date fromDate) {
		return Calendar.getInstance().getTime().getTime() - fromDate.getTime() > TIME_TO_EXPIRE;
	}
}
