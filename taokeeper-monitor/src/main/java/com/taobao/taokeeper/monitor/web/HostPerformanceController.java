package com.taobao.taokeeper.monitor.web;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.SymbolConstant.COLON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import common.toolkit.java.entity.HostPerformanceEntity;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.io.ServletUtil;

/**
 * @author yinshi.nc@taobao.com
 * @since 2011-08-10
 */
@Controller
@RequestMapping("/hostPerformance.do")
public class HostPerformanceController extends BaseController {
	
	@RequestMapping(params = "method=showHostPerformancePAGE")
	public ModelAndView showSystemPerformancePAGE(HttpServletRequest request, HttpServletResponse response, String clusterId ) {

		clusterId = StringUtil.defaultIfBlank( clusterId, 1 + EMPTY_STRING );
		
		try {
			Map<Integer, ZooKeeperCluster > zooKeeperClusterMap = GlobalInstance.getAllZooKeeperCluster();
			ZooKeeperCluster zooKeeperCluster = GlobalInstance.getZooKeeperClusterByClusterId( Integer.parseInt( clusterId) );
			Map<String, HostPerformanceEntity> hostPerformanceEntityMap = new HashMap< String, HostPerformanceEntity >();
			if( null == zooKeeperCluster ){
				zooKeeperCluster = zooKeeperClusterDAO.getZooKeeperClusterByCulsterId( Integer.parseInt( clusterId) );
			}
			if( null ==  zooKeeperCluster ){
				ServletUtil.writeToResponse( response, "目前还没有这样的ZK集群<a href='zooKeeper.do?method=zooKeeperRegisterPAGE'><font color='red'> 加入监控</font></a>" );
				return null;
			}
			
			
			//进行系统数据采集
			List<String> serverList = zooKeeperCluster.getServerList();
			if( null != serverList ){
				for( String server : serverList ){
					String ip = StringUtil.trimToEmpty( server.split( COLON )[0] );
					hostPerformanceEntityMap.put( ip, GlobalInstance.getHostPerformanceEntity( ip ) );
				}
			}
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put( "hostPerformanceEntityMap", hostPerformanceEntityMap );
			model.put( "zooKeeperClusterMap", zooKeeperClusterMap );
			model.put( "description", zooKeeperCluster.getDescription() );
			model.put("clusterId", zooKeeperCluster.getClusterId() );
			model.put( "timeOfUpdateHostPerformanceSet", GlobalInstance.timeOfUpdateHostPerformanceSet );
			return new ModelAndView("monitor/showHostPerformancePAGE", model );
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
