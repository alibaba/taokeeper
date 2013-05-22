package com.taobao.taokeeper.research.test.performance.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.StringUtil;

/**
 * Session established and release
 * @author <a href="mailto:nileader@gmail.com">银时</a>
 */
public class SessionPressTest implements Watcher {

	static Log log = LogFactory.getLog( SessionPressTest.class );

	static String SERVER_LIST = "10.232.19.92:2181";
	final static int SESSION_TIMEOUT = 5000;
	static int SESSIONCREATORS = 10;

	static AtomicLong failTimes = new AtomicLong();
	
	static List< SessionCreator > sessionCreatorList = new ArrayList< SessionCreator >();

	static boolean isWork = true;
	
	
	public static void main( String[] args ) throws IOException, KeeperException, InterruptedException {

		if ( args.length != 2 ) {
			throw new IllegalArgumentException( "Argument must contain serverList and sessinoNums" );
		}
		SessionPressTest.SERVER_LIST = StringUtil.defaultIfBlank( args[0], SERVER_LIST );
		SessionPressTest.SESSIONCREATORS = Integer.parseInt( StringUtil.defaultIfBlank( args[1], SESSIONCREATORS + EmptyObjectConstant.EMPTY_STRING ) );
		
		try {
			// Prepare SessionCreator
			for ( int i = 0; i < SESSIONCREATORS; i++ ) {
				sessionCreatorList.add( new SessionCreator( SERVER_LIST, SESSION_TIMEOUT ) );
			}
			StatisticsUtil.start( 20 );
			for ( SessionCreator sessionCreator : sessionCreatorList ) {
				sessionCreator.start();
			}
		} finally {
			while ( isWork ) {
				log.error( "Error times: " + SessionPressTest.failTimes );
				Thread.sleep( 10000 );
			}
			StatisticsUtil.stop();
		}
	}
	
	@Override
	public void process( WatchedEvent event ) {
		// TODO Auto-generated method stub
	}

}
