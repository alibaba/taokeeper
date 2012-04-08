package com.taobao.taokeeper.monitor.web;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import common.toolkit.java.entity.DateFormat;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.DateUtil;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.io.ServletUtil;

/**
 * Report
 * 
 * @author yinshi.nc@taobao.com
 * @since 2011-08-10
 */
@Controller
@RequestMapping("/report.do")
public class ReportController extends BaseController {

	
	private static final Logger LOG = LoggerFactory.getLogger( ReportController.class );
	
	/** PAGE 是否开启报警开关 */
	@RequestMapping(params = "method=reportPAGE")
	public ModelAndView reportPAGE( HttpServletRequest request, HttpServletResponse response, String clusterId, String server, String statDate ){

		try {
			clusterId = StringUtil.defaultIfBlank( clusterId, 1 + EMPTY_STRING );
			statDate  = StringUtil.defaultIfBlank( statDate, DateUtil.getNowTime( DateFormat.Date ) );
			
			String contentOfReport = reportService.getReportContentOfServerConnectionByClusterIdAndServerAndStatDate( Integer.parseInt( clusterId ), server, statDate );
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put( "contentOfReport", contentOfReport );
			model.put("clusterId", clusterId );
			model.put("server", server );
			model.put("statDate", statDate );
			return new ModelAndView( "report/report", model );
			
		} catch (NumberFormatException e) {
			LOG.error( "不合法的clusterId：" + clusterId );
			ServletUtil.writeToResponse(response, "不合法的clusterId：" + clusterId );
			e.printStackTrace();
		} catch ( DaoException e ) {
			LOG.error( "Error when handle db: " + e.getMessage() );
			ServletUtil.writeToResponse(response, "Error when handle db: " + e.getMessage() );
			e.printStackTrace();
		} catch ( Exception e ) {
			LOG.error( "Server error : " + e.getMessage() );
			ServletUtil.writeToResponse(response, "Server error: " + e.getMessage() );
			e.printStackTrace();
		}
		
		return null;
	}

}
