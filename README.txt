Notice:The file is encoded by UTF-8

HomePage:http://code.taobao.org/p/taokeeper/wiki/index/monitor/
CopyRight by Taobao.com
Any question to:yinshi.nc@taobao.com

一、代码提交须知
请认真检查所提交代码中是否包含内部信息，涉及的文件包括如下：

taokeeper-monitor
  |__src/main/resource
         |__*.properties
         |__logback.xml(LOG_HOME)
  |__src/main/webapp/WEB-INF
         |__spring-beans.xml(configOfMsgCenter,dbJDBCUrl,username,password)
         
         
二、Database Initialization
init/taokeeper.sql
         
        