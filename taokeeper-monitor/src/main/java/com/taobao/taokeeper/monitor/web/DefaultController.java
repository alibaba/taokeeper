package com.taobao.taokeeper.monitor.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Admin
 * @author yinshi.nc@taobao.com
 * @since 2011-11-15
 */
@Controller
@RequestMapping("/default.do")
public class DefaultController extends BaseController {
	
	
	@RequestMapping( method = RequestMethod.GET )
	public String switchOfNeedAlarmPAGE(HttpServletRequest request, HttpServletResponse response ) {
		return "redirect:/zooKeeperStatus.do?method=showZooKeeperStatusPAGE";
	}
	
}
