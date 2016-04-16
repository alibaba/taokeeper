package common.toolkit.util.collection;

import static common.toolkit.constant.EmptyObjectConstant.EMPTY_STRING;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.toolkit.constant.SymbolConstant;
import common.toolkit.util.ObjectUtil;
import common.toolkit.util.StringUtil;

/**
 * 集合类的公共方法
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 11, 2012
 */
public class CollectionUtil extends ObjectUtil {

	/**
	 * Check if collection contains element.
	 * @return true: null == collection || 0 == collection.size()
	 * @return false: null != collection && 0 != collection.size()
	 */
	public static boolean isBlank( Collection< ? extends Object > collection ) {
		if ( null == collection || 0 == collection.size() ) {
			return true;
		}
		return false;
	}

	/**
	 * Convert Collection< String > to String, 并且使用split来分隔，不含空格。
	 * @param split 需要分隔的字符
	 * @return String
	 */
	public static String toString( Collection< String > collection, String split ) {

		if ( null == collection || collection.isEmpty() ) {
			return EMPTY_STRING;
		}
		String str = EMPTY_STRING;
		for ( String _str : collection ) {
			str += _str + split;
		}
		str = StringUtil.replaceLast( str, split, EMPTY_STRING );
		return str;
	}

	/**
	 * Convert Collection< String > to String, 并且使用 ,来分隔，不含空格。
	 * @param split 需要分隔的字符
	 * @return String
	 */
	public static String toString( Collection< String > collection ) {
		return toString( collection, SymbolConstant.COMMA );
	}

	public static final <K, V> Map< K, V > emptyMap() {
		return Collections.emptyMap();
	}

	public static final <T> Set< T > emptySet() {
		return Collections.emptySet();
	}

	public static final <T> List< T > emptyList() {
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public static Map< String, Long > frequency( Collection< String > collection ) {

		Map< String, Long > map = new HashMap< String, Long >();

		if ( CollectionUtil.isBlank( collection ) ) {
			return map;
		}
		for ( String key : collection ) {
			key = StringUtil.trimToEmpty( key );
			if ( map.containsKey( key ) ) {
				map.put( key, map.get( key ) + 1 );
			} else {
				map.put( key, 1l );
			}
		}

		return MapUtil.sortByValue( map );
	}
	
	
	/**
	 * Returns a new java.util.Collection containing a - b. The cardinality of each element e in the returned java.util.Collection will be the cardinality of e in a minus the cardinality of e in b, or zero, whichever is greater.
	 * @param a the collection to subtract from, must not be null
	 * @param b the collection to subtract, must not be null
	 * @return a new collection with the results
	 */
	public static <T> Collection< T > subtract( final Collection< T > a, final Collection< T > b ) {
		
		if( CollectionUtil.isBlank( a ) ){
			return new ArrayList< T >();
		}
		if( CollectionUtil.isBlank( b ) ){
			return a;
		}
		
		Collection< T > list = new ArrayList< T >( a );
		for ( Iterator< T > it = b.iterator(); it.hasNext(); ) {
			list.remove( it.next() );
		}
		return list;
	}
	
}
