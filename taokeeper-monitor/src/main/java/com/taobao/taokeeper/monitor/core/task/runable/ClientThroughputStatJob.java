package com.taobao.taokeeper.monitor.core.task.runable;

import static com.taobao.taokeeper.common.constant.SystemConstant.COMMAND_CONS;
import static com.taobao.taokeeper.common.constant.SystemConstant.MINS_RATE_OF_COLLECT_ZOOKEEPER;
import static com.taobao.taokeeper.common.constant.SystemConstant.passwordOfSSH;
import static com.taobao.taokeeper.common.constant.SystemConstant.userNameOfSSH;
import static common.toolkit.java.constant.BaseConstant.WORD_SEPARATOR;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.EncodingConstant.GBK;
import static common.toolkit.java.constant.HtmlTagConstant.BR;
import static common.toolkit.java.constant.SymbolConstant.COLON;
import static common.toolkit.java.constant.SymbolConstant.COMMA;
import static common.toolkit.java.constant.SymbolConstant.EQUAL_SIGN;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.common.constant.SystemConstant;
import com.taobao.taokeeper.dao.ZooKeeperClusterDAO;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.monitor.core.Initialization;
import common.toolkit.java.entity.DateFormat;
import common.toolkit.java.entity.io.Connection;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.exception.SSHException;
import common.toolkit.java.util.DateUtil;
import common.toolkit.java.util.JsonUtil;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.ThreadUtil;
import common.toolkit.java.util.io.FileUtil;
import common.toolkit.java.util.io.IOUtil;
import common.toolkit.java.util.io.SSHUtil;
/**
 * Description: 这个类收集zk集群上所有客户端发送与接收的数据量。
 * 
 * @author yinshi.nc
 * @Date 2011-10-28
 */
public class ClientThroughputStatJob implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger( Initialization.class );

	/** 是否是初始状态，如果是初始状态，那么需要先从磁盘上将当天数据读入。 */
	private static boolean isInitState = true;

	@Override
	public void run() {

		
		if( !GlobalInstance.need_client_throughput_stat ){
			LOG.info( "No need to need_client_throughput_stat=" + GlobalInstance.need_client_throughput_stat );
			ThreadUtil.sleep( 1000 * 60 * MINS_RATE_OF_COLLECT_ZOOKEEPER  );
			return;
		}
		
		if( isInitState ){
			String fileName = SystemConstant.dataStoreBasePath + SystemConstant.dataStoreCategoryPath_clientStat + SystemConstant.PREFIX_OF_ZOOKEEPER_CLIENT_STAT_FILE_NAME + DateUtil.getNowTime( DateFormat.Date ) + ".stat";
			readZooKeeperClientThroughputStatFromFile( fileName );
		}
		
		while ( true ) {
			try {
				// 根据clusterId来获取一个zk集群
				WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
				ZooKeeperClusterDAO zooKeeperClusterDAO = ( ZooKeeperClusterDAO ) wac.getBean( "zooKeeperClusterDAO" );
				try {
					List< ZooKeeperCluster > zooKeeperClusterSet = null;
					Map< Integer, ZooKeeperCluster > zooKeeperClusterMap = GlobalInstance.getAllZooKeeperCluster();
					if ( null == zooKeeperClusterMap ) {
						zooKeeperClusterSet = zooKeeperClusterDAO.getAllDetailZooKeeperCluster();
					} else {
						zooKeeperClusterSet = new ArrayList< ZooKeeperCluster >();
						zooKeeperClusterSet.addAll( zooKeeperClusterMap.values() );
					}

					if ( null == zooKeeperClusterSet || zooKeeperClusterSet.isEmpty() ) {
						LOG.warn( "No zookeeper cluster" );
					} else {
						for ( ZooKeeperCluster zookeeperCluster : zooKeeperClusterSet ) { // 对每个cluster处理
							if ( null != zookeeperCluster && null != zookeeperCluster.getServerList() ) {
								sshZooKeeperAndHandleCons( zookeeperCluster );
							}// for each cluster
						}
					}
				} catch ( DaoException daoException ) {
					LOG.warn( "Error when handle data base" + daoException.getMessage() );
				} catch ( Exception e ) {
					LOG.error( "程序出错:" + e.getMessage() );
					e.printStackTrace();
				}
				// 每2分钟收集一次检测
				Thread.sleep( 1000 * 60 * MINS_RATE_OF_COLLECT_ZOOKEEPER );
			} catch ( Throwable e ) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 进行SSH连接并进行执行cons。
	 */
	private void sshZooKeeperAndHandleCons( ZooKeeperCluster zooKeeperCluster ) {

		Map< String, Connection > connectionMap = new HashMap< String, Connection >();
		String fileName = SystemConstant.dataStoreBasePath + SystemConstant.dataStoreCategoryPath_clientStat + SystemConstant.PREFIX_OF_ZOOKEEPER_CLIENT_STAT_FILE_NAME + DateUtil.getNowTime( DateFormat.Date ) + ".stat";
		for ( String server : zooKeeperCluster.getServerList() ) {
			if ( StringUtil.isBlank( server ) )
				continue;
			String[] serverArray = server.split( COLON );
			if ( 2 != serverArray.length )
				continue;
			String ip = StringUtil.trimToEmpty( serverArray[0] );
			String port = StringUtil.trimToEmpty( serverArray[1] );
			
			Map< String, Connection > connectionMapOfServer = new HashMap< String, Connection >();
			try {
				String consOutput = SSHUtil.execute( ip, SystemConstant.portOfSSH, userNameOfSSH, passwordOfSSH, StringUtil.replaceSequenced( COMMAND_CONS, ip, port ) );

				/**
				 * Example: /10.232.38.158:50097[0](queued=0,recved=1,sent=0)
				 * /10.232.36.82:38650[1](queued=0,recved=39031,sent=39033,sid=
				 * 0x1337c7074f0007b
				 * ,lop=PING,est=1322032944436,to=40000,lcxid=0xe
				 * ,lzxid=0xffffffffffffffff
				 * ,lresp=1322553178545,llat=0,minlat=0,avglat=0,maxlat=66)
				 * /10.232.36.85:56371[1](queued=0,recved=39029,sent=39031,sid=
				 * 0x1337c7074f0007c
				 * ,lop=PING,est=1322032944768,to=40000,lcxid=0xc
				 * ,lzxid=0xffffffffffffffff
				 * ,lresp=1322553184592,llat=0,minlat=0,avglat=0,maxlat=302)
				 */
				if ( StringUtil.isBlank( consOutput ) ) {
					LOG.warn( "No output execute " + COMMAND_CONS + " on ip: " + ip + ", port: " + port );
					continue;
				}

				String[] consOutputArray = consOutput.split( BR );
				if ( 0 == consOutputArray.length ) {
					LOG.warn( "No output of command " + COMMAND_CONS + " on ip: " + ip + ", port: " + port );
					return;
				}

				for ( String line : consOutputArray ) {
					if ( StringUtil.isBlank( line ) ) {
						continue;
					}

					String[] lineArray = line.split( COLON );
					if ( 2 != lineArray.length )
						continue;
					
					String clientIp = line.split( COLON )[0];
					String sessionId = StringUtil.trimToEmpty( StringUtil.findFirstByRegex( line.split( COLON )[1], "sid=(?s).*?," ) )
							.replace( "sid=", EMPTY_STRING ).replace( COMMA, EMPTY_STRING );
					String receive = StringUtil.trimToEmpty( StringUtil.findFirstByRegex( line.split( COLON )[1], "recved=(?s).*?," ) )
							.replace( "recved=", EMPTY_STRING ).replace( COMMA, EMPTY_STRING );
					String sent = StringUtil.trimToEmpty( StringUtil.findFirstByRegex( line.split( COLON )[1], "sent=(?s).*?," ) )
							.replace( "sent=", EMPTY_STRING ).replace( COMMA, EMPTY_STRING );
					connectionMap.put( sessionId, new Connection( ip, clientIp, sessionId, receive, sent ) );
					if( !StringUtil.isBlank( sessionId ) && !StringUtil.isBlank( ip ) ){
						connectionMapOfServer.put( sessionId, new Connection( ip, clientIp, sessionId, receive, sent ) );
					}

				}// 处理 cons 的内容
			} catch ( SSHException e ) {
				LOG.warn( "Error when sshZooKeeperAndHandleWchc:[ip:" + ip + ", port:" + port + " ] " + e.getMessage() );
			} catch ( Exception e ) {
				LOG.error( "程序错误: " + e.getMessage() );
				e.printStackTrace();
			}
			GlobalInstance.putZooKeeperClientConnectionMapByClusterIdAndServerIp( ip, connectionMapOfServer );
		}// for serverList
		GlobalInstance.putZooKeeperClientConnectionMapByClusterId( zooKeeperCluster.getClusterId(), connectionMap );
		// 把ZooKeeperClientThroughputStat写入文件中去。
		dumpZooKeeperClientThroughputStatIntoFile( fileName );
	}

	/**
	 * 把SessionId与IP关系，以及每个客户端的情况dump到文件中去。
	 */
	private boolean dumpZooKeeperClientThroughputStatIntoFile( String fileName ) {

		try {
			// 写入文件中按照：clusterId-sessionId-clientIp=content来进行
			Map< Integer, Map< String, Connection >> allZooKeeperClientConnectionMap = GlobalInstance.getAllZooKeeperClientConnectionMap();

			StringBuffer sb = new StringBuffer();
			for ( int clusterId : allZooKeeperClientConnectionMap.keySet() ) {

				Map< String, Connection > connectionOfCluster = allZooKeeperClientConnectionMap.get( clusterId );

				if ( null != connectionOfCluster ) {

					for ( String sessionId : connectionOfCluster.keySet() ) {
						Connection conn = connectionOfCluster.get( sessionId );
						if ( null != conn ) {
							String key = clusterId + WORD_SEPARATOR + sessionId + WORD_SEPARATOR + conn.getClientIp();
							sb.append( key ).append( "=" ).append( JsonUtil.convertVO2String( conn ) ).append( "\n" );
						}
					}// each clusterId-sessionId-clientIp
				}
			}// each cluster
			sb.append( "最后更新于: " + DateUtil.getNowTime( DateFormat.DateTime ) );

			FileUtil.write( fileName, sb.toString(), false );
			LOG.info( "成功将SessionId与ClientIp信息Dump到本地文件" );
			return true;
		} catch ( IOException e ) {
			LOG.warn( "将SessionId与ClientIp信息Dump到本地文件失败:" + e.getMessage() );
			return false;
		} catch ( Exception e ) {
			LOG.error( "程序错误:" + e.getMessage() );
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 从文件中读取SessionId与IP关系，以及每个客户端的情况
	 */
	private void readZooKeeperClientThroughputStatFromFile( String fileName ) {

		BufferedReader br = null;
		try {
			Map< Integer, Map< String, Connection >> allZooKeeperClientConnectionMapInit = new HashMap< Integer, Map< String, Connection > >();
			br = FileUtil.readFileReturnBufferedReader( fileName, GBK );
			int clusterId = 0;
			Map< String, Connection > connectionMap = new HashMap< String, Connection >();

			String line = null;
			while ( ( line = br.readLine() ) != null ) {

				String[] lineArray = StringUtil.trimToEmpty( line ).split( EQUAL_SIGN );
				if ( 2 != lineArray.length )
					continue;

				String[] preLine = StringUtil.trimToEmpty( lineArray[0] ).split( WORD_SEPARATOR );
				if ( 3 != preLine.length || StringUtil.isBlank( preLine[1], preLine[2] ) )
					continue;

				int clusterIdLine = Integer.parseInt( preLine[0] );

				// 如果clusterId和上次不一致了，并且上次不是0，那么就要换集群了。
				if ( clusterId != 0 && clusterId != clusterIdLine ) {
					allZooKeeperClientConnectionMapInit.put( clusterId, connectionMap );
					clusterId = clusterIdLine;
					connectionMap = new HashMap< String, Connection >();
				} else { // 其它就是加入conn
					clusterId = clusterIdLine;
					connectionMap.put( preLine[1], ( Connection ) JsonUtil.convertString2VO( lineArray[1], Connection.class ) );
				}
			}

			// 走完最后一行，如果connectionMap 非空，那么就要加入
			if ( null != connectionMap && !connectionMap.isEmpty() ) {
				allZooKeeperClientConnectionMapInit.put( clusterId, connectionMap );
				clusterId = 0;
				connectionMap = null;
			}

			if ( null != allZooKeeperClientConnectionMapInit ){
				GlobalInstance.zooKeeperClientConnectionMapOfCluster = allZooKeeperClientConnectionMapInit;
			}

			LOG.warn( "从文件中读取客户端连接信息成功" );
		}catch ( FileNotFoundException e ) {
			LOG.warn( "磁盘中不存在上次存储的客户端信息，无须加载。" );
		}catch ( Exception e ) {
			LOG.warn( "从文件中读取客户端连接信息失败：" + e.getMessage() );
			e.printStackTrace();
		} finally {
			IOUtil.closeReader( br );
		}

	}

}
