Notice:The file is encoded by UTF-8

HomePage: http://jm.taobao.org/2012/01/12/zookeeper%E7%9B%91%E6%8E%A7/
CopyRight by Taobao.com
Any question to: nileader@qq.com

## Quick Start
1. Initialize the database using [taokeeper-build/etc/taokeeper.sql](file:///Users/yinshi.nc/Documents/Personal/Projects/taokeeper/taokeeper-build/etc/taokeeper.sql)
2. Create a configuration file [config.properties](file:///Users/yinshi.nc/Documents/Personal/Projects/taokeeper/taokeeper-build/etc/config.properties) based on the template file [taokeeper-build/etc/config.properties](file:///Users/yinshi.nc/Documents/Personal/Projects/taokeeper/taokeeper-build/etc/config.properties), place it in a directory such as `~/workspace`, and then configure the JVM startup parameter `-DconfigFilePath="/Users/yinshi.nc/Workspace/config.properties"`
3. Implements com.taobao.taokeeper.reporter.alarm.MessageSender to send message.
4. Exec taokeeper-build/build.cmd to generate taokeeper-monitor.war


任何建议与问题，请到 http://jm-blog.aliapp.com/?p=1450 进行反馈。
         
         
         
         
         
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

        