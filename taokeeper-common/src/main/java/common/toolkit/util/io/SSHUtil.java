package common.toolkit.util.io;

import static common.toolkit.constant.BaseConstant.WORD_SEPARATOR;
import static common.toolkit.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.constant.HtmlTagConstant.BR;
import static common.toolkit.constant.SymbolConstant.COMMA;
import static common.toolkit.constant.SymbolConstant.PERCENT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import common.toolkit.entity.HostPerformanceEntity;
import common.toolkit.entity.io.SSHResource;
import common.toolkit.exception.IllegalParamException;
import common.toolkit.exception.SSHException;
import common.toolkit.util.StringUtil;
import common.toolkit.util.number.IntegerUtil;

/**
 * Description: SSH Util
 * @author  nileader / nileader@gmail.com
 * @Date	 2012-3-28
 */
public class SSHUtil {

	private final static String USERNAME = "nobody";
	private final static String PASSWORD = "look";
	private final static int PORT        = 22;

	private final static String COMMAND_TOP = "top -b -n 1 | head -5";
	private final static String COMMAND_DF_LH = "df -lh";
	private final static String LOAD_AVERAGE_STRING = "load average: ";
	private final static String CPU_USAGE_STRING = "Cpu(s):";
	private final static String MEM_USAGE_STRING = "Mem:";
	private final static String SWAP_USAGE_STRING = "Swap:";

	/**
	 * Get HostPerformanceEntity[cpuUsage, memUsage, load] by ssh.<br>
	 * 方法返回前已经释放了所有资源，调用方不需要关心
	 * 
	 * @param ip
	 * @param userName
	 * @param password
	 * @throws Exception
	 * @since 1.0.0
	 */
	public static HostPerformanceEntity getHostPerformance( String ip, int port, String userName, String password ) throws SSHException {

		if ( StringUtil.isBlank( ip ) ) {
			try {
				throw new IllegalParamException( "Param ip is empty!" );
			} catch ( IllegalParamException e ) {
				throw new SSHException( e.getMessage(), e );
			}
		}
		port = IntegerUtil.defaultIfSmallerThan0( port, 22 );
		userName = StringUtil.defaultIfBlank( userName, USERNAME );
		password = StringUtil.defaultIfBlank( password, PASSWORD );
		Connection conn = null;
		try {
			conn = new Connection( ip, port );
			conn.connect( null, 2000, 2000 );
			boolean isAuthenticated = conn.authenticateWithPassword( userName, password );
			if ( isAuthenticated == false ) {
				throw new Exception( "SSH authentication failed with [ userName: " + userName + ", password: " + password + "]" );
			}
			return getHostPerformance( conn );
		} catch ( Exception e ) {
			throw new SSHException( "SSH 连接错误", e );
		} finally {
			if ( null != conn )
				conn.close();
		}
	}

	/**
	 * GetSystemPerformance
	 * 
	 * @param conn a connection
	 * @return double cpu usage
	 * @throws Exception
	 */
	private static HostPerformanceEntity getHostPerformance( Connection conn ) throws Exception {

		HostPerformanceEntity systemPerformanceEntity = null;
		Session session = null;
		BufferedReader read = null;
		try {
			systemPerformanceEntity = new HostPerformanceEntity();
			systemPerformanceEntity.setIp( conn.getHostname() );
			session = conn.openSession();
			session.execCommand( COMMAND_TOP );

			read = new BufferedReader( new InputStreamReader( new StreamGobbler( session.getStdout() ) ) );
			String line = "";
			int lineNum = 0;

			String totalMem = EMPTY_STRING;
			String freeMem = EMPTY_STRING;
			String buffersMem = EMPTY_STRING;
			String cachedMem = EMPTY_STRING;
			while ( ( line = read.readLine() ) != null ) {

				if ( StringUtil.isBlank( line ) )
					continue;
				lineNum += 1;

				if ( 5 < lineNum )
					return systemPerformanceEntity;

				if ( 1 == lineNum ) {
					// 第一行，通常是这样：
					// top - 19:58:52 up 416 days, 30 min, 1 user, load average:
					// 0.00, 0.00, 0.00
					int loadAverageIndex = line.indexOf( LOAD_AVERAGE_STRING );
					String loadAverages = line.substring( loadAverageIndex ).replace( LOAD_AVERAGE_STRING, EMPTY_STRING );
					String[] loadAverageArray = loadAverages.split( "," );
					if ( 3 != loadAverageArray.length )
						continue;
					systemPerformanceEntity.setLoad( StringUtil.trimToEmpty( loadAverageArray[0] ) );
				} else if ( 3 == lineNum ) {
					// 第三行通常是这样：
					// Cpu(s): 0.0% us, 0.0% sy, 0.0% ni, 100.0% id, 0.0% wa,
					// 0.0% hi, 0.0% si
					String cpuUsage = line.split( "," )[0].replace( CPU_USAGE_STRING, EMPTY_STRING ).replace( "us", EMPTY_STRING );
					systemPerformanceEntity.setCpuUsage( StringUtil.trimToEmpty( cpuUsage ) );
				} else if ( 4 == lineNum ) {
					// 第四行通常是这样：
					// Mem: 1572988k total, 1490452k used, 82536k free, 138300k
					// buffers
					String[] memArray = line.replace( MEM_USAGE_STRING, EMPTY_STRING ).split( COMMA );
					totalMem = StringUtil.trimToEmpty( memArray[0].replace( "total", EMPTY_STRING ) ).replace( "k", EMPTY_STRING );
					freeMem = StringUtil.trimToEmpty( memArray[2].replace( "free", EMPTY_STRING ) ).replace( "k", EMPTY_STRING );
					buffersMem = StringUtil.trimToEmpty( memArray[3].replace( "buffers", EMPTY_STRING ) ).replace( "k", EMPTY_STRING );
				} else if ( 5 == lineNum ) {
					// 第四行通常是这样：
					// Swap: 2096472k total, 252k used, 2096220k free, 788540k
					// cached
					String[] memArray = line.replace( SWAP_USAGE_STRING, EMPTY_STRING ).split( COMMA );
					cachedMem = StringUtil.trimToEmpty( memArray[3].replace( "cached", EMPTY_STRING ) ).replace( "k", EMPTY_STRING );

					if ( StringUtil.isBlank( totalMem, freeMem, buffersMem, cachedMem ) )
						throw new Exception( "Error when get system performance of ip: " + conn.getHostname()
								+ ", can't get totalMem, freeMem, buffersMem or cachedMem" );

					Double totalMemDouble = Double.parseDouble( totalMem );
					Double freeMemDouble = Double.parseDouble( freeMem );
					Double buffersMemDouble = Double.parseDouble( buffersMem );
					Double cachedMemDouble = Double.parseDouble( cachedMem );

					Double memoryUsage = 1 - ( ( freeMemDouble + buffersMemDouble + cachedMemDouble ) / totalMemDouble );
					systemPerformanceEntity.setMemoryUsage( memoryUsage * 100 + PERCENT );
				} else {
					continue;
				}
			}// parse the top output

			// 统计磁盘使用状况
			Map< String, String > diskUsageMap = new HashMap< String, String >();
			session = conn.openSession();
			session.execCommand( COMMAND_DF_LH );
			read = new BufferedReader( new InputStreamReader( new StreamGobbler( session.getStdout() ) ) );
			/**
			 * 内容通常是这样： Filesystem 容量 已用 可用 已用% 挂载点 /dev/xvda2 5.8G 3.2G 2.4G
			 * 57% / /dev/xvda1 99M 8.0M 86M 9% /boot none 769M 0 769M 0%
			 * /dev/shm /dev/xvda7 68G 7.1G 57G 12% /home /dev/xvda6 2.0G 36M
			 * 1.8G 2% /tmp /dev/xvda5 2.0G 199M 1.7G 11% /var
			 * */
			boolean isFirstLine = true;
			while ( ( line = read.readLine() ) != null ) {

				if ( isFirstLine ) {
					isFirstLine = false;
					continue;
				}
				if ( StringUtil.isBlank( line ) )
					continue;

				line = line.replaceAll( " {1,}", WORD_SEPARATOR );
				String[] lineArray = line.split( WORD_SEPARATOR );
				if ( 6 != lineArray.length ) {
					continue;
				}
				String diskUsage = lineArray[4];
				String mountedOn = lineArray[5];
				diskUsageMap.put( mountedOn, diskUsage );
			}
			systemPerformanceEntity.setDiskUsageMap( diskUsageMap );

		} catch ( Exception e ) {
			throw new Exception( "Error when get system performance of ip: " + conn.getHostname(), e );
		} finally {
			try {
				if ( null != read )
					read.close();
				if ( null != session )
					session.close();
			} catch ( Exception e ) {
				// ingore
			}
		}
		return systemPerformanceEntity;
	}

	/**
	 * SSH 方式登录远程主机，执行命令,方法内部会关闭所有资源，调用方无须关心。
	 * @param ip 主机ip
	 * @param username 用户名
	 * @param password 密码
	 * @param command 要执行的命令
	 */
	public static String execute( String ip, int port, String username, String password, String command ) throws SSHException {

		if ( StringUtil.isBlank( command ) )
			return EMPTY_STRING;
		port = IntegerUtil.defaultIfSmallerThan0( port, 22 );
		Connection conn = null;
		Session session = null;
		BufferedReader read = null;
		StringBuffer sb = new StringBuffer();
		try {
			if ( StringUtil.isBlank( ip ) ) {
				throw new IllegalParamException( "Param ip is empty!" );
			}
			username = StringUtil.defaultIfBlank( username, USERNAME );
			password = StringUtil.defaultIfBlank( password, PASSWORD );
			conn = new Connection( ip, port );
			conn.connect( null, 2000, 2000 );
			boolean isAuthenticated = conn.authenticateWithPassword( username, password );
			if ( isAuthenticated == false ) {
				throw new Exception( "SSH authentication failed with [ userName: " + username + ", password: " + password + "]" );
			}

			session = conn.openSession();
			session.execCommand( command );

			read = new BufferedReader( new InputStreamReader( new StreamGobbler( session.getStdout() ) ) );
			String line = "";
			while ( ( line = read.readLine() ) != null ) {
				sb.append( line ).append( BR );
			}
			return sb.toString();
		} catch ( Exception e ) {
			throw new SSHException( "SSH远程执行command: " + command + " 出现错误: " + e.getMessage(), e );
		} finally {
			if ( null != read ) {
				try {
					read.close();
				} catch ( IOException e ) {
				}
			}
			if ( null != session )
				session.close();
			if ( null != conn )
				conn.close();
		}
	}
	
	
	/**
	 * SSH 方式登录远程主机，执行命令,方法内部会关闭所有资源，调用方无须关心。
	 * @param ip
	 * @param username
	 * @param password
	 * @param command
	 * @return
	 * @throws SSHException
	 */
	public static String execute( String ip, String username, String password, String command ) throws SSHException {
		return SSHUtil.execute( ip, PORT, username, password, command );
	}
	
	
	

	/**
	 * SSH 方式登录远程主机，执行命令,方法内部会关闭所有资源,此方法不会返回任何内容。
	 * @param ip 主机ip
	 * @param username 用户名
	 * @param password 密码
	 * @param command 要执行的命令
	 */
	public static void executeNoOutPut( String ip, int port, String username, String password, String command ) throws SSHException {

		if ( StringUtil.isBlank( command ) )
			return;
		port = IntegerUtil.defaultIfSmallerThan0( port, 22 );
		Connection conn = null;
		Session session = null;
		BufferedReader read = null;
		try {
			if ( StringUtil.isBlank( ip ) ) {
				throw new IllegalParamException( "Param ip is empty!" );
			}
			username = StringUtil.defaultIfBlank( username, USERNAME );
			password = StringUtil.defaultIfBlank( password, PASSWORD );
			conn = new Connection( ip, port );
			conn.connect( null, 2000, 2000 );
			boolean isAuthenticated = conn.authenticateWithPassword( username, password );
			if ( isAuthenticated == false ) {
				throw new Exception( "SSH authentication failed with [ userName: " + username + ", password: " + password + "]" );
			}

			session = conn.openSession();
			session.execCommand( command );
			Thread.sleep( 10000 );

		} catch ( Exception e ) {
			throw new SSHException( "SSH远程执行command: " + command + " 出现错误: " + e.getMessage(), e );
		} finally {
			if ( null != read ) {
				try {
					read.close();
				} catch ( IOException e ) {
				}
			}
			if ( null != session )
				session.close();
			if ( null != conn )
				conn.close();
		}
	}
	
	
	
	/**
	 * SSH 方式登录远程主机，执行命令,方法内部会关闭所有资源,此方法不会返回任何内容。
	 * @param ip 主机ip
	 * @param username 用户名
	 * @param password 密码
	 * @param command 要执行的命令
	 */
	public static void executeNoOutPut( String ip, String username, String password, String command ) throws SSHException {
		SSHUtil.executeNoOutPut( ip, PORT, username, password, command );
	}
	
	

	/**
	 * SSH
	 * 方式登录远程主机，执行命令,方法内部没有关闭资源，调用方需要调用SSHResource.closeAllResource()来关闭所有资源。
	 * @param ip 主机ip
	 * @param username 用户名
	 * @param password 密码
	 * @param command 要执行的命令
	 * @return SSHResource 包含Connection, Session, BufferedReader
	 */
	public static SSHResource executeWithoutHandleBufferedReader( String ip, int port, String username, String password, String command ) throws SSHException {

		if ( StringUtil.isBlank( command ) )
			return null;
		port = IntegerUtil.defaultIfSmallerThan0( port, 22 );
		SSHResource sshResource = new SSHResource();

		try {
			if ( StringUtil.isBlank( ip ) ) {
				throw new IllegalParamException( "Param ip is empty!" );
			}
			username = StringUtil.defaultIfBlank( username, USERNAME );
			password = StringUtil.defaultIfBlank( password, PASSWORD );
			sshResource.conn = new Connection( ip, port );
			sshResource.conn.connect( null, 2000, 2000 );
			boolean isAuthenticated = sshResource.conn.authenticateWithPassword( username, password );
			if ( isAuthenticated == false ) {
				throw new Exception( "SSH authentication failed with [ userName: " + username + ", password: " + password + "]" );
			}

			sshResource.session = sshResource.conn.openSession();
			sshResource.session.execCommand( command );
			sshResource.setReader( new BufferedReader( new InputStreamReader( new StreamGobbler( sshResource.session.getStdout() ) ) ) );

			return sshResource;
		} catch ( Exception e ) {
			throw new SSHException( "SSH远程执行command: " + command + " 出现错误: " + e.getMessage(), e );
		}
	}
	
	
	/**
	 * SSH
	 * 方式登录远程主机，执行命令,方法内部没有关闭资源，调用方需要调用SSHResource.closeAllResource()来关闭所有资源。
	 * @param ip 主机ip
	 * @param username 用户名
	 * @param password 密码
	 * @param command 要执行的命令
	 * @return SSHResource 包含Connection, Session, BufferedReader
	 */
	public static SSHResource executeWithoutHandleBufferedReader( String ip, String username, String password, String command ) throws SSHException {
		return SSHUtil.executeWithoutHandleBufferedReader( ip, PORT, username, password, command );
	}

}
