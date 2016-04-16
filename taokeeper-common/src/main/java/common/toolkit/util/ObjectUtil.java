package common.toolkit.util;


import java.util.HashMap;
import java.util.Map;

/**
 * @author  nileader / nileader@gmail.com
 * @Date	 Feb 15, 2012
 */
public class ObjectUtil {

	//用于全局存储
	public static Map<String, String> GLOBAL_STORE = new HashMap<String, String>();
	
	public static Object defaultIfBlank( Object object, Object defaultObject ){
		if( ObjectUtil.isBlank( object ) )
			return defaultObject;
		return object;
	}
	
	/** 是否null*/
	public static boolean isBlank( Object object ){
		return null == object;
	}
	
	/***
	 * check if originalObjectArray
	 * @return true if have one blank at least.
	 */
	public static boolean isBlank( Object... originalObjectArray ) {

		if ( null == originalObjectArray || 0 == originalObjectArray.length )
			return true;
		for ( int i = 0; i < originalObjectArray.length; i++ ) {
			if ( isBlank( originalObjectArray[i] ) )
				return true;
		}
		return false;
	}
	
}
