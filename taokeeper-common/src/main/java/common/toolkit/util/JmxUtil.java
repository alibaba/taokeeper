package common.toolkit.util;


import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import common.toolkit.util.io.NetUtil;

/**
 * JMX工具类
 * @author 银时 yinshi.nc@taobao.com
 */
public class JmxUtil {

    private volatile String port;
    private volatile String ip;
    private volatile String jndiPath;

    private JMXConnector connector;

    private volatile MBeanServerConnection mbs;

    public JmxUtil(String ip, String port, String jndiPath) {
        this.port     = port;
        this.ip       = ip;
        this.jndiPath = jndiPath;
    }
    
    public JmxUtil(String ip, String port ) {
        this.port     = port;
        this.ip       = ip;
    }
    

    public void setPort(String JMXPort) {
    	if( NetUtil.isLegalPort( JMXPort ) ){
    		this.port = JMXPort;
    	}
    }


    public void setJMXPort(String JMXPort) {
    	if( NetUtil.isLegalPort( JMXPort ) ){
    		this.port = JMXPort;
    	}
    }


    public String getPort() {
        return port;
    }


    public String getJMXPortStr() {
        String JMXPortStr = Integer.valueOf(port).toString();
        return JMXPortStr;
    }


    public void setIp(String ip) {
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ip);
        }
        catch (UnknownHostException e) {
            throw new RuntimeException( "JMXSupport, setIP Error:" + e );
        }
        this.ip = address.getHostAddress();
    }


    public String getIp() {
        return ip;
    }


    public String getJndiPath() {
        return jndiPath;
    }


    public void setJndiPath(String jndiPath) {
        this.jndiPath = jndiPath;
    }


    public String getDescription() {
        return this.ip + ":" + this.port;
    }


    /**
     * 获取一个JMX连接
     * @throws Throwable
     */
    public MBeanServerConnection getMBeanServerConnection() throws Throwable {
        if (null == connector) {
            try {
            	JMXServiceURL address = null;
            	//这里有两种连接方式：service:jmx:rmi:///jndi/rmi://" + ip + ":" + port + "/" + jndiPath 和 ip:prot
            	if( StringUtil.isBlank( jndiPath ) ){
            		address = new JMXServiceURL( ip + ":" + port );
            	}else{
            		address = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + ip + ":" + port + "/" + jndiPath);
            	}
                connector = JMXConnectorFactory.connect(address);
                mbs = connector.getMBeanServerConnection();
            }
            catch (MalformedURLException e) {
                closeJMXConnector();
                throw new Throwable( getDescription() + ", getMBeanServerConnection, MalformedURLException", e );
            }
            catch (IOException e) {
                closeJMXConnector();
                throw new Throwable( getDescription() + ", getMBeanServerConnection, (null == connector)-> IOException", e );
            }
            catch (Throwable t) {
                closeJMXConnector();
                throw new Throwable( getDescription() + ", getMBeanServerConnection, (null == connector)-> Throwable", t );
            }
        }else {
            try {
                mbs = connector.getMBeanServerConnection();
            }
            catch (IOException e) {
                closeJMXConnector();
                throw new Exception( getDescription() + ", getMBeanServerConnection, (null != connector)-> IOException", e );
            }
            catch (Throwable t) {
                closeJMXConnector();
                throw new Throwable( getDescription() + ", getMBeanServerConnection, (null != connector)-> Throwable", t );
            }
        }
        return mbs;
    }


    /**
     * 关闭JMX连接
     * @throws Throwable 
     */
    public void closeJMXConnector() throws Throwable {
        if (null != connector) {
            try {
                connector.close();
            }
            catch (IOException e) {
            	throw new Exception( getDescription() + ", closeJMXConnector-> IOException", e );
            }
            catch (Throwable t) {
                throw new Throwable( getDescription() + ", closeJMXConnector-> Throwable", t );
            }
            finally {
                connector = null;
                mbs = null;
            }
        }
        if (null != mbs) {
            mbs = null;
        }
    }
}
