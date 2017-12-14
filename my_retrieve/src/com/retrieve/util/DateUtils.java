package com.retrieve.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {


	public static Date getYear(String year){
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		try {
			date = sdf.parse(year);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	
	
	public static Date getCurrentYMDHMS(){
		Date date = new Date();
		Date result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		try {
			String currentDate = sdf.format(date);
			result = sdf.parse(currentDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
}
