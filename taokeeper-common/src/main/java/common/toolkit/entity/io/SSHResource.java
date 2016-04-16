package common.toolkit.entity.io;

import java.io.BufferedReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import common.toolkit.util.io.IOUtil;

/**
 * SSH资源
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class SSHResource {

	public Connection conn = null;
	public Session session = null;
	public BufferedReader reader = null;

	/** 关闭SSH操作所有占用的资源 */
	public void closeAllResource() {
		IOUtil.closeReader( reader );
		if ( null != session )
			session.close();
		if ( null != conn )
			conn.close();
	}

	public void setConn( Connection conn ) {
		this.conn = conn;
	}

	public void setSession( Session session ) {
		this.session = session;
	}

	public void setReader( BufferedReader reader ) {
		this.reader = reader;
	}

}
