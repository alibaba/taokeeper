package common.toolkit.util;


/**
 * Description: Boolean util class.
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class BooleanUtil {

	
	/**
	 * Convert string to boolean<br>
	 * 
	 * @return true only if "true", else return false;
	 */
	public static boolean parseBoolean( String booleanStr ) {
		
		if( StringUtil.isBlank( booleanStr ) )
			return false;
		
		if( StringUtil.trimToEmpty( booleanStr ).equalsIgnoreCase( "true" ) )
			return true;
		return false;
	}

	

}
