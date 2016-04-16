package common.toolkit.util.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 * @yinshi.nc nileader@qq.com
 */
public class MD5Util {

	public static String encode( String str, String charset ) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {

		MessageDigest messageDigest = MessageDigest.getInstance( "MD5" );
		messageDigest.reset();
		messageDigest.update( str.getBytes( charset ) );
		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for ( int i = 0; i < byteArray.length; i++ ) {
			if ( Integer.toHexString( 0xFF & byteArray[i] ).length() == 1 )
				md5StrBuff.append( "0" ).append( Integer.toHexString( 0xFF & byteArray[i] ) );
			else
				md5StrBuff.append( Integer.toHexString( 0xFF & byteArray[i] ) );
		}

		return md5StrBuff.toString().toLowerCase();
	}
	
	public static void main( String[] args ) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		System.out.println( MD5Util.encode( "3e3a74484fbd037c6dad15f790f840cc041521", "GBK" ) );
		
	}
	
	
	
	
	
	
	
	
}