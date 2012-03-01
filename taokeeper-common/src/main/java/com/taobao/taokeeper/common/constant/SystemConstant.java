package com.taobao.taokeeper.common.constant;


import common.toolkit.java.constant.SymbolConstant;
import common.toolkit.java.util.StringUtil;

/**
 * Description: System constant,有些变量在Spring初始化的时候会注入。
 * 
 * @author yinshi.nc
 * @Date 2011-10-26
 */
public class SystemConstant {

	public final static String ZOOKEEPER_MONITOR_PATH = "/YINSHI.MONITOR.ALIVE.CHECK";

	public final static String AUTHENTICATION_TYPE = "digest";

	public final static String AUTHENTICATION_KEY = "yinshi.nc:taobao";

	/** delay of check zookeeper watcher notify */
	public final static int DELAY_SECS_OF_TWO_SERVER_ALIVE_CHECK_ZOOKEEPER = 5; // 两个ip检测的间隔
																				// s
	public final static int DELAY_MINS_OF_TWO_CYCLE_ALIVE_CHECK_ZOOKEEPER = 1; // 两次存活性检查间隔
																				// mins
	public final static int MINS_RATE_OF_CHECK_ZOOKEEPER = 1;

	public final static int MINS_RATE_OF_COLLECT_ZOOKEEPER = 1;
	public final static int MINS_RATE_OF_COLLECT_HOST_PERFORMANCE = 2;
	public final static int MINS_RATE_OF_DUMP_ZOOKEEPER_CLUSTER = 1;

	public final static String COMMAND_CONS = "echo cons | nc {0} {1}";
	public final static String COMMAND_STAT = "echo stat | nc {0} {1}";
	public final static String COMMAND_WCHS = "echo wchs | nc {0} {1}";
	public final static String COMMAND_WCHC = "echo wchc | nc {0} {1}";

	public static String userNameOfSSH = "nobody";
	public static String passwordOfSSH = "look";

	/** ZooKeeper监控信息存储设置客户端统计信息文件存放目录 */
	public static String dataStoreBasePath = "/home/yinshi.nc/zookeeper-monitor";
	
	public static String dataStoreCategoryPath_clientStat = "/ZooKeeperClientThroughputStat/";
	public static String PREFIX_OF_ZOOKEEPER_CLIENT_STAT_FILE_NAME = "zookeeper-client-";
	
	
	
	

	public static String configOfMsgCenter = "";

	public static String serverOfMsgCenter = "";
	public static String sourceIdOfMsgCenter = "";
	public static String templateIdOfMsgCenter = "";
	public static String messageTypeIdOfMsgCenter = "";

	public void setDataStoreBasePath( String dataStoreBasePath ) {
		SystemConstant.dataStoreBasePath = StringUtil.defaultIfBlank( dataStoreBasePath, SystemConstant.dataStoreBasePath );
	}

	public void setUserNameOfSSH( String userNameOfSSH ) {
		try {
			SystemConstant.userNameOfSSH = StringUtil.trimToEmpty( userNameOfSSH );
		} catch ( Exception e ) {
			throw new RuntimeException( "SSH用户名解析错误: " + e.getMessage(), e );
		}
	}

	public void setPasswordOfSSH( String passwordOfSSH ) {
		try {
			SystemConstant.passwordOfSSH = StringUtil.trimToEmpty( passwordOfSSH );
		} catch ( Exception e ) {
			throw new RuntimeException( "SSH密码解析错误: " + e.getMessage(), e );
		}
	}

	public void setConfigOfMsgCenter( String configOfMsgCenter ) {
		try {
			if ( StringUtil.isBlank( configOfMsgCenter ) )
				throw new Exception( "configOfMsgCenter不能为空" );

			// 对配置进行解析
			String[] configOfMsgCenterArray = configOfMsgCenter.split( SymbolConstant.SLASH );
			if ( 4 != configOfMsgCenterArray.length )
				throw new Exception( "configOfMsgCenter格式不合法" );

			SystemConstant.serverOfMsgCenter = StringUtil.defaultIfBlank( configOfMsgCenterArray[0], SystemConstant.serverOfMsgCenter );
			SystemConstant.sourceIdOfMsgCenter = StringUtil.defaultIfBlank( configOfMsgCenterArray[1], SystemConstant.sourceIdOfMsgCenter );
			SystemConstant.templateIdOfMsgCenter = StringUtil.defaultIfBlank( configOfMsgCenterArray[2], SystemConstant.templateIdOfMsgCenter );
			SystemConstant.messageTypeIdOfMsgCenter = StringUtil.defaultIfBlank( configOfMsgCenterArray[3], SystemConstant.messageTypeIdOfMsgCenter );
		} catch ( Exception e ) {
			throw new RuntimeException( "configOfMsgCenter( " + configOfMsgCenter
					+ " )解析错误，请配置这样一个字符串：1.2.3.4:9999,1.2.3.5:9999/a*b/3545656/67657" + e.getMessage(), e );
		}
	}

}
