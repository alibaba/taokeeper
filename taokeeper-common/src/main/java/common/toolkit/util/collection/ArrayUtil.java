package common.toolkit.util.collection;


import java.io.File;
import java.util.ArrayList;

import common.toolkit.util.StringUtil;



/**
 * 绫昏鏄� 鏁扮粍鐩稿叧宸ュ叿绫�
 * @author 閾舵椂 yinshi.nc@taobao.com
 */
public class ArrayUtil {
	
	public static File[] getLastNElementOfArray( File[] fileArray, int n ){
		
		if( null == fileArray || 0 == fileArray.length ){
			return null;
		}
		//濡傛灉瑕佽幏鍙栫殑涓暟澶т簬绛変簬鏁版嵁闀垮害, 閭ｄ箞杩斿洖鍏ㄩ儴
		if( n >= fileArray.length )
			return fileArray;

		//鍙栨渶鍚巒涓厓绱犺繑鍥�
		int index = fileArray.length - n;
		File[] fileArray2 = new File[n];
		for( int i = index, j = 0; i < fileArray.length; i++, j++ ){
			fileArray2[j] = fileArray[i];
		}
		return fileArray2;
	}
	
	/**
	 * Convert String[] to ArrayList<String>
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> toArrayList( String[] array ){
		ArrayList<String> arrayList = new ArrayList<String>();
		if( null == array ||  0 == array.length ){
			return arrayList;
		}
		for( int i = 0; i < array.length; i++ ){
			arrayList.add( array[i] );
		}
		return arrayList;
	}
	
	
	public static String toString( String[] array ){
		StringBuffer sb = new StringBuffer( "[");
		if( null == array )
			return sb.append( "]" ).toString();
		
		for( String e : array ){
			sb.append( e ).append( "," );
		}
		return StringUtil.replaceLast( sb.toString(), ",", "]" );
		
	}
	
	
	
}