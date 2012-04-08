package com.taobao.taokeeper.monitor.service.impl;
import org.springframework.beans.factory.annotation.Autowired;

import com.taobao.taokeeper.dao.AlarmSettingsDAO;
import com.taobao.taokeeper.dao.ReportDAO;
import com.taobao.taokeeper.dao.SettingsDAO;
import com.taobao.taokeeper.dao.ZooKeeperClusterDAO;

/**
 * 
 * Description: Base Service
 * @author  nileader / nileader@gmail.com
 * @Date	 Feb 18, 2012
 */
public class BaseService {

	@Autowired
	protected ZooKeeperClusterDAO zooKeeperClusterDAO;
	@Autowired
	protected AlarmSettingsDAO alarmSettingsDAO;
	@Autowired
	protected SettingsDAO taoKeeperSettingsDAO;
	@Autowired
	protected ReportDAO reportDAO;
}