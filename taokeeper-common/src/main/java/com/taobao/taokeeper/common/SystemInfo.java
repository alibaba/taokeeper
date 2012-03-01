package com.taobao.taokeeper.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.toolkit.java.util.system.SystemUtil;


/**
 * Description: 系统环境
 * 
 * @author yinshi.nc
 * @Date 2011-10-31
 */
public class SystemInfo {

	Logger logger =  LoggerFactory.getLogger(SystemInfo.class);

	public static String systemHost;
	public static String systemJavaRuntimeInfo;
	public static String envName;

	/** 设置变量, 服务器启动的时候会自动调用. */
	public void setEnvName( String envName ) {
		SystemInfo.systemHost = SystemUtil.getOSName();
		SystemInfo.envName = envName;
		logger.warn( "注意: " + envName + "环境启动" );
	}

}
