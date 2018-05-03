package com.taobao.taokeeper.monitor;

import com.taobao.taokeeper.common.GlobalInstance;
import com.taobao.taokeeper.common.SystemInfo;
import com.taobao.taokeeper.common.constant.SystemConstant;
import com.taobao.taokeeper.dao.SettingsDAO;
import com.taobao.taokeeper.model.TaoKeeperSettings;
import com.taobao.taokeeper.model.type.Message;
import com.taobao.taokeeper.monitor.core.Initialization;
import com.taobao.taokeeper.monitor.core.ThreadPoolManager;
import com.taobao.taokeeper.reporter.alarm.TbMessageSender;
import common.toolkit.exception.DaoException;
import common.toolkit.util.ObjectUtil;
import common.toolkit.util.StringUtil;
import common.toolkit.util.db.DbcpUtil;
import common.toolkit.util.number.IntegerUtil;
import common.toolkit.util.system.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.Properties;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.taobao.taokeeper.monitor",
        "com.taobao.taokeeper.dao.impl",
        "com.taobao.taokeeper.monitor.web"
})
public class TaokeeperMonitorApplication {
    private static final Logger LOG = LoggerFactory.getLogger( TaokeeperMonitorApplication.class );

    public static void main(String[] args) {

        initSystem();

        SpringApplication.run(TaokeeperMonitorApplication.class, args);
    }


    /**
     * 从数据库加载并初始化系统配置
     */
    static void initSystem() {
        LOG.info( "=================================Start to init system===========================" );
        Properties properties = null;
        try {
            properties = SystemUtil.loadProperty();
            if ( ObjectUtil.isBlank( properties ) )
                throw new Exception( "Please defined,such as -DconfigFilePath=\"W:\\TaoKeeper\\taokeeper\\config\\config-test.properties\"" );
        } catch ( Exception e ) {
            LOG.error( e.getMessage() );
            throw new RuntimeException( e.getMessage(), e.getCause() );
        }

        SystemInfo.envName = StringUtil.defaultIfBlank( properties.getProperty( "systemInfo.envName" ), "TaoKeeper-Deploy" );

        DbcpUtil.driverClassName = StringUtil.defaultIfBlank( properties.getProperty( "dbcp.driverClassName" ), "com.mysql.jdbc.Driver" );
        DbcpUtil.dbJDBCUrl = StringUtil.defaultIfBlank( properties.getProperty( "dbcp.dbJDBCUrl" ), "jdbc:mysql://127.0.0.1:3306/taokeeper" );
        DbcpUtil.characterEncoding = StringUtil.defaultIfBlank( properties.getProperty( "dbcp.characterEncoding" ), "UTF-8" );
        DbcpUtil.username = StringUtil.trimToEmpty( properties.getProperty( "dbcp.username" ) );
        DbcpUtil.password = StringUtil.trimToEmpty( properties.getProperty( "dbcp.password" ) );
        DbcpUtil.maxActive = IntegerUtil.defaultIfError( properties.getProperty( "dbcp.maxActive" ), 30 );
        DbcpUtil.maxIdle = IntegerUtil.defaultIfError( properties.getProperty( "dbcp.maxIdle" ), 10 );
        DbcpUtil.maxWait = IntegerUtil.defaultIfError( properties.getProperty( "dbcp.maxWait" ), 10000 );

        SystemConstant.dataStoreBasePath = StringUtil.defaultIfBlank( properties.getProperty( "SystemConstent.dataStoreBasePath" ),
                "/home/yinshi.nc/taokeeper-monitor/" );
        SystemConstant.userNameOfSSH = StringUtil.defaultIfBlank( properties.getProperty( "SystemConstant.userNameOfSSH" ), "admin" );
        SystemConstant.passwordOfSSH = StringUtil.defaultIfBlank( properties.getProperty( "SystemConstant.passwordOfSSH" ), "123456" );
        SystemConstant.portOfSSH = IntegerUtil.defaultIfError( properties.getProperty( "SystemConstant.portOfSSH" ), 22 );

        SystemConstant.IP_OF_MESSAGE_SEND = StringUtil.trimToEmpty( properties.getProperty( "SystemConstant.IP_OF_MESSAGE_SEND" ) );


        LOG.info( "=================================Finish init system===========================" );
        ThreadPoolManager.addJobToMessageSendExecutor( new TbMessageSender( new Message( "银时", "TaoKeeper启动", "TaoKeeper启动",
                Message.MessageType.WANGWANG ) ) );
    }

    @Component
    public static class SettingsInitializer {

        @Autowired
        private SettingsDAO taoKeeperSettingsDAO;

        @EventListener(ApplicationReadyEvent.class)
        public void loadSettings() {
            TaoKeeperSettings taoKeeperSettings = null;
            try {
                taoKeeperSettings = taoKeeperSettingsDAO.getTaoKeeperSettingsBySettingsId(1);
            } catch (DaoException e) {
                LOG.error(e.getMessage(),e);
            }
            if (null != taoKeeperSettings)
                GlobalInstance.taoKeeperSettings = taoKeeperSettings;
        }
    }
}