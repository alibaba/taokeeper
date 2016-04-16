package common.toolkit.util.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import common.toolkit.constant.EmptyObjectConstant;
import common.toolkit.constant.EncodingConstant;
import common.toolkit.util.StringUtil;

/**
 * 数据DES加密，注意：执行100w次加密并且解密，花费：87s
 * @author 银时 yinshi.nc@taobao.com
 */
public class CipherUtil {

	final static String DEFAULT_KEY = "NILEADER";
	final static String URL_DECODER = EncodingConstant.UTF8;
	final static String CRYPT_DECODER = EncodingConstant.UTF8;

	/** 解密 */
	public static String decrypt( String message ) throws Exception {

		if ( StringUtil.isBlank( message ) ) {
			return EmptyObjectConstant.EMPTY_STRING;
		}

		byte[] bytesrc = convertHexString( message );
		Cipher cipher = Cipher.getInstance( "DES/CBC/PKCS5Padding" );
		DESKeySpec desKeySpec = new DESKeySpec( DEFAULT_KEY.getBytes( CRYPT_DECODER ) );
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( "DES" );
		SecretKey secretKey = keyFactory.generateSecret( desKeySpec );
		IvParameterSpec iv = new IvParameterSpec( DEFAULT_KEY.getBytes( CRYPT_DECODER ) );

		cipher.init( Cipher.DECRYPT_MODE, secretKey, iv );

		byte[] retByte = cipher.doFinal( bytesrc );
		return java.net.URLDecoder.decode( new String( retByte ), URL_DECODER );
	}

	/** 加密 */
	public static String encrypt( String message ) throws Exception {

		if ( StringUtil.isBlank( message ) ) {
			return EmptyObjectConstant.EMPTY_STRING;
		}

		message = java.net.URLEncoder.encode( message, URL_DECODER ).toLowerCase();

		Cipher cipher = Cipher.getInstance( "DES/CBC/PKCS5Padding" );

		DESKeySpec desKeySpec = new DESKeySpec( DEFAULT_KEY.getBytes( CRYPT_DECODER ) );

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( "DES" );
		SecretKey secretKey = keyFactory.generateSecret( desKeySpec );
		IvParameterSpec iv = new IvParameterSpec( DEFAULT_KEY.getBytes( CRYPT_DECODER ) );
		cipher.init( Cipher.ENCRYPT_MODE, secretKey, iv );

		return toHexString( cipher.doFinal( message.getBytes( CRYPT_DECODER ) ) ).toUpperCase();

	}

	private static byte[] convertHexString( String ss ) {
		byte digest[] = new byte[ss.length() / 2];
		for ( int i = 0; i < digest.length; i++ ) {
			String byteString = ss.substring( 2 * i, 2 * i + 2 );
			int byteValue = Integer.parseInt( byteString, 16 );
			digest[i] = ( byte ) byteValue;
		}
		return digest;
	}

	private static String toHexString( byte b[] ) {
		StringBuffer hexString = new StringBuffer();
		for ( int i = 0; i < b.length; i++ ) {
			String plainText = Integer.toHexString( 0xff & b[i] );
			if ( plainText.length() < 2 )
				plainText = "0" + plainText;
			hexString.append( plainText );
		}
		return hexString.toString();
	}

}