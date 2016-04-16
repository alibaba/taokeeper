package common.toolkit.util.io;

import static common.toolkit.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.constant.SymbolConstant.AND_SIGN;
import static common.toolkit.constant.SymbolConstant.COLON;
import static common.toolkit.constant.SymbolConstant.EQUAL_SIGN;
import static common.toolkit.constant.SymbolConstant.QUESTION_SIGN;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.toolkit.constant.BaseConstant;
import common.toolkit.constant.EncodingConstant;
import common.toolkit.constant.HttpConstant;
import common.toolkit.constant.SymbolConstant;
import common.toolkit.exception.CanNotEncodeException;
import common.toolkit.exception.IllegalParamException;
import common.toolkit.exception.NotExpectedFormatedException;
import common.toolkit.util.ObjectUtil;
import common.toolkit.util.StringUtil;

/**
 * 类说明: Servlet相关工具类<br>
 * 请注意本类的一些概念：<br>
 * requestURI:  /taokeeper/monitor.do?method=alarm&clusterId=1 <br>
 * requestSimpleURL:  http://www.foo.com/taokeeper/monitor.do <br>
 * requestFullURL:  http://www.foo.com/taokeeper/monitor.do?method=alarm&clusterId=1 <br>
 * contextPath: /taokeeper <br>
 * requestServer: www.foo.com:80 <br>
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class ServletUtil {

	/**
	 * 将一个普通的URL转换成一个其中所有参数最编码的URL<br>
	 * 使用 java.net.URLEncoder<br>
	 * 注意：本方法只会对参数的value进行编码，不会影响参数的key
	 * 
	 * <pre>
	 * encodeParamsForRequestURL("http://www.foo.com/taokeeper/index.do?method=test&userName=中文名字&password=hello1234", "GBK")<br>
	 * =http://www.foo.com/taokeeper/index.do?method=test&userName=%E4%B8%AD%E6%96%87%E5%90%8D%E5%AD%97&password=hello1234
	 * </pre>
	 * @param requestURL 完整的URL路径
	 * @param encoding 参数编码,默认使用UTF-8编码
	 * @return
	 * @throws CanNotEncodeException 
	 */
	public static String encodeParamsForRequestURL( String requestURL, String encoding ) throws CanNotEncodeException {

		//如果requestURL为空 或者 不包含任何参数，那么不经行编码，直接返回
		if( StringUtil.isBlank( requestURL ) || !requestURL.contains( "?" ) )
			return requestURL;
		
		encoding = StringUtil.defaultIfBlank( encoding, EncodingConstant.UTF8 );
		
		/**
		 * e.g.http://www.foo.com/taokeeper/index.do?method=test&userName=
		 * ???&password=hello1234 为了能够处理这样的URL,就不能以?来分割了.
		 */
		requestURL = requestURL.replaceFirst( "[?]", BaseConstant.WORD_SEPARATOR );

		String[] requestURLArry;
		try {
			requestURLArry = StringUtil.splitWithLeastLength( requestURL, BaseConstant.WORD_SEPARATOR, 2 );
		} catch ( NotExpectedFormatedException e ) {
			//再把 ? 变回去
			requestURL = requestURL.replaceFirst( BaseConstant.WORD_SEPARATOR, "?" );
			return requestURL;
		}

		String mainURL 	 = StringUtil.trimToEmpty( requestURLArry[0] );
		String paramsURL = StringUtil.trimToEmpty( requestURLArry[1] );
		
		StringBuffer encodeURL = new StringBuffer( mainURL );
		boolean isFirstParam = true;

		String[] paramsArray = paramsURL.split( AND_SIGN );

		/**
		 * paramsArray中应该是这样：
		 * [key1=value1,key2=,key3=value3]
		 * */
		for ( String param : paramsArray ) {

			if( StringUtil.isBlank( param ) )
				continue;
			//为防止参数value中也包含 =， 这里要处理一下
			param = param.replaceFirst( EQUAL_SIGN, BaseConstant.WORD_SEPARATOR );
			String[] paramSplitByEqual = param.split( BaseConstant.WORD_SEPARATOR );
			if ( 0 == paramSplitByEqual.length )
				continue;

			String key = paramSplitByEqual[0];
			String value = null;
			try {
				if ( 1 == paramSplitByEqual.length ) { //e.g. key2=
					value = EMPTY_STRING;
				} else { // e.g. key1=value1
					value = URLEncoder.encode( paramSplitByEqual[1], encoding );
				}
			} catch ( UnsupportedEncodingException e ) {
				throw new CanNotEncodeException( "Can't encode value by encoding: " + encoding + ", error: " + e.getMessage(), e );
			}
			if ( isFirstParam ) {
				encodeURL.append( "?" + key + "=" + value );
				isFirstParam = false;
			} else
				encodeURL.append( "&" + key + "=" + value );
		}
		return encodeURL.toString();
	}
	
	
	
	
	/**
	 * Get ip where request from
	 */
	public static String getRequestRemoteAddr( HttpServletRequest request ) {
		String forwordIp = request.getHeader("X-Forwarded-For");
        return StringUtil.isBlank( forwordIp ) ? request.getRemoteAddr() : forwordIp;
	}
	
	

	/**
	 * 获取一个请求的资源名(URI)。适用于POST和GET 方式请求,不包含querystring或其它参数<br>
	 * 
	 * <pre>
	 * getRequestURI("http://xxx.com/xxx/test.do")               = /xxx/test.do  <br>
	 * getRequestURI("http://xxx.com/xxx/test.do?param=value")   = /xxx/test.do  <br>
	 * </pre>
	 */
	public static String getRequestURI( HttpServletRequest request ) {
		if ( ObjectUtil.isBlank( request ) )
			return EMPTY_STRING;
		return StringUtil.trimToEmpty( request.getRequestURI() );
	}

	/**
	 * 获取一个请求的URI和QueryString，适用于POST/Get请求<br>
	 * 注意：本方法不会对请求的参数进行编码<br>
	 * 
	 * @return String 请求的资源和查询参数
	 * 
	 * <pre>
	 * getRequestURIAndQueryString("http://xxx.com/xxx/test.do")               	= /xxx/test.do
	 * getRequestURIAndQueryString("http://xxx.com/xxx/test.do?param=value")   	= /xxx/test.do?param=value
	 * getRequestURIAndQueryString(这里如果是一个POST请求，也会解析出QueryString)   	= /xxx/test.do?param=value
	 * </pre>
	 */
	public static String getRequestURIAndQueryString( HttpServletRequest request ) {

		if ( ObjectUtil.isBlank( request ) )
			return EMPTY_STRING;

		StringBuffer requestURIAndQueryString = new StringBuffer( getRequestURI( request ) );
		// 处理GET请求
		if ( HttpConstant.REQUEST_METHOD_GET.equalsIgnoreCase( request.getMethod() ) ) {
			String queryString = request.getQueryString();
			if ( !StringUtil.isBlank( queryString ) ) {
				requestURIAndQueryString.append( QUESTION_SIGN + queryString );
			}
		} else {// 针对POST请求
			boolean isFirstParam = true;
			@SuppressWarnings("rawtypes")
			Map map = request.getParameterMap();
			if ( null != map ) {
				for ( Object param : map.keySet() ) {
					String key = ( String ) param;
					String value = StringUtil.defaultIfBlank( request.getParameter( key ), EMPTY_STRING );
					if ( isFirstParam ) {
						requestURIAndQueryString.append( QUESTION_SIGN + key + EQUAL_SIGN + value );
						isFirstParam = false;
					} else if ( !isFirstParam ) {
						requestURIAndQueryString.append( AND_SIGN + key + EQUAL_SIGN + value );
					}
				}
			}
		}
		return requestURIAndQueryString.toString();
	}
	
	
	/**
	 * 从request中获取 ContextPath。类似方法：getContextPathFromRequestURI( String
	 * requestURI )
	 * @param request
	 * @return
	 */
	public static String getContextPathFromRequest( HttpServletRequest request ) {
		if ( ObjectUtil.isBlank( request ) )
			return EMPTY_STRING;
		return request.getContextPath();
	}

	/**
	 * 从requestURI中获取 ContextPath。类似方法：getContextPathFromRequest(
	 * HttpServletRequest request )
	 * @param requestURI
	 * @return
	 */
	public static String getContextPathFromRequestURI( final String requestURI ) {

		if ( StringUtil.isBlank( requestURI ) )
			return EMPTY_STRING;
		String[] requestURIArray;
		try {
			requestURIArray = StringUtil.splitWithLeastLength( requestURI, SymbolConstant.SLASH, 2 );
		} catch ( NotExpectedFormatedException e ) {
			return EMPTY_STRING;
		}
		return SymbolConstant.SLASH + requestURIArray[1];
	}

	/**
	 * 从Request中获取请求的域名,以点开头( e.g. .foo.com )
	 * @param request
	 * @return String 域名(不包含端口号,e.g. .foo.com)
	 * 
	 *         <pre>
	 * .foo.com
	 * </pre>
	 */
	public static String getDomainFromRequest( HttpServletRequest request ) {

		if ( ObjectUtil.isBlank( request ) )
			return EMPTY_STRING;

		String server = StringUtil.trimToEmpty( request.getServerName() );
		return StringUtil.trimToEmpty( server.replaceFirst( server.split( "[.]" )[0], EMPTY_STRING ) );
	}

	/**
	 * 从requestURL中获取请求的域名,以点开头( e.g. .jm.foo.net )
	 * @param requestURL
	 * @return String 域名(不包含端口号,e.g. .jm.foo.net)
	 * 
	 *         <pre>
	 * getDomainFromRequestURL( &quot;http://ops.jm.foo.net/taokeeper/monitor.do...&quot; ) = &quot;.jm.foo.net&quot;;
	 * </pre>
	 */
	public static String getDomainFromRequestURL( final String requestURL ) throws IllegalParamException {

		if ( StringUtil.isBlank( requestURL ) )
			return EMPTY_STRING;

		Pattern p = Pattern.compile( "[^//]*?\\.(com|cn|net|org|biz|info|cc|tv|cc)", Pattern.CASE_INSENSITIVE );
		Matcher matcher = p.matcher( requestURL );
		if ( !matcher.find() )
			throw new IllegalParamException( requestURL + " is not a legal request url " );

		String domainOfRequest = matcher.group();
		domainOfRequest = domainOfRequest.replaceFirst( domainOfRequest.split( "[.]" )[0], "" );
		return domainOfRequest;
	}

	
	/**
	 * 获取一个请求的QueryString，适用于POST/Get请求<br>
	 * 注意：本方法不会对请求的参数进行编码<br>
	 * 
	 * @return String 请求的资源和查询参数  key1=v1&key2=v2
	 * 
	 * <pre>
	 * getQueryStringFromRequest("http://xxx.com/xxx/test.do")               	= ""
	 * getQueryStringFromRequest("http://xxx.com/xxx/test.do?param=value")   	= "param=value"
	 * getQueryStringFromRequest(这里如果是一个POST请求，也会解析出QueryString)  = "param=value"
	 * </pre>
	 */
	public static String getQueryStringFromRequest( HttpServletRequest request ) {

		if ( ObjectUtil.isBlank( request ) )
			return EMPTY_STRING;

		StringBuffer requestURIAndQueryString = new StringBuffer();
		// 处理GET请求
		if ( HttpConstant.REQUEST_METHOD_GET.equalsIgnoreCase( request.getMethod() ) ) {
			String queryString = request.getQueryString();
			if ( !StringUtil.isBlank( queryString ) ) {
				requestURIAndQueryString.append( queryString );
			}
		} else {// 针对POST请求
			boolean isFirstParam = true;
			@SuppressWarnings("rawtypes")
			Map map = request.getParameterMap();
			if ( null != map ) {
				for ( Object param : map.keySet() ) {
					String key = ( String ) param;
					String value = StringUtil.defaultIfBlank( request.getParameter( key ), EMPTY_STRING );
					if ( isFirstParam ) {
						requestURIAndQueryString.append( key + EQUAL_SIGN + value );
						isFirstParam = false;
					} else if ( !isFirstParam ) {
						requestURIAndQueryString.append( AND_SIGN + key + EQUAL_SIGN + value );
					}
				}
			}
		}
		return requestURIAndQueryString.toString();
	}
	
	
	
	/**
	 * 从Request中获取request请求的Server( ip:port or domain:port )
	 * @param request
	 * @return String Server( ip:port or domain:port )
	 * 
	 *         <pre>
	 * ops.jm.foo.net:8080
	 * </pre>
	 */
	public static String getServerFromRequest( HttpServletRequest request ) {
		if ( ObjectUtil.isBlank( request ) )
			return EMPTY_STRING;
		return request.getServerName() + COLON + request.getServerPort();
	}

	/**
	 * 从requestURL中获取request请求的Server( ip:port or domain:port )
	 * @param requestURL 
	 *            http://ops.jm.foo.net/taokeeper/monitor.do?method=alarm
	 *            &clusterId=1 <br>
	 * @return String Server( ip:port or domain:port )
	 * 
	 *         <pre>
	 * ops.jm.foo.net:80
	 * </pre>
	 * @throws IllegalParamException
	 */
	public static String getServerFromRequestURL( final String requestURL ) throws IllegalParamException {

		if ( StringUtil.isBlank( requestURL ) )
			return EMPTY_STRING;

		String server = EMPTY_STRING;
		if ( requestURL.startsWith( HttpConstant.HTTP_PREFIX ) )
			server = requestURL.substring( 7 ).split( "/" )[0];
		else if ( requestURL.startsWith( HttpConstant.HTTPS_PREFIX ) )
			server = requestURL.substring( 8 ).split( "/" )[0];
		else
			throw new IllegalParamException( requestURL + " is not a legal request url " );

		if ( !StringUtil.trimToEmpty( server ).contains( COLON ) ) {
			server += COLON + "80";
		}
		return server;
	}

	
	/**
	 * 获取一个请求的简单URL（requestSimpleURL:  http://ops.jm.foo.net/taokeeper/monitor.do）。适用于POST/Get请求<br>
	 * 注意：本方法不含任何参数<br>
	 * @return String requestSimpleURL
	 * 
	 * <pre>
	 * getSimpleURLFromRequest("http://xxx.com/xxx/test.do")               	= http://xxx.com/xxx/test.do
	 * getSimpleURLFromRequest("http://xxx.com/xxx/test.do?param=value")   	= http://xxx.com/xxx/test.do
	 * getSimpleURLFromRequest(这里如果是一个POST请求，也会解析出QueryString)   = http://xxx.com/xxx/test.do
	 * </pre>
	 */
	public static String getSimpleURLFromRequest( HttpServletRequest request ) {
		if ( ObjectUtil.isBlank( request ) )
			return EMPTY_STRING;
		return request.getRequestURL().toString();
	}
	
	/**
	 * 获取一个请求的完整URL,包含协议头, Server信息以及所有参数。适用于POST/Get请求<br>
	 * 注意：本方法不会对请求的参数进行编码<br>
	 * 
	 * @return String requestFullURL
	 * <pre>
	 * getRequestURL("http://xxx.com/xxx/test.do")               	= http://xxx.com/xxx/test.do
	 * getRequestURL("http://xxx.com/xxx/test.do?param=value")   	= http://xxx.com/xxx/test.do?param=value
	 * getRequestURL(这里如果是一个POST请求，也会解析出QueryString)   = http://xxx.com/xxx/test.do?param=value
	 * </pre>
	 */
	public static String getFullURLFromRequest( HttpServletRequest request ) {

		if ( ObjectUtil.isBlank( request ) )
			return EMPTY_STRING;
		String simpleURL   = StringUtil.trimToEmpty( ServletUtil.getSimpleURLFromRequest( request ) );
		String queryString = StringUtil.trimToEmpty( ServletUtil.getQueryStringFromRequest( request ) ); 
		if( !StringUtil.isBlank( queryString ) ){
			queryString = StringUtil.setPrefix( queryString, QUESTION_SIGN );
		}
		return  simpleURL + queryString ;
	}
	
	/**
	 * 从一个 server （127.0.0.1:8080）中解析出 [127.0.0.1,8080] return ip: stirng[0]
	 * port: string[1] 放心，不会出现null pointer, 数组长度一定是2
	 */
	public static String[] paraseIpAndPortFromServer( String server ) {
		if ( StringUtil.isBlank( server ) )
			return new String[] { EMPTY_STRING, EMPTY_STRING };
		String[] serverArray = server.split( COLON );
		if ( 0 == serverArray.length )
			return new String[] { EMPTY_STRING, EMPTY_STRING };
		else if ( 1 == serverArray.length )
			return new String[] { serverArray[0], EMPTY_STRING };
		else if ( 2 == serverArray.length )
			return serverArray;
		return new String[] { EMPTY_STRING, EMPTY_STRING };
	}
	
	
	
	
	public static void showSystemErrorToResponse( HttpServletResponse response, Throwable e, String encoding ) {
		ServletUtil.writeToResponse( response, "System exception occurs, please try again later: " + e.getMessage(), encoding );
	}

	public static void showSystemErrorToResponse( HttpServletResponse response, Throwable e ) {
		showSystemErrorToResponse( response, e, EncodingConstant.UTF8 );
	}

	/**
	 * 向客户端Response写数据,默认采用UTF-8编码
	 * 
	 * @throws IOException
	 * */
	public static void writeToResponse( HttpServletResponse response, String content ) {
		writeToResponse( response, content, EncodingConstant.UTF8 );
	}

	/**
	 * 向客户端Response写数据
	 * 
	 * @throws IOException
	 * */
	public static boolean writeToResponse( HttpServletResponse response, String content, String encoding ) {
		response.setContentType( "text/html" );
		response.setCharacterEncoding( encoding );
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch ( IOException e ) {
			return false;
		}
		out.write( content );
		out.flush();
		return true;
	}

}