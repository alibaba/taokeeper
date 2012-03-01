package com.taobao.taokeeper.monitor.web;
import org.springframework.beans.factory.annotation.Autowired;

import com.taobao.taokeeper.dao.AlarmSettingsDAO;
import com.taobao.taokeeper.dao.SettingsDAO;
import com.taobao.taokeeper.dao.ZooKeeperClusterDAO;
import com.taobao.taokeeper.monitor.service.ReportService;

/**
 * Description: Base Controller
 * @author   yinshi.nc
 * @Date	 2011-11-11
 */
public class BaseController {

	@Autowired
	protected ZooKeeperClusterDAO zooKeeperClusterDAO;
	@Autowired
	protected AlarmSettingsDAO alarmSettingsDAO;
	@Autowired
	protected SettingsDAO taoKeeperSettingsDAO;
	@Autowired
	protected ReportService reportService;
	
}
