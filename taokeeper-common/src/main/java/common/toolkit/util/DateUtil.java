package common.toolkit.util;

import static common.toolkit.constant.EmptyObjectConstant.EMPTY_STRING;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import common.toolkit.constant.BaseConstant;
import common.toolkit.constant.EmptyObjectConstant;
import common.toolkit.entity.DateFormat;
import common.toolkit.exception.IllegalParamException;
import common.toolkit.util.collection.CollectionUtil;

/**
 * Description: String util class.
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class DateUtil {

	
	/**
	 * Convert java.util.Date to String<br>
	 * 
	 * @return like format yyyy-MM-dd HH:mm:ss.
	 */
	public static String convertDate2String( Date date, DateFormat dateFormat ) {
		if ( null == date )
			return EMPTY_STRING;
		long seconds = date.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormat.getFormat() );
		Date dt = new Date( seconds );
		String dateString = sdf.format( dt );
		return StringUtil.trimToEmpty( dateString );
	}
	
	/**
	 * Convert java.util.Date to String<br>
	 * 
	 * @return like format yyyy-MM-dd HH:mm:ss.
	 */
	public static String convertDate2String( Date date, String dateFormat ) {
		if ( null == date )
			return EMPTY_STRING;
		long seconds = date.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
		Date dt = new Date( seconds );
		String dateString = sdf.format( dt );
		return StringUtil.trimToEmpty( dateString );
	}
	
	/**
	 * 将13位long型的时候转换成指定格式字符串时间<br>
	 * 
	 * @return like format yyyy-MM-dd HH:mm:ss.
	 */
	public static String convertLong2String( Long longFormatDateTime, DateFormat dateFormat ) 
	throws IllegalParamException{
		
		String dateFormatString = dateFormat.getFormat();
		if( dateFormat == DateFormat.SolrDateTime ){
			dateFormatString = "yyyy-MM-dd+HH:mm:ss#";
		}
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormatString );
		Date dt = new Date( longFormatDateTime );
		String dateString = sdf.format( dt );
		if( dateFormat == DateFormat.SolrDateTime ){
			dateString = dateString.replace( "+", "T" ).replace( "#", "Z" );
		}
		return StringUtil.trimToEmpty( dateString );
	} 
	
	 /** 将13位long型的时候转换成指定格式字符串时间<br>
	 * 
	 * @return like format yyyy-MM-dd HH:mm:ss.
	 */
	public static String convertLong2String( Long longFormatDateTime, String dateFormat ) 
	throws IllegalParamException{
		
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormat );
		Date dt = new Date( longFormatDateTime );
		String dateString = sdf.format( dt );
		return StringUtil.trimToEmpty( dateString );
	} 
	
	
	/**
	 * Convert java.util.Date to String<br>
	 * 
	 * @return like format yyyy-MM-dd HH:mm:ss.
	 */
	public static String convertDate2String( Date date ) {
		return DateUtil.convertDate2String( date, DateFormat.DateTime );
	}
	
	
	/**
	 * @return a formated date:"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"
	 */
	public static String getNowTime( DateFormat dateFormat ) {
		return DateUtil.getNowTime( dateFormat.getFormat() );
	}
	
	
	/**
	 * @return a formated date:"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"
	 */
	public static String getNowTime( String dateFormat ) {
		SimpleDateFormat sdf = new SimpleDateFormat( StringUtil.defaultIfBlank( dateFormat, DateFormat.Date.getFormat() ) );
		Date dt = new Date();
		String dateString = sdf.format( dt );
		return StringUtil.trimToEmpty( dateString );
	}
	
	/**
	 * @return a formated date:"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"
	 */
	public static String getNowTime( String dateFormat, long dateLong ) {
		SimpleDateFormat sdf = new SimpleDateFormat( StringUtil.defaultIfBlank( dateFormat, DateFormat.Date.getFormat() ) );
		Date dt = new Date( dateLong );
		String dateString = sdf.format( dt );
		return StringUtil.trimToEmpty( dateString );
	}
	
	/**
	 * @return a formated date:"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"
	 *///2012-09-27T11:36:34Z
	public static String getNowTime( DateFormat dateFormat, long dateLong ) {
		
		String dateFormatString = dateFormat.getFormat();
		if( dateFormat == DateFormat.SolrDateTime ){
			dateFormatString = "yyyy-MM-dd+HH:mm:ss#";
		}
		SimpleDateFormat sdf = new SimpleDateFormat( dateFormatString );
		Date dt = new Date( dateLong );
		String dateString = sdf.format( dt );
		if( dateFormat == DateFormat.SolrDateTime ){
			dateString = dateString.replace( "+", "T" ).replace( "#", "Z" );
		}
		return StringUtil.trimToEmpty( dateString );
	}
	
	
	
	/**
	 * @return a formated yesterday date:"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"
	 */
	public static String getYesterdayTime( DateFormat dateFormat ) {

		String format = EmptyObjectConstant.EMPTY_STRING;

		switch (dateFormat) {
		case DateTime: {
			format = DateFormat.DateTime.getFormat();
			break;
		}
		case Date: {
			format = DateFormat.Date.getFormat();
			break;
		}
		default:
			format = DateFormat.Date.getFormat();
		}
		SimpleDateFormat sdf = new SimpleDateFormat( StringUtil.defaultIfBlank( format, DateFormat.Date.getFormat() ) );
		Date dt = new Date( System.currentTimeMillis() - BaseConstant.MILLISECONDS_OF_ONE_DAY );
		String dateString = sdf.format( dt );
		return StringUtil.trimToEmpty( dateString );
	}
	
	
	
	
	
	
	

	public static long getTimeMillisToAfterDaysHour( int days, int hourOfTomorrow ) throws Exception {

		if ( 0 == hourOfTomorrow )
			hourOfTomorrow = 2;

		Calendar calendar = Calendar.getInstance();

		int yearOfToday = calendar.get( Calendar.YEAR );
		int monthOfToday = calendar.get( Calendar.MONTH ) + 1;
		int dayOfToday = calendar.get( Calendar.DAY_OF_MONTH );

		calendar.set( Calendar.DAY_OF_MONTH, dayOfToday + days );
		if ( 31 == dayOfToday && days >=1 ) {
			calendar.set( Calendar.MONTH, monthOfToday + 1 );
		}
		if ( 12 == monthOfToday && 31 == dayOfToday && days>=1 ) {
			calendar.set( Calendar.YEAR, yearOfToday + 1 );
		}

		int dayOfTomorrow  = calendar.get( Calendar.DAY_OF_MONTH );
		int monthOfTomorrow  = calendar.get( Calendar.MONTH );
		int yearOfTomorrow = calendar.get( Calendar.YEAR );

		Calendar calendarOfTomorrow = new GregorianCalendar( yearOfTomorrow, monthOfTomorrow, dayOfTomorrow, hourOfTomorrow, 0, 0 );
		long startTimeMillis = System.currentTimeMillis();
		
		
		long timeMillisToAfterDaysHour = calendarOfTomorrow.getTime().getTime() - startTimeMillis; 
		
		if( 0 > timeMillisToAfterDaysHour )
			throw new Exception( "时间差为负数，可能设置有误" );
		
		return timeMillisToAfterDaysHour;

	}
	
	
	public static long getTimeMillisToTodayHour( int hourOfTomorrow ) throws Exception {
		return DateUtil.getTimeMillisToAfterDaysHour( 0, hourOfTomorrow );
	}
	
	
	
	
	/**
	 * Get days before:<br>
	 * today: 2012-03-29, days: 2, then return [2012-03-28,2012-03-27]
	 * */
	public static List<String> getDaysBefore( int days ){
		
		if( 0 == days )
			return CollectionUtil.emptyList();
		
		List<String> daysBeforeList = new ArrayList< String >();
		
		for( int i = 1; i <= days; i++ ){
			Date date = new Date( System.currentTimeMillis() - i * BaseConstant.MILLISECONDS_OF_ONE_DAY  );
			daysBeforeList.add( DateUtil.convertDate2String( date, DateFormat.Date ) );
		}
		return daysBeforeList;
		
	}
	
	
	
	/**
	 * Get yesterday<br>
	 * @param 2012-05-15
	 * @return 2012-05-14
	 * */
	public static String getYesterday( String date ){
		date = StringUtil.defaultIfBlank( date, DateUtil.getNowTime( DateFormat.Date ) );
		SimpleDateFormat  df = new java.text.SimpleDateFormat( "yyyy-MM-dd");
		Date dateNow;
		try {
			dateNow = df.parse( date );
		} catch ( Throwable e ) {
			return DateUtil.getYesterdayTime( DateFormat.Date );
		}
		return DateUtil.getNowTime( "yyyy-MM-dd", dateNow.getTime() - 24*60*60*1000 );
	}
	
	
	public static boolean isInAssignHour( int start, int end ) throws IllegalParamException{
		
		Calendar cal = Calendar.getInstance();
		  int hour = cal.get(Calendar.HOUR_OF_DAY);
		  
		  if( start > end ){
			  throw new IllegalParamException();
		  }
		  return (hour >= start && hour <= end );
	}
	
	
	
	
	

}
