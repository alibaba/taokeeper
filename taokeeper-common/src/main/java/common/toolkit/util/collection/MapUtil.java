package common.toolkit.util.collection;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.toolkit.util.ObjectUtil;
import common.toolkit.util.StringUtil;
import common.toolkit.util.number.LongUtil;

/**
 * Map相关的工具类
 * 
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 8, 2012
 */
public class MapUtil extends CollectionUtil {

	public static Map< String, Long > addValue( Map< String, Long > map1, Map< String, Long > map2 ) {

		if ( CollectionUtil.isBlank( map1 ) )
			return MapUtil.trimToEmpty( map2 );
		if ( CollectionUtil.isBlank( map2 ) )
			return MapUtil.trimToEmpty( map1 );

		Map< String, Long > map3 = new HashMap< String, Long >();

		for ( String key : map1.keySet() ) {
			key = StringUtil.trimToEmpty( key );
			long value1 = LongUtil.defaultIfNull( map1.get( key ) );
			long value2 = LongUtil.defaultIfNull( map2.get( key ) );
			long value3 = value1 + value2;
			map3.put( key, value3 );
			map2.remove( key );
		}
		map3.putAll( map2 );
		return map3;
	}

	/**
	 * 获取map的大小，放心，这个方法不会发生NPE
	 * 
	 * @param map
	 * @return size of map(0 if null == map)
	 */
	public static int size( Map< ?, ? > map ) {
		if ( ObjectUtil.isBlank( map ) ) {
			return 0;
		} else
			return map.size();
	}

	/**
	 * map对象是否为空
	 * 
	 * @param map
	 *            if null == map || map.size == 0
	 */
	public static boolean isBlank( Map< ?, ? > map ) {
		if ( 0 == MapUtil.size( map ) )
			return true;
		return false;
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static Map sortByValue( Map map ) {
		List list = new LinkedList( map.entrySet() );
		Collections.sort( list, new Comparator() {

			public int compare( Object o1, Object o2 ) {
				return ( ( Comparable ) ( ( Map.Entry ) ( o2 ) ).getValue() ).compareTo( ( ( Map.Entry ) ( o1 ) ).getValue() );

			}
		} );
		Map result = new LinkedHashMap();

		for ( Iterator it = list.iterator(); it.hasNext(); ) {
			Map.Entry entry = ( Map.Entry ) it.next();
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

	public static Long sumValue( Map< String, Long > map ) {
		if ( CollectionUtil.isBlank( map ) ) {
			return 0l;
		}
		Long sum = 0l;
		for ( Object o : map.keySet() ) {
			Long value = map.get( o );
			sum += value;
		}
		return sum;
	}

	public static < K, V > HashMap< K, V > trimToEmpty( Map< K, V > collection ) {
		if ( CollectionUtil.isBlank( collection ) )
			return new HashMap< K, V >();
		return new HashMap< K, V >( collection );
	}

	
	/**
	 * Map -> String
	 * <pre>
	 * return [key1:value1,key2:value2]
	 * </pre>
	 * @param map
	 * @return
	 */
	public static < K, V > String toString( Map< K, V > map ) {
		if ( null == map || map.size() == 0 ) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder( "[" );
		for ( Object o : map.keySet() ) {
			if ( ObjectUtil.isBlank( o ) )
				continue;
			String key = o.toString();
			Object valueObject = map.get( o );
			String value = "";
			if ( !ObjectUtil.isBlank( valueObject ) ) {
				value = valueObject.toString();
			}
			sb.append( key + ":" + value ).append( "," );
		}

		String str = sb.toString();
		if ( str.endsWith( "," ) ) {
			str = StringUtil.replaceLast( str, "", "" );
		}

		return str + "]";
	}

}
