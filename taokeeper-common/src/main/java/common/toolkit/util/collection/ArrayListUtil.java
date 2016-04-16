package common.toolkit.util.collection;

import java.util.ArrayList;

import common.toolkit.util.number.IntegerUtil;

/**
 * Description: ArrayList 工具类
 * @author   银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date	 Jan 13, 2012
 */
public class ArrayListUtil extends CollectionUtil {

	
	
	/**
	 * 按步长从一个ArrayList<T>中取出值,成为一个新的ArrayList<T> <br>
	 * 注意,第一个无论如何都会选取第一个元素
	 * @param arrayList		原始ArrayList<T>
	 * @param stepLength	每隔多少取一个值
	 * <pre>
	 * 例如:
	 * 原始ArrayList<T>：[yinshi,nichao,nileader,test,name,abc]
	 * stepLength = 2,那么返回
	 * [yinshi,nileader,name]
	 * </pre>
	 */
	public static <T> ArrayList<T> select( ArrayList<T> arrayList, int stepLength ){
		
		ArrayList<T> arrayListNew = new ArrayList< T >();
		if ( CollectionUtil.isBlank( arrayList ) ){
			return arrayListNew;
		}
		
		stepLength = IntegerUtil.defaultIfSmallerThan0( stepLength, 1 );
		if( 1 == stepLength )
			return arrayList;
		
		int index = 0;
		while( index + stepLength < arrayList.size() ){
			arrayListNew.add( arrayList.get( index ) );
			index += stepLength;
		}
		arrayListNew.add( arrayList.get( index ) );
		
		return arrayListNew;
	}
	
	
}
