package common.toolkit.util.number;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.toolkit.constant.SymbolConstant;
import common.toolkit.exception.NotExpectedFormatedException;
import common.toolkit.util.StringUtil;

/**
 * IntegerUtil.java 工具类
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 13, 2012
 */
public class NumberUtil {

	
	
	/**
	 * Check if the number is Negative:-0.1,-1,-10,...
	 * @param number
	 * @return
	 */
	public static boolean isNegative( Number number ) {
		if( number.doubleValue() < 0 ){
			return true;
		}
		return false;
	}
	
	/**
	 * Check if the number is Negative:0.1,1,10,...
	 * @param number
	 * @return
	 */
	public static boolean isPositive( Number number ) {
		if( number.doubleValue() > 0 ){
			return true;
		}
		return false;
	}
	
	
	/**
	 * Check if the number is positive
	 * @param number
	 * @return
	 */
	public static boolean isNegative( Number... numberArray ) {
		
		if( null == numberArray || 0 == numberArray.length )
			return false;
		
		for( Number number : numberArray ){
			if( number.doubleValue() < 0 ){
				return true;
			}
		}
		return false;
	}
	
	
	
	/**
	 * Check if in interval
	 * @param interval such as: "[20,80]","(34,41521]"
	 * @return
	 */
	public static boolean isInInterval( String interval, String numberStr ) {

		double number = 0;
		try {
			number = Double.parseDouble( numberStr );
		} catch ( Exception e ) {
			return false;
		}
		
		interval = StringUtil.trimToEmpty( interval );

		if( StringUtil.isBlank( interval ) )
			return false;
		
		/** (1,3) */
		String regex0 = "(\\()[0-9]+(.[0-9]+)?,[0-9]+(.[0-9]+)?(\\))";
		/** (1,3] */
		String regex1 = "(\\()[0-9]+(.[0-9]+)?,[0-9]+(.[0-9]+)?(\\])";
		/** [1,3) */
		String regex2 = "(\\[)[0-9]+(.[0-9]+)?,[0-9]+(.[0-9]+)?(\\))";
		/** [1,3] */
		String regex3 = "(\\[)[0-9]+(.[0-9]+)?,[0-9]+(.[0-9]+)?(\\])";

		Pattern pattern0 = Pattern.compile( regex0 );
		Pattern pattern1 = Pattern.compile( regex1 );
		Pattern pattern2 = Pattern.compile( regex2 );
		Pattern pattern3 = Pattern.compile( regex3 );

		Matcher matcher0 = pattern0.matcher( interval );
		Matcher matcher1 = pattern1.matcher( interval );
		Matcher matcher2 = pattern2.matcher( interval );
		Matcher matcher3 = pattern3.matcher( interval );

		interval = StringUtil.replaceAll( interval, "", SymbolConstant.PARENTHESES_BRACKETS_LEFT, SymbolConstant.PARENTHESES_BRACKETS_RIGHT,SymbolConstant.SQUARE_BRACKETS_LEFT, SymbolConstant.SQUARE_BRACKETS_RIGHT );

		String[] intervalArray = null;
		try {
			intervalArray = StringUtil.splitWithFixedLength( interval, SymbolConstant.COMMA, 2 );
		} catch ( NotExpectedFormatedException e ) {
			return false;
		}

		double intervalNumber0 = 0;
		double intervalNumber1 = 0;
		try {
			intervalNumber0 = Double.parseDouble( intervalArray[0] );
			intervalNumber1 = Double.parseDouble( intervalArray[1] );
		} catch ( Exception e ) {
			return false;
		}
		
		if( intervalNumber0 >= intervalNumber1 )
			return false;
		
		/** (1,3) */
		if ( matcher0.matches() ) {
			return ( number > intervalNumber0 && number < intervalNumber1 );
		} else if ( matcher1.matches() ) {
			return ( number > intervalNumber0 && number <= intervalNumber1 );
		} else if ( matcher2.matches() ) {
			return ( number >= intervalNumber0 && number < intervalNumber1 );
		} else if ( matcher3.matches() ) {
			return ( number > intervalNumber0 && number <= intervalNumber1 );
		}
		return false;

	}

}
