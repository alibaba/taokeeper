package com.taobao.taokeeper.research.test.performance.session;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.ThreadUtil;

/**
 * Session Creator
 * 
 * @author yinshi.nc
 * @Date 2011-11-10
 */
public class SessionCreator implements Watcher {

	private String serverList;
	private int sessionTimeout;

	public SessionCreator( String serverList, int sessionTimeout ) throws IOException {
		this.serverList = serverList;
		this.sessionTimeout = sessionTimeout;
	}

	private boolean createSessionAndClose() {
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper( this.serverList, this.sessionTimeout, this );
			while( null != zk && States.CONNECTING == zk.getState()  ){
				try {
					Thread.sleep( 20 );
				} catch ( InterruptedException e ) {}
			}
			return true;
		} catch ( Throwable e ) {
			return false;
		}finally{
			closeSession( zk );
		}
	}
	
	
	private boolean closeSession( ZooKeeper zk ){
		if ( null != zk ) {
			try {
				zk.close();
			} catch ( Throwable e ) {}
		}
		return true;
	}
	

	public void start() {
		ThreadUtil.startThread( new Runnable() {
			@Override
			public void run() {
				while ( true ) {
					if( createSessionAndClose() ){
						StatisticsUtil.totalTransactions.incrementAndGet();
					}else{
						SessionPressTest.failTimes.incrementAndGet();
					}
				}
			}
		} );

	}

	@Override
	public void process( WatchedEvent event ) {}

}
