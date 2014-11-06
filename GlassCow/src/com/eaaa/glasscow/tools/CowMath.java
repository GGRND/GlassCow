package com.eaaa.glasscow.tools;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CowMath {

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * @param date as String value in yyyy-MM-dd
	 * @return Integer
	 * @throws ParseException
	 */
	public static int daysSince(String date) throws ParseException {
		long dateInMillis = formatter.parse(date.substring(0, 10)).getTime();
		long nowInMillis = System.currentTimeMillis();
		long timeDifference;

		timeDifference = nowInMillis - dateInMillis;

		// 60*60*24*1000 = 86400000 = 1 Day in Millis
		return (int) (timeDifference / 86400000);
	}

	public static int daysSinceAbsolute(String date) throws ParseException {
		return Math.abs(daysSince(date));
	}

	public static int calculateDryOff(String date, int currentCatle) throws ParseException {
		int daysDiff = daysSince(date);

		if (currentCatle == 0) {
			daysDiff -= 31;
		} else if (currentCatle == 1) {
			daysDiff -= 63;
		} else {
			daysDiff -= 49;
		}
		return daysDiff;
	}
}
