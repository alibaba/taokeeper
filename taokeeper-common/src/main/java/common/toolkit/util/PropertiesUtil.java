package common.toolkit.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import common.toolkit.constant.SymbolConstant;
import common.toolkit.util.collection.CollectionUtil;
import common.toolkit.util.io.IOUtil;

/**
 * @author nileader / nileader@gmail.com
 * @Date 2012-4-11
 */
public class PropertiesUtil {

	public static Map< String, String > convertPropertiesToMap( Properties properties ) {

		Map< String, String > map = new HashMap< String, String >();
		if ( ObjectUtil.isBlank( properties ) )
			return map;
		for ( Map.Entry< Object, Object > entry : properties.entrySet() ) {
			String key = ( String ) entry.getKey();
			map.put( key, ( String ) entry.getValue() );
		}
		return map;
	}

	public static Map< String, String > convertPropertiesToMap( String propertiesStr ) {

		if ( StringUtil.isBlank( propertiesStr ) ) {
			return new HashMap< String, String >();
		}

		InputStream inConf = new ByteArrayInputStream( propertiesStr.getBytes() );
		try {
			Properties properties = new Properties();
			properties.load( inConf );
			return PropertiesUtil.convertPropertiesToMap( properties );
		} catch ( Throwable e ) {
			e.printStackTrace();
			return new HashMap< String, String >();
		} finally {
			IOUtil.closeInputStream( inConf );
		}
	}

	public static String convertMapToPropertiesString( Map< String, String > map ) {
		StringBuffer sb = new StringBuffer();
		if ( CollectionUtil.isBlank( map ) )
			return sb.toString();

		for ( String str : map.keySet() ) {
			String key = StringUtil.trimToEmpty( str );
			String value = StringUtil.trimToEmpty( map.get( key ) );
			sb.append( key ).append( SymbolConstant.EQUAL_SIGN ).append( value ).append( "\n" );
		}
		return sb.toString();
	}

}
