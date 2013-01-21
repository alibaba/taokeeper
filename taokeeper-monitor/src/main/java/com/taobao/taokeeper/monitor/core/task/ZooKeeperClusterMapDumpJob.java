package com.taobao.taokeeper.monitor.core.task;
import static com.taobao.taokeeper.common.constant.SystemConstant.MINS_RATE_OF_DUMP_ZOOKEEPER_CLUSTER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.taokeeper.monitor.core.ThreadPoolManager;
import com.taobao.taokeeper.monitor.core.task.runable.ZKClusterConfigDumper;


/**
 * Description:Dump ZooKeeper cluster info to memory
 * @author  nileader / nileader@gmail.com
 * @Date	 Feb 16, 2012
 */
public class ZooKeeperClusterMapDumpJob implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger( ZooKeeperClusterMapDumpJob.class );
	
	@Override
	public void run() {
		
		while( true ){
			
			try{
				ThreadPoolManager.addJobToZKClusterDumperExecutor( new ZKClusterConfigDumper() );
				Thread.sleep( 1000 * 60 * MINS_RATE_OF_DUMP_ZOOKEEPER_CLUSTER );
			}catch( Throwable e ){
				LOG.error( "Error when dump zk cluster config info to memory: " + e.getMessage() );
				e.printStackTrace();
			}
		}
	}
	
}
