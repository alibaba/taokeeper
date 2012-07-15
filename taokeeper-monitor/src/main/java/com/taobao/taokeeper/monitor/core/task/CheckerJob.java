package com.taobao.taokeeper.monitor.core.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.toolkit.java.entity.DateFormat;
import common.toolkit.java.util.DateUtil;

/**
 * Description: Check if alive one node(ip).
 * 
 * @author 银时 yinshi.nc@taobao.com
 * @Date 2011-10-28
 */
public class CheckerJob implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger( CheckerJob.class );
	private static final Logger LOG_checkData = LoggerFactory.getLogger( "checkData" );

	private Map< String, ZkClient > zkClientList = new HashMap< String, ZkClient >();

	private String[] paths = { "/jingwei-v2/tasks/IC-DATA-DB2/locks", "/jingwei-v2/tasks/IC-DATA-DB3/locks", "/jingwei-v2/tasks/IC-DATA-DB4/locks",
			"/jingwei-v2/tasks/IC-DATA-DB5/locks", "/jingwei-v2/tasks/IC-DATA-DB6/locks", "/jingwei-v2/tasks/IC-DATA-DB7/locks", };

	@Override
	public void run() {

		String[] serlist = { "172.24.113.124", "172.24.113.125", "172.24.113.126" };
		// String[] serlist={ "10.232.102.188:2181", "10.232.102.189:2181" };
		try {

			for ( String server : serlist ) {
				ZkClient zkClient = new ZkClient( server, 50000 );
				zkClientList.put( server, zkClient );
			}
		} catch ( Throwable e1 ) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while ( true ) {
			try {

				for ( String path : paths ) {

					LOG_checkData.warn( "Path: " + path );
					// System.err.println( "Path: " + path );
					for ( String server : zkClientList.keySet() ) {
						StringBuffer sb = new StringBuffer( DateUtil.getNowTime( DateFormat.DateTime ) );
						sb.append( "  [server: " + server ).append( "] ,data: " );
						List< String > lsit = new ArrayList< String >();
						try {
							lsit = zkClientList.get( server ).getChildren( path );
						} catch ( Throwable e ) {
							LOG.error( e.getMessage(), e );
						}
						for ( String str : lsit ) {
							sb.append( str );
						}
						LOG_checkData.warn( sb.toString() );
						// System.err.println( sb.toString() );
					}
					LOG.warn( "-------------------" );
					// System.err.println("------------------");
					Thread.sleep( 1 * 1000 );
				}
				Thread.sleep( 10 * 1000 );
			} catch ( Throwable e ) {
				LOG.error( e.getMessage(), e );
			}
		}
	}

	public static void main( String[] args ) {
		Thread thread = new Thread( new CheckerJob() );
		thread.start();
	}
}
