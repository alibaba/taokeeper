package common.toolkit.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.toolkit.constant.EmptyObjectConstant;
import common.toolkit.constant.SymbolConstant;
import common.toolkit.util.StringUtil;
import common.toolkit.util.number.IntegerUtil;
import common.toolkit.util.number.NumberUtil;
import common.toolkit.util.number.RandomUtil;

/**
 * 类说明: List相关工具类
 * @author 银时 yinshi.nc@taobao.com
 */
public class ListUtil {

	public static <T> List< T > convertToList( Set< T > collection ) {
		if ( CollectionUtil.isBlank( collection ) )
			return CollectionUtil.emptyList();
		return new ArrayList< T >( collection );
	}

	/**
	 * Get a random element from collection.
	 * @param collection
	 * @return
	 */
	public static <T> T getRandomElement( List< T > collection ){
		if( CollectionUtil.isBlank( collection ) )
			return null;
		return collection.get( RandomUtil.getInt( collection.size() ) );
	}
	
	/**
	 * 把一个字符串转换成List
	 * @param originalStr abc, def,helloword,myname
	 * @param splitStr ,
	 * @return List<String>
	 */
	public static List< String > parseList( String originalStr ) {
		return ListUtil.parseList( originalStr, SymbolConstant.COMMA );
	}

	/**
	 * 把一个字符串转换成List
	 * @param @param originalStr abc, def,helloword,myname
	 * @param @param splitStr ,
	 * @return List<String>
	 */
	public static List< String > parseList( String originalStr, String splitStr ) {
		List< String > list = new ArrayList< String >();
		if ( StringUtil.isBlank( originalStr ) || StringUtil.isBlank( splitStr ) )
			return list;
		return ArrayUtil.toArrayList( originalStr.split( splitStr ) );
	}

	public static <T> List< T > reverseList( List< T > collection ) {

		if ( CollectionUtil.isBlank( collection ) )
			return collection;
		List< T > collectionNew = new ArrayList< T >();
		for ( int i = collection.size() - 1; i >= 0; i-- ) {
			collectionNew.add( collection.get( i ) );
		}
		return collectionNew;
	}

	/**
	 * Return the sublist of list.<br>
	 * Note:No worry of java.lang.StringIndexOutOfBoundsException
	 * @param list original
	 * @param fromIndex
	 * @param size
	 * @return
	 */
	public static <T> List< T > subList( List< T > list, int fromIndex, int size ) {

		if ( CollectionUtil.isBlank( list ) )
			return CollectionUtil.emptyList();
		if ( NumberUtil.isNegative( fromIndex, size ) ) {
			return CollectionUtil.emptyList();
		}

		int endIndex = IntegerUtil.maxIfTooBig( fromIndex + size, list.size() );
		return list.subList( fromIndex, endIndex );
	}

	public static <T> ArrayList< T > trimToEmpty( List< T > collection ) {
		if ( CollectionUtil.isBlank( collection ) )
			return new ArrayList< T >();
		return new ArrayList< T >( collection );
	}

	/**
	 * Convert Collection< String > to String, 并且使用split来分隔，不含空格。
	 * @param split 需要分隔的字符
	 * @return String
	 */
	public static String toString( Collection< ? extends Object > collection, String split ) {

		if ( null == collection || collection.isEmpty() ) {
			return EmptyObjectConstant.EMPTY_STRING;
		}
		String str = EmptyObjectConstant.EMPTY_STRING;
		for ( Object object : collection ) {
			str += object.toString() + split;
		}
		str = StringUtil.replaceLast( str, split, EmptyObjectConstant.EMPTY_STRING );
		return str;
	}

	/**
	 * Convert Collection< String > to String, 并且使用 ,来分隔，不含空格。
	 * @param split 需要分隔的字符
	 * @return String
	 */
	public static String toString( Collection< ? extends Object > collection ) {
		return toString( collection, SymbolConstant.COMMA );
	}

	/**
	 * split a big list to some small list.
	 * @param list
	 * @param size sublist size.
	 * @return
	 */
	public static <T> Map< Integer, List< T > > split( List< T > list, int size ) {
		size = IntegerUtil.defaultIfSmallerThan0( size, 100 );
		Map< Integer, List< T > > map = new HashMap< Integer, List< T > >();
		if ( CollectionUtil.isBlank( list ) )
			return map;
		if ( list.size() <= size ) {
			map.put( 1, list );
			return map;
		}
		int i = 0;
		int j = 0;
		List< T > subList = new ArrayList< T >();
		for ( T t : list ) {
			subList.add( t );
			if ( ++j >= size ) {
				map.put( ++i, subList );
				j = 0;
				subList = new ArrayList< T >();
			}
		}

		if ( !CollectionUtil.isBlank( subList ) ) {
			map.put( ++i, subList );
		}

		return map;
	}

	public static Map< String, List< String > > diff( List< String > list1, List< String > list2 ) {

		Map< String, List< String > > diffMap = new HashMap< String, List< String > >();
		List< String > list1Have = new ArrayList< String >();
		List< String > bothHave = new ArrayList< String >();
		List< String > list2Have = new ArrayList< String >();

		if ( !CollectionUtil.isBlank( list1 ) && CollectionUtil.isBlank( list2 ) ) {
			list1Have = list1;
		} else if ( CollectionUtil.isBlank( list1 ) && !CollectionUtil.isBlank( list2 ) ) {
			list2Have = list2;
		} else if ( !CollectionUtil.isBlank( list1 ) && !CollectionUtil.isBlank( list2 ) ) {
			list1Have = ( List< String > ) CollectionUtil.subtract( list1, list2 );
			list2Have = ( List< String > ) CollectionUtil.subtract( list2, list1 );
			bothHave = ( List< String > ) CollectionUtil.subtract( list1, list1Have );
		}
		diffMap.put( "list1Have", list1Have );
		diffMap.put( "bothHave", bothHave );
		diffMap.put( "list2Have", list2Have );
		return diffMap;
	}

}