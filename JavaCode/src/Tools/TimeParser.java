package Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeParser {
	public static String longDateToStringDate(long date) throws ParseException {
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		//前面的lSysTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
		java.util.Date dt = new Date( date * 1000 );  
		String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
		return sDateTime;
	}
	public static int longDateToDayTime(long date) throws ParseException {
		SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
		//前面的lSysTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
		java.util.Date dt = new Date( date*1000 );  
		String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
		return Integer.valueOf(sDateTime);
	}
	public static long stringDateToLongDate(String date) throws ParseException {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		java.util.Date FromTime = sdf.parse(date);
		long time = FromTime.getTime()/1000;
		return time;
	}
}
