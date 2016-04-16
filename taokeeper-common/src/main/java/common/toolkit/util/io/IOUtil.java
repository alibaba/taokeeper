package common.toolkit.util.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import common.toolkit.constant.EncodingConstant;
import common.toolkit.util.StringUtil;

/**
 * IO相关的工具类
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class IOUtil {


	/**
	 * Convert inputStream to String
	 * @params encoding  可以为"", 默认为 GBK 
	 * @throws IOException
	 */
	public static String convertInputStream2String(InputStream is, String encoding) throws IOException {
		if (null == is)
			return "";
		if( StringUtil.isBlank( encoding ) ){
			encoding = EncodingConstant.GBK;
		}
	    BufferedReader in = new BufferedReader( new InputStreamReader( is, encoding ) );
	    StringBuffer buffer = new StringBuffer();
	    String line = "";
	    while ((line = in.readLine()) != null){
	      buffer.append(line).append( "\n" );
	    }
	    return buffer.toString();
	}
	
	
	/**
	 * Convert String to InputStream with special encoding
	 * @param  str          字符串
	 * @param  encoding     编码
	 * @return InputStream  流
	 * @throws UnsupportedEncodingException
	 */
    public static InputStream convertString2InputStream( String str, String encoding ) throws UnsupportedEncodingException {
        if ( !StringUtil.isBlank( str ) ) {
            ByteArrayInputStream stringInputStream = new ByteArrayInputStream( str.getBytes( encoding ) );
            return stringInputStream;
        }
        return null;
    }
	
	
    /** 关闭  InputStream 流 */
	public static void closeInputStream( InputStream is ){
		if( null != is ){
			try {
				is.close();
			} catch ( IOException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    /** 关闭  Reader 流 */
	public static void closeReader( Reader reader ){
		if( null != reader ){
			try {
				reader.close();
			} catch ( IOException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    /** 关闭  Writer 流 */
	public static void closeWriter( Writer writer ){
		if( null != writer ){
			try {
				writer.close();
			} catch ( IOException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
