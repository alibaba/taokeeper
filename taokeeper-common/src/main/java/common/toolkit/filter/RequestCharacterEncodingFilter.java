package common.toolkit.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.toolkit.constant.EmptyObjectConstant;
import common.toolkit.constant.HttpConstant;
import common.toolkit.util.StringUtil;

/**
 * 对get/post方式的请求分别进行编码处理<br>
 * 默认都是UTF-8编码，可以通过参数：request-encoding-get和request-encoding-post来指定编码。<br>
 * 只有指定了相应HTTP-METHOD方法的编码方式，才会进行编码，否则不会编码。<br>
 * @author 银时 yinshi.nc@taobao.com
 */
public class RequestCharacterEncodingFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger( RequestCharacterEncodingFilter.class );
	
	private String requestEncodingOfGet  = EmptyObjectConstant.EMPTY_STRING;
	private String requestEncodingOfPost = EmptyObjectConstant.EMPTY_STRING;

	public void destroy() {
	}

	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = ( HttpServletRequest ) request;
		if ( StringUtil.equalsIgnoreCaseAll( HttpConstant.REQUEST_METHOD_GET, httpServletRequest.getMethod() ) && !StringUtil.isBlank( this.requestEncodingOfGet ) ){
			request.setCharacterEncoding( this.requestEncodingOfGet );
		} else if ( StringUtil.equalsIgnoreCaseAll( HttpConstant.REQUEST_METHOD_POST, httpServletRequest.getMethod() ) && !StringUtil.isBlank( this.requestEncodingOfPost ) ) {
			request.setCharacterEncoding( this.requestEncodingOfPost );
		}

		chain.doFilter( request, response );
	}

	@Override
	public void init( FilterConfig filterConfig ) throws ServletException {
		this.requestEncodingOfGet = StringUtil.trimToEmpty( filterConfig.getInitParameter( HttpConstant.REQUEST_ENCODING_GET ) );
		this.requestEncodingOfPost = StringUtil.trimToEmpty( filterConfig.getInitParameter( HttpConstant.REQUEST_ENCODING_POST ) );
		
		if( StringUtil.isBlank( requestEncodingOfGet ) )
			LOG.warn( "对没有对 request-encoding-get 进行设置，将不会对GET方式的请求进行编码" );
		if( StringUtil.isBlank( requestEncodingOfPost ) )
			LOG.warn( "对没有对 request-encoding-post 进行设置，将不会对POST方式的请求进行编码" );
		
	}
}