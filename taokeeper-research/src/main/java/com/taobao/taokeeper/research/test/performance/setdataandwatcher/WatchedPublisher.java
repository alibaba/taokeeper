package com.taobao.taokeeper.research.test.performance.setdataandwatcher;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper.States;

import com.taobao.taokeeper.research.test.performance.onlysetdata.Publisher;

import common.toolkit.java.util.ObjectUtil;
import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.ThreadUtil;

/**
 * 
 * @author yinshi.nc
 * @Date 2011-11-10
 */
public class WatchedPublisher extends Publisher {

	protected static Log LOG = LogFactory.getLog( WatchedPublisher.class );

	public WatchedPublisher( String serverList, int sessionTimeout, String path, int bytes ) throws IOException {
		super( serverList, sessionTimeout, path, bytes );
	}

	public void start() {
		if ( null == this.zk )
			return;
		ThreadUtil.startThread( new Runnable() {
			@Override
			public void run() {
				// �������
				while ( true ) {
					try {
						while ( zk != null && States.CONNECTING == zk.getState() ) {
							Thread.sleep( 20 );
						}
						zk.getData( path, true, null );
						zk.setData( path, new byte[bytes], -1 );
					} catch ( KeeperException keeperException ) {
						keeperException.code().equals( Code.SESSIONEXPIRED );
						try {
							createZk();
							PressDataSetAndWatcher.reConnectSuccessTimes.incrementAndGet();
						} catch ( Exception e ) {
							PressDataSetAndWatcher.reConnectFailTimes.incrementAndGet();
							e.printStackTrace();
						}
					} catch ( Exception e ) {
						PressDataSetAndWatcher.failTimes.incrementAndGet();
						log.error( "fail set data: " + e.getMessage() );
						e.printStackTrace();
					}
				}
			}
		} );
	}

	@Override
	public void process( WatchedEvent event ) {

		if ( ObjectUtil.isBlank( event ) ) {
			return;
		}
		// 事件类型
		EventType eventType = event.getType();
		// 受影响的path
		String path = event.getPath();

		if ( EventType.NodeDataChanged == eventType ) {
			try {
				while ( zk != null && States.CONNECTING == zk.getState() ) {
					Thread.sleep( 20 );
				}
				zk.getData( path, true, null );
				StatisticsUtil.totalTransactions.incrementAndGet();
			} catch ( KeeperException keeperException ) {
				keeperException.code().equals( Code.SESSIONEXPIRED );
				try {
					createZk();
					PressDataSetAndWatcher.reConnectSuccessTimes.incrementAndGet();
				} catch ( Exception e ) {
					PressDataSetAndWatcher.reConnectFailTimes.incrementAndGet();
					e.printStackTrace();
				}
			} catch ( Exception e ) {
				PressDataSetAndWatcher.failTimes.incrementAndGet();
				log.error( "fail set data: " + e.getMessage() );
				e.printStackTrace();
			}
		}
	}

}
