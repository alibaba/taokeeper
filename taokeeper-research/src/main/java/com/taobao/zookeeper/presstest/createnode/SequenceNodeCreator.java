package com.taobao.zookeeper.presstest.createnode;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.ThreadUtil;

/**
 * SequenceNode 鍒涘缓鑰�
 * 
 * @author yinshi.nc
 * @Date 2011-11-10
 */
public class SequenceNodeCreator implements Watcher {

	
	Log log = LogFactory.getLog( getClass() );
	
	private String serverList;
	private static int SESSION_TOUT = 5000;
	private String parentPath;
	private ZooKeeper zk;
	private static boolean needPrint = false;
	
	public SequenceNodeCreator( String serverList, String parentPath ) throws IOException {
		this.serverList = serverList;
		this.parentPath = parentPath;
		ThreadUtil.startThread( new Runnable() {
			@Override
			public void run() {
				while( true ){
					needPrint = true;
					try {
						Thread.sleep( 3000 );
					} catch ( InterruptedException e ) {}
				}
			}
		} );
	}

	private String createSequenceNode() {
		try {
			if ( null == zk || States.CONNECTED != zk.getState() )
				zk = buildZooKeeperClient();
			return zk.create( parentPath + "/" + "child-", "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL );
		} catch ( Exception e ) {
			SequenceNodeTest.failTimes.incrementAndGet();
			return null;
		}
	}

	private ZooKeeper buildZooKeeperClient() throws IOException, InterruptedException {
		if ( zk != null ) {
			zk.close();
		}
		return new ZooKeeper( this.serverList, SESSION_TOUT, this );
	}

	private boolean closeSession( ZooKeeper zk ) {
		if ( null != zk ) {
			try {
				zk.close();
			} catch ( InterruptedException e ) {
			}
		}
		return true;
	}

	public void start() {
		
		ThreadUtil.startThread( new Runnable() {
			@Override
			public void run() {
				try{
					while ( true ) {
						String path = EmptyObjectConstant.EMPTY_STRING;
						if ( !StringUtil.isBlank( path = createSequenceNode() ) ) {
							if( needPrint ){
								needPrint = false;
								log.info( "path: " + path );
							}
							StatisticsUtil.totalTransactions.incrementAndGet();
						}
					}
				}catch(Exception e){
					
				}finally{
					closeSession( zk );
				}
			}
		} );

	}

	@Override
	public void process( WatchedEvent event ) {
	}

}
