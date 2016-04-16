package common.toolkit.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import common.toolkit.util.StringUtil;
import common.toolkit.util.system.SystemUtil;

/**
 * Description: Setting variable for every request.
 * 
 * @author nileader / nileader@gmail.com
 * @Date Jul 29, 2012
 */
public class BaseRequestVariableFilter implements Filter {

	public void destroy() {
	}

	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException,
			ServletException {

		HttpServletRequest req = ( HttpServletRequest ) request;

		StringBuffer requestURL = req.getRequestURL();
		String basePath = requestURL.substring( 0, requestURL.indexOf( req.getContextPath() ) ) + req.getContextPath();
		String hostName = SystemUtil.getHostName();
		String baseHost = req.getServerName() + ":" + req.getServerPort();
		String req_scheme = req.getScheme();
		String req_url_header = req_scheme + "://" + baseHost;
		request.setAttribute( "baseUrl", basePath );
		request.setAttribute( "req_url_header", req_url_header );
		request.setAttribute( "baseHost", baseHost );
		request.setAttribute( "basePath", basePath );
		request.setAttribute( "hostName", StringUtil.trimToEmpty( hostName ) );
		setFlagIsFirerox( req );
		chain.doFilter( request, response );
	}

	private void setFlagIsFirerox( HttpServletRequest request ) {
		String s = request.getHeader( "user-agent" );
		boolean isFirefox = false;
		if ( null != s && s.toLowerCase().indexOf( "firefox" ) > 0 ) {
			isFirefox = true;
		}
		request.setAttribute( "isFirefox", isFirefox );
	}

	@Override
	public void init( FilterConfig filterConfig ) throws ServletException {
	}
}