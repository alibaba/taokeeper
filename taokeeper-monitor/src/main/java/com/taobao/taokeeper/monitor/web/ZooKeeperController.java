package com.taobao.taokeeper.monitor.web;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.SymbolConstant.COMMA;
import static common.toolkit.java.constant.SymbolConstant.SQUARE_BRACKETS_LEFT;
import static common.toolkit.java.constant.SymbolConstant.SQUARE_BRACKETS_RIGHT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.model.AlarmSettings;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.monitor.core.ThreadPoolManager;
import com.taobao.taokeeper.monitor.core.task.ZooKeeperALiveCheckerJob;
import com.taobao.taokeeper.monitor.core.task.runable.ZKClusterConfigDumper;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.collection.CollectionUtil;
import common.toolkit.java.util.collection.ListUtil;
import common.toolkit.java.util.io.ServletUtil;

/**
 * @author yinshi.nc@taobao.com
 * @since 2011-08-10
 */
@Controller
@RequestMapping("/zooKeeper.do")
public class ZooKeeperController extends BaseController {
	
	
	private static final Logger LOG = LoggerFactory.getLogger( ZooKeeperController.class ); 
	
	
	@RequestMapping(params = "method=zooKeeperRegisterPAGE")
	public ModelAndView zooKeeperRegisterPAGE(HttpServletRequest request, HttpServletResponse response, String handleMessage ) {
		return new ModelAndView("monitor/zooKeeperRegisterPAGE", null );
	}
	
	
	@RequestMapping(params = "method=zooKeeperSettingsPAGE")
	public ModelAndView zooKeeperSettingsPAGE(HttpServletRequest request, HttpServletResponse response, String clusterId, String handleMessage ) {

		clusterId = StringUtil.defaultIfBlank( clusterId, 1 + EMPTY_STRING );
		
		try {
			Map<Integer, ZooKeeperCluster > zooKeeperClusterMap = GlobalInstance.getAllZooKeeperCluster();
			ZooKeeperCluster zooKeeperCluster = zooKeeperClusterMap.get( Integer.parseInt( clusterId ) );
			if( null == zooKeeperCluster ){
				zooKeeperCluster = zooKeeperClusterDAO.getZooKeeperClusterByCulsterId( Integer.parseInt( clusterId) );
			}
			
			if( null ==  zooKeeperCluster ){
				ServletUtil.writeToResponse( response, "目前还没有这样的ZK集群<a href='zooKeeper.do?method=zooKeeperRegisterPAGE'><font color='red'> 加入监控</font></a>" );
				return null;
			}
			
			
			//由于serverList格式问题，因为这里要特殊处理
			String zooKeeperClusterServerList = CollectionUtil.toString( zooKeeperCluster.getServerList() );
			Map<String, Object> model = new HashMap<String, Object>();
			model.put( "zooKeeperCluster", zooKeeperCluster );
			model.put( "zooKeeperClusterMap", zooKeeperClusterMap );
			model.put("clusterId", clusterId );
			model.put( "zooKeeperClusterServerList", zooKeeperClusterServerList );
			model.put( "handleMessage", handleMessage );
			return new ModelAndView("monitor/zooKeeperSettingsPAGE", model );
		} catch ( NumberFormatException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}

	/**
	 * 注意，这里更新完数据库后，还要更新缓存。
	 */
	@RequestMapping(params = "method=updateZooKeeperSettingsHandle")
	public String updateZooKeeperSettingsHandle(HttpServletRequest request, HttpServletResponse response, 
			String clusterId,
			String clusterName,
			String serverListString,
			String description ) {
		
		try {
			if( StringUtil.isBlank( clusterId ) )
				throw new Exception( "clusterId 不能为空" );
			
			ZooKeeperCluster zooKeeperCluster = new ZooKeeperCluster();
			zooKeeperCluster.setClusterId( Integer.parseInt( clusterId ) );
			zooKeeperCluster.setClusterName( clusterName );
			zooKeeperCluster.setDescription( description );
			if( !StringUtil.isBlank( serverListString ) ){
				zooKeeperCluster.setServerList( ListUtil.parseList( serverListString.replace( SQUARE_BRACKETS_LEFT, EMPTY_STRING ).replace( SQUARE_BRACKETS_RIGHT, EMPTY_STRING ), COMMA ) );
			}
			
			//进行Update
			String handleMessage = null;
			if( zooKeeperClusterDAO.updateZooKeeperSettingsByClusterId( zooKeeperCluster ) ){
				LOG.info( "完成zooKeeper集群更新：" + zooKeeperCluster );
				//Update zk cluster config info of memory
				ThreadPoolManager.addJobToZKClusterDumperExecutor( new ZKClusterConfigDumper() );
				
				handleMessage = "[Update Success], and update cache success.";
			}else{
				handleMessage = "Update Fail";
				LOG.warn( "对zooKeeper集群信息更新失败-" + zooKeeperCluster );
			}
			return "redirect:/zooKeeper.do?method=zooKeeperSettingsPAGE&clusterId=" + clusterId + "&handleMessage=" + handleMessage;
		} catch ( NumberFormatException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( DaoException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	/**
	 * 注册ZooKeeper集群, 并且自动完成缓存更新，插入一个默认报警设置
	 */
	@RequestMapping(params = "method=registerZooKeeperHandle")
	public String registerZooKeeperHandle(HttpServletRequest request, HttpServletResponse response, 
			String clusterName,
			String serverListString,
			String description ) {
		
		try {
			
			ZooKeeperCluster zooKeeperCluster = new ZooKeeperCluster();
			zooKeeperCluster.setClusterName( clusterName );
			zooKeeperCluster.setDescription( description );
			if( !StringUtil.isBlank( serverListString ) ){
				zooKeeperCluster.setServerList( ListUtil.parseList( serverListString.replace( SQUARE_BRACKETS_LEFT, EMPTY_STRING ).replace( SQUARE_BRACKETS_RIGHT, EMPTY_STRING ), COMMA ) );
			}
			
			//进行Add
			String handleMessage = null;
			
			int clusterId = zooKeeperClusterDAO.addZooKeeper( zooKeeperCluster );
			
			if( 0< clusterId ){
				LOG.warn( "完成zooKeeper集群添加：" + zooKeeperCluster );

				//Update zk cluster config info of memory
				ThreadPoolManager.addJobToZKClusterDumperExecutor( new ZKClusterConfigDumper() );
				
				//现在要加入一个默认的报警
				alarmSettingsDAO.addAlarmSettings( new AlarmSettings( clusterId, "5", "60", "70", "2", "银时", "15869027928", "yinshi.nc@taobao.com", "200","1000","/home/yinshi.nc","/home/yinshi.nc","70","" ) );
				
				//启动自检
				if( null != zooKeeperCluster.getServerList() && !zooKeeperCluster.getServerList().isEmpty() ){
					final List<String> serverList = zooKeeperCluster.getServerList();
					Thread aliveCheckThread = new Thread( new Runnable() {
						@Override
						public void run() {
							ZooKeeperALiveCheckerJob job = new ZooKeeperALiveCheckerJob();
							for( String server : serverList ){
								job.checkAliveNoAlarm( server );
							}
						}
					});
					aliveCheckThread.start();
				}
				
				handleMessage = "Register Success, and add a default alarm settings for you.";
			}else{
				handleMessage = "Register Fail";
				clusterId = 1;
			}
			return "redirect:/zooKeeper.do?method=zooKeeperSettingsPAGE&clusterId=" + clusterId + "&handleMessage=" + handleMessage;
		} catch ( NumberFormatException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( DaoException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
}
