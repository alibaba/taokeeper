package com.taobao.zookeeper.presstest.watcher;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.ThreadUtil;

/**
 * ��ݶ�����,�յ���ݱ��֪ͨ�������ݻ�ȡ
 * @author yinshi.nc
 * @since 2011-11-22
 */
public class Subscriber implements Watcher {

	private static Log log = LogFactory.getLog( Subscriber.class );

	ZooKeeper zk = null;
	private String path;
	private String serverList;
	private int sessionTimeout;

	/**
	 * @param path		znode��path
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public Subscriber( String serverList, int sessionTimeout, String path ){
		this.serverList = serverList;
		this.sessionTimeout = sessionTimeout;
		this.path = path;
		this.createZk();
	}
	
	private boolean createZk(){
		if( null != this.zk ){
			try {
				this.zk.close();
			} catch ( InterruptedException e ) {
				//ignore
			}
			this.zk = null;
		}
		try {
			this.zk = new ZooKeeper( this.serverList, this.sessionTimeout, this );
			return true;
		} catch ( IOException e ) {
			return false;
		}
	}
	
	public void start() {
		if( null == this.zk )
			return;
		ThreadUtil.startThread( new Runnable() {
			@Override
			public void run() {
					// ��ȡ���
					try {
						zk.exists( path, true );
					}catch(KeeperException keeperException ){
						 //�����ڣ���ôҪ��������һ���ˡ�
						keeperException.code().equals( Code.SESSIONEXPIRED );
						if( createZk() ) {
							PressWatcher.reConnectSuccessTimes.incrementAndGet();
						}else{
							PressWatcher.reConnectFailTimes.incrementAndGet();
						}
					}catch ( Exception e ) {
						PressWatcher.failTimes.incrementAndGet();
						log.error( "zk.exists: ");
						e.printStackTrace();
					}
			}
		} );
	}

	@Override
	public void process( WatchedEvent event ) {
		
		// ��ȡ���
		try {
			if( null == this.zk ){
				createZk();
			}
			zk.getData( path, true, new Stat() );
			StatisticsUtil.totalTransactions.incrementAndGet();
		}catch(KeeperException keeperException ){
			 //�����ڣ���ôҪ��������һ���ˡ�
			keeperException.code().equals( Code.SESSIONEXPIRED );
			if( createZk() ) {
				PressWatcher.reConnectSuccessTimes.incrementAndGet();
			}else{
				PressWatcher.reConnectFailTimes.incrementAndGet();
			}
		}catch ( Exception e ) {
			PressWatcher.failTimes.incrementAndGet();
			log.error( "Fail get data: " );
			e.printStackTrace();
		}
		
	}

}
