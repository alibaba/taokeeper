package com.taobao.taokeeper.research.test.performance.onlysetdata;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

import common.toolkit.java.util.StatisticsUtil;
import common.toolkit.java.util.ThreadUtil;

/**
 * ��ݷ�����
 * @author yinshi.nc
 * @Date 2011-11-10
 */
public class Publisher implements Watcher {

	protected static Log log = LogFactory.getLog( Publisher.class );

	protected ZooKeeper zk = null;
	protected String path;
	protected String serverList;
	protected int sessionTimeout;
	protected int bytes = 256;

	public Publisher(String serverList, int sessionTimeout, String path, int bytes) throws IOException {
		this.serverList = serverList;
		this.sessionTimeout = sessionTimeout;
		this.path = path;
		this.bytes = bytes;
		this.createZk();
	}
	
	protected void createZk() throws IOException{
		PressDataSet.closeZk( this.zk );
		this.zk = new ZooKeeper( this.serverList, this.sessionTimeout, this );
		
	}
	
	public void start() {
		if( null == this.zk )
			return;
		ThreadUtil.startThread( new Runnable() {
			@Override
			public void run() {
				//�������
				while ( !PressDataSet.isFinish ) {
					try {
						while( zk != null && States.CONNECTING == zk.getState() ){
							Thread.sleep( 20 );
						}
						zk.setData( path, new byte[bytes], -1 );
						StatisticsUtil.totalTransactions.incrementAndGet();
					} catch(KeeperException keeperException ){
						 //�����ڣ���ôҪ��������һ���ˡ�
						keeperException.code().equals( Code.SESSIONEXPIRED );
						try{
							createZk();
							PressDataSet.reConnectSuccessTimes.incrementAndGet();
						}catch( Exception e){
							PressDataSet.reConnectFailTimes.incrementAndGet();
							e.printStackTrace();
						}
					}catch ( Exception e ) {
						PressDataSet.failTimes.incrementAndGet();
						log.error( "fail set data: " + e.getMessage() );
						e.printStackTrace();
					}
				}
			}
		} );

	}

	@Override
	public void process( WatchedEvent event ) {}

}
