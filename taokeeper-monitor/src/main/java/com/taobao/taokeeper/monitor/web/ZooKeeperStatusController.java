package com.taobao.taokeeper.monitor.web;
import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.model.ZooKeeperStatusV2;
import common.toolkit.java.exception.DaoException;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.io.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.SymbolConstant.COLON;

/**
 * Control of ZooKeeperStatus 
 * @author yinshi.nc@taobao.com
 * @since 2011-08-10
 */
@Controller
@RequestMapping("/zooKeeperStatus.do")
public class ZooKeeperStatusController extends BaseController {
	
	private static final Logger LOG = LoggerFactory.getLogger( ZooKeeperStatusController.class );
	
	@RequestMapping(params = "method=showZooKeeperStatusPAGE")
	public ModelAndView showZooKeeperStatusPAGE(HttpServletRequest request, HttpServletResponse response, String clusterId ) throws IOException{

		try {
			clusterId = StringUtil.defaultIfBlank( clusterId, 1 + EMPTY_STRING );
			
			ZooKeeperCluster zooKeeperCluster = GlobalInstance.getZooKeeperClusterByClusterId( Integer.parseInt( clusterId) );
			Map<Integer, ZooKeeperCluster > zooKeeperClusterMap = GlobalInstance.getAllZooKeeperCluster();
			if( null == zooKeeperCluster ){
				zooKeeperCluster = zooKeeperClusterDAO.getZooKeeperClusterByCulsterId( Integer.parseInt( clusterId) );
			}
			
			if( null ==  zooKeeperCluster ){
				ServletUtil.writeToResponse( response, "目前还没有这样的ZK集群<a href='zooKeeper.do?method=zooKeeperRegisterPAGE'><font color='red'> 加入监控</font></a>" );
				return null;
			}
			
			Map<String, ZooKeeperStatusV2> zooKeeperStatusMap = new HashMap<String, ZooKeeperStatusV2>();
			
			//进行系统数据采集
			List<String> serverList = zooKeeperCluster.getServerList();
			if( null != serverList ){
				for( String server : serverList ){
					String ip = StringUtil.trimToEmpty( server.split( COLON )[0] );
					//获取自检状态
					int statusType = GlobalInstance.getZooKeeperStatusType( ip );
					ZooKeeperStatusV2 zooKeeperStatus = GlobalInstance.getZooKeeperStatus(ip);
					if( null != zooKeeperStatus ){
						zooKeeperStatus.setStatusType( statusType );
					}else{
						zooKeeperStatus = new ZooKeeperStatusV2();
						zooKeeperStatus.setStatusType( statusType );
					}
					zooKeeperStatusMap.put( ip, zooKeeperStatus );
				}
			}
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put( "zooKeeperStatusMap", zooKeeperStatusMap );
			model.put("clusterId", zooKeeperCluster.getClusterId() );
			model.put("clusterName", zooKeeperCluster.getClusterName() );
			model.put("description", zooKeeperCluster.getDescription() );
			model.put("zooKeeperClusterMap", zooKeeperClusterMap );
			model.put( "timeOfUpdateZooKeeperStatusSet", GlobalInstance.timeOfUpdateZooKeeperStatusSet );
            //model.put("clusterRTStatsMap", ZooKeeperRTCollectJob.getRtStatus().get(zooKeeperCluster.getClusterId()));
            //model.put("clusterRTStats", ZooKeeperRTCollectJob.getClustRTStatus().get(zooKeeperCluster.getClusterId()));
			return new ModelAndView( "monitor/zooKeeperStatusPAGE", model );
			
		} catch (NumberFormatException e) {
			LOG.error( "不合法的clusterId：" + clusterId );
			ServletUtil.writeToResponse(response, "不合法的clusterId：" + clusterId );
			e.printStackTrace();
		} catch ( DaoException e ) {
			LOG.error( "Error when handle db: " + e.getMessage() );
			ServletUtil.writeToResponse(response, "Error when handle db: " + e.getMessage() );
			e.printStackTrace();
		} catch ( Exception e ) {
			LOG.error( "Server error : " + e.getMessage() );
			ServletUtil.writeToResponse(response, "Server error: " + e.getMessage() );
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
}
