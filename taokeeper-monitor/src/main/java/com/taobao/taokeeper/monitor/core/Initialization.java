package com.taobao.taokeeper.monitor.core;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.dao.SettingsDAO;
import com.taobao.taokeeper.model.TaoKeeperSettings;
import com.taobao.taokeeper.monitor.core.task.HostPerformanceCollectTask;
import com.taobao.taokeeper.monitor.core.task.ZooKeeperALiveCheckerJob;
import com.taobao.taokeeper.monitor.core.task.ZooKeeperClusterMapDumpJob;
import com.taobao.taokeeper.monitor.core.task.ZooKeeperStatusCollectJob;
import com.taobao.taokeeper.monitor.core.task.runable.ClientThroughputStatJob;

import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.ThreadUtil;
/**
 * Description: System Initialization
 * @author yinshi.nc
 * @Date 2011-10-27
 */
public class Initialization extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger( Initialization.class );

	public void init() {

		/** Init threadpool */
		ThreadPoolManager.init();

		initSystem();

		// Start the job of dump db info to memeory
		Thread zooKeeperClusterMapDumpJobThread = new Thread( new ZooKeeperClusterMapDumpJob() );
		zooKeeperClusterMapDumpJobThread.start();
		try {
			// 这里等待一下，因为第一次一定要dump成功，
			// TODO 这个等待逻辑要改。
			Thread.sleep( 5000 );
		} catch ( InterruptedException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ThreadUtil.startThread( new ClientThroughputStatJob() );
		
		/** 启动ZooKeeper数据修改通知检测 */
		ThreadUtil.startThread( new ZooKeeperALiveCheckerJob() );

		/** 启动ZooKeeper集群状态收集 */
		ThreadUtil.startThread( new ZooKeeperStatusCollectJob() );

		/** 收集机器CPU LOAD MEMEORY */
		ThreadUtil.startThread( new HostPerformanceCollectTask() );

		LOG.info( "所有任务顺利启动" );
	}

	/**
	 * 从数据库加载并初始化系统配置
	 */
	private void initSystem() {

		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		SettingsDAO settingsDAO = ( SettingsDAO ) wac.getBean( "taoKeeperSettingsDAO" );

		TaoKeeperSettings taoKeeperSettings = null;
		try {
			taoKeeperSettings = settingsDAO.getTaoKeeperSettingsBySettingsId( 1 );
		} catch ( DaoException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( null != taoKeeperSettings )
			GlobalInstance.taoKeeperSettings = taoKeeperSettings;
	}

}
