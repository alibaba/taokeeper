Notice:The file is encoded by UTF-8

HomePage: http://jm.taobao.org/2012/01/12/zookeeper%E7%9B%91%E6%8E%A7/
CopyRight by Taobao.com
Any question to: nileader@qq.com
   
   
1. Use To manage projects dependence using maven
2. Database Initialization: taokeeper-build/sql/taokeeper.sql
3. Implements com.taobao.taokeeper.reporter.alarm.MessageSender to send message.
4. Exec taokeeper-build/build.cmd to generate taokeeper-monitor.war


How to deploy(See more,please to http://jm.taobao.org/2012/01/12/zookeeper%E7%9B%91%E6%8E%A7/ )

1. Download taokeeper.sql( http://pan.baidu.com/share/link?shareid=515952&uk=2064399439 ),init mysql.

2. Download taokeeper-monitor.tar.gz ( http://pan.baidu.com/share/link?shareid=515943&uk=2064399439 ), 
    tar -zxvf taokeeper-monitor.tar.gz  to webapps of tomcat, 
    make sure the path is %TOMCAT_HOME%\webapps\taokeeper-monitor\WEB-INF

3. Download taokeeper-monitor-config.properties http://pan.baidu.com/share/link?shareid=515942&uk=2064399439 ), 
    store it such as /home/admin/taokeeper-monitor/config/taokeeper-monitor-config.properties

----------------------------------------------------------------
systemInfo.envName=TEST
#DBCP
dbcp.driverClassName=com.mysql.jdbc.Driver
dbcp.dbJDBCUrl=jdbc:mysql://1.1.1.1:3306/taokeeper
dbcp.characterEncoding=GBK
dbcp.username=xiaoming
dbcp.password=123456
dbcp.maxActive=30
dbcp.maxIdle=10
dbcp.maxWait=10000
#SystemConstant
SystemConstent.dataStoreBasePath=/home/xiaoming/taokeeper-monitor/ZookeeperStore
#SSH account of zk server
SystemConstant.userNameOfSSH=xiaoming
SystemConstant.passwordOfSSH=123456
------------------------------------------------------------------

4. Add JAVA_OPTS to tomcat catalina.bat or catalina.sh:
windows：set JAVA_OPTS=-DconfigFilePath="D:\server\tomcat-6.0.33\webapps\taokeeper-monitor-config.properties"
linux：JAVA_OPTS=-DconfigFilePath="/home/admin/taokeeper-monitor/config/taokeeper-monitor-config.properties"

5. Startup tomcat

6. Visit the page: http://127.0.0.1:8080/taokeeper-monitor
         
         
         
         
         
Alibaba OpenSource Maven Repository  
	<profiles>
		<profile>
			<id>opensource</id>
			<repositories>
				<repository>
					<id>taocodeReleases</id>
					<name>taocode nexus</name>
					<url>http://mvnrepo.code.taobao.org/nexus/content/repositories/releases/</url>
				</repository>
				<repository>
					<id>taocodeSnapshots</id>
					<name>taocode nexus</name>
					<url>http://mvnrepo.code.taobao.org/nexus/content/repositories/snapshots/</url>
				</repository>
			</repositories>
		</profile>
	</profiles>

        