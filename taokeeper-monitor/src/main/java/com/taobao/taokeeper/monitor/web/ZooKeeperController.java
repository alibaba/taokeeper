package com.taobao.taokeeper.monitor.web;
import static common.toolkit.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.constant.SymbolConstant.COMMA;
import static common.toolkit.constant.SymbolConstant.SQUARE_BRACKETS_LEFT;
import static common.toolkit.constant.SymbolConstant.SQUARE_BRACKETS_RIGHT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.model.AlarmSettings;
import com.taobao.taokeeper.model.ZooKeeperCluster;
import com.taobao.taokeeper.monitor.core.ThreadPoolManager;
import com.taobao.taokeeper.monitor.core.task.ZooKeeperALiveCheckerJob;
import com.taobao.taokeeper.monitor.core.task.runable.ZKClusterConfigDumper;
import common.toolkit.exception.DaoException;
import common.toolkit.util.StringUtil;
import common.toolkit.util.collection.CollectionUtil;
import common.toolkit.util.collection.ListUtil;
import common.toolkit.util.io.ServletUtil;

/**
 * @author yinshi.nc@taobao.com
 * @since 2011-08-10
 */
@Controller
@RequestMapping("/zookeeper")
public class ZooKeeperController extends BaseController {


	private static final Logger LOG = LoggerFactory.getLogger( ZooKeeperController.class );

    @Autowired
    private ZKClusterConfigDumper zkClusterConfigDumper;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("zooKeeperCluster", new ZooKeeperCluster());
        model.addAttribute("zooKeeperClusterServerList", "");
        model.addAttribute("handleMessage", "");
        return "monitor/zookeeperRegisterPAGE";
    }

    @PostMapping("/register")
    public String handleRegister(Model model,
                                 @ModelAttribute("zooKeeperCluster") ZooKeeperCluster zooKeeperCluster
                                 ) {
        boolean isSuccess = false;
        String handleMessage = "";
        int clusterId = 0;
        if( null == zooKeeperCluster
                || StringUtil.isBlank( zooKeeperCluster.getClusterName() )
                || CollectionUtil.isBlank( zooKeeperCluster.getServerList() )
        ){
            handleMessage = "cluster name or serverlist empty.";
        }else{
            try {
                clusterId = zooKeeperClusterDAO.addZooKeeper(zooKeeperCluster);
                if( 0< clusterId ){
                    handleMessage = "Register a zookeeper cluster success";
                    isSuccess = true;
                    LOG.info( handleMessage + ": " + zooKeeperCluster );

                    //Update zk cluster config info of memory
                    ThreadPoolManager.addJobToZKClusterDumperExecutor( zkClusterConfigDumper );

                    //现在要加入一个默认的报警
                    alarmSettingsDAO.addAlarmSettings( new AlarmSettings( clusterId, "5", "60", "70", "2", "银时", "15869027928", "yinshi.nc@taobao.com", "200","1000","/home/yinshi.nc","/home/yinshi.nc","70","" ) );

                    //启动自检
                    Thread aliveCheckThread = new Thread( new Runnable() {
                        @Override
                        public void run() {
                            ZooKeeperALiveCheckerJob job = new ZooKeeperALiveCheckerJob();
                            for( String server : zooKeeperCluster.getServerList() ){
                                job.checkAliveNoAlarm( server );
                            }
                        }
                    });
                    aliveCheckThread.start();

                    handleMessage = "Success register a zookeeper cluster, and add a default alarm settings for you.";
                }else{
                    handleMessage = "Register a zookeeper cluster fail";
                }

            }catch ( DaoException e){
                handleMessage = "Register ZooKeeper cluster fail.";
                LOG.error( handleMessage, e);
            }
        }

        if( isSuccess ){
            return "redirect:/zookeeper/zookeeperSettings?clusterId=" + clusterId + "&handleMessage=" + handleMessage;
        }else{
            model.addAttribute("handleMessage", handleMessage );
            return "monitor/zookeeperRegisterPAGE";
        }

    }

    @RequestMapping("zookeeperSettings")
	public ModelAndView zooKeeperSettingsPAGE(
            HttpServletRequest request,
            HttpServletResponse response,
            String clusterId,
            String handleMessage ) {

		clusterId = StringUtil.defaultIfBlank( clusterId, 1 + EMPTY_STRING );

		try {
			Map<Integer, ZooKeeperCluster > zooKeeperClusterMap = GlobalInstance.getAllZooKeeperCluster();
			ZooKeeperCluster zooKeeperCluster = zooKeeperClusterMap.get( Integer.parseInt( clusterId ) );
			if( null == zooKeeperCluster ){
				zooKeeperCluster = zooKeeperClusterDAO.getZooKeeperClusterByCulsterId( Integer.parseInt( clusterId) );
			}

            if( null ==  zooKeeperCluster ){
                ServletUtil.writeToResponse( response, "目前还没有这样的ZK集群<a href='/zookeeper/register'><font color='red'> 加入监控</font></a>" );
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
			return new ModelAndView("monitor/zookeeperSettingsPAGE", model );
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

}
