package com.taobao.taokeeper.monitor.core.task.runable;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.dao.ZooKeeperClusterDAO;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import common.toolkit.java.exception.DaoException;

/**
 * Description: Dump zooKeeper cluster config info from database.
 * @author nileader / nileader@gmail.com
 * @Date Feb 16, 2012
 */
public class ZKClusterConfigDumper implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger( ZKClusterConfigDumper.class );

	@Override
	public void run() {
		try {
			dumpZooKeeperClusterMapToMemory();
		} catch ( DaoException daoException ) {
			LOG.error( "Error when dump zookeeper cluster config info to memeory: " + daoException.getMessage() );
			daoException.printStackTrace();
		} catch ( Throwable e ) {
			LOG.error( "Error when dump zookeeper cluster config info to memeory: " + e.getMessage() );
			e.printStackTrace();
		}
	}

	/**
	 * Dump zooKeeper cluster config info from database
	 */
	private boolean dumpZooKeeperClusterMapToMemory() throws DaoException {
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		ZooKeeperClusterDAO zooKeeperClusterDAO = ( ZooKeeperClusterDAO ) wac.getBean( "zooKeeperClusterDAO" );
		List< ZooKeeperCluster > zookeeperClusterSet = zooKeeperClusterDAO.getAllDetailZooKeeperCluster();
		if ( null == zookeeperClusterSet || zookeeperClusterSet.isEmpty() ) {
			LOG.warn( "No zookeeper cluster" );
		} else {
			// First clean up catch.
			GlobalInstance.clearZooKeeperCluster();
			
			for ( ZooKeeperCluster zooKeeperCluster : zookeeperClusterSet ) { 
				GlobalInstance.putZooKeeperCluster( zooKeeperCluster.getClusterId(), zooKeeperCluster );
			}
			LOG.info( "Finsh dump all cluster info from db: " + GlobalInstance.getAllZooKeeperCluster() );
		}
		return true;
	}

}
