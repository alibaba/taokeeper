package common.toolkit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @yinshi.nc nileader@qq.com
 */
public class GlobalContext {

	private static ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<String, Object>();
	public static void putContext( String key, Object o){
		if( context.contains( key ) ){
			throw new RuntimeException( "Exist such key in context, key: " + key );
		}
		context.put( key, o );
	}
	public static Object getContext( String key ){
		return context.get( key );
	}
	
}
