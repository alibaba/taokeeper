package common.toolkit.util.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.toolkit.constant.SymbolConstant;
import common.toolkit.entity.db.DBConnectionResource;
import common.toolkit.util.StringUtil;

/**
 * 数据库连接池管理,需要注意调用方法后是否需要释放相应资源。
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class DbcpUtil {

	private static Log LOG = LogFactory.getLog(DbcpUtil.class);
	
	private static BasicDataSource dataSource = null;

	
	public static String driverClassName    = "com.mysql.jdbc.Driver";
	public static String dbJDBCUrl             = "jdbc:mysql://localhost:3306/yinshi-test";
	public static String characterEncoding ="UTF-8";
	public static String username			     ="root";
	public static String password			  ="123456";
	public static int      maxActive			  = 30;
	public static int 	  maxIdle				  = 10;
	public static int 	  maxWait               = 10000;
	
	public DbcpUtil() {
	}
	
	public DbcpUtil( String driverClassName, String dbJDBCUrl, String characterEncoding, String username, String password, int maxActive, int maxIdle,int maxWait ) {
		DbcpUtil.driverClassName		= driverClassName;
		DbcpUtil.dbJDBCUrl           	= dbJDBCUrl;
		DbcpUtil.characterEncoding 	= characterEncoding;
		DbcpUtil.username			  	= username;
		DbcpUtil.password				= password;
		DbcpUtil.maxActive				= maxActive;
		DbcpUtil.maxIdle				= maxIdle;
		DbcpUtil.maxWait				= maxWait;
	}
	
	

	/**
	 * 初始化数据库连接
	 */
	private static void init() throws Exception {

		/** 如果之前有连接池塘，关闭数据源  */
		shutdownDataSource();
		try {
			Properties p = new Properties();
			p.setProperty( "driverClassName", driverClassName );
			if( StringUtil.containsIgnoreCase( dbJDBCUrl, SymbolConstant.QUESTION_SIGN ) ){
				p.setProperty( "url", dbJDBCUrl + SymbolConstant.AND_SIGN + "characterEncoding=" + characterEncoding );
			}else{
				p.setProperty( "url", dbJDBCUrl + SymbolConstant.QUESTION_SIGN + "characterEncoding=" + characterEncoding );
			}
			p.setProperty( "username", username );
			p.setProperty( "password", password );
			p.setProperty( "maxActive", String.valueOf( maxActive ) );
			p.setProperty( "maxIdle", String.valueOf( maxIdle ) );
			p.setProperty( "maxWait", String.valueOf( maxWait ) );
			p.setProperty( "removeAbandoned", "false" );
			p.setProperty( "removeAbandonedTimeout", "120" );
			p.setProperty( "testOnBorrow", "true" );
			p.setProperty( "logAbandoned", "true" );
			LOG.warn( "Start init datasource[driverName:" + driverClassName + ", url: " + p.getProperty( "url" ) + ", username: [" + username + "], password: [" + password + "]" );
			DbcpUtil.dataSource = ( BasicDataSource ) BasicDataSourceFactory.createDataSource( p );
			LOG.warn( "完成数据源创建，是否链接：" + !DbcpUtil.dataSource.isClosed() );
		} catch ( Exception e ) {
			throw new Exception( "创建数据源失败: " + e.getMessage(), e.getCause() );
		}
	}

	/**
	 * 关闭数据源
	 */
	public static void shutdownDataSource() {
		if ( null != DbcpUtil.dataSource ) {
			try {
				DbcpUtil.dataSource.close();
			} catch ( Exception e ) {
				// ignore
			}
			DbcpUtil.dataSource = null;
		}
	}

	/**
	 * 从连接池中获取数据库连接
	 */
	private static synchronized Connection getConnection() throws Exception {

		if ( null == DbcpUtil.dataSource ) {
			init();
		}
		Connection conn = null;
		if ( null != DbcpUtil.dataSource ) {
			try {
				conn = dataSource.getConnection();
			} catch ( Throwable e ) {
				throw new Exception( "Can't create conncetion, please make sure if database is available, " + e.getMessage(), e.getCause() );
			}
		}
		return conn;
	}

	/**
	 * 关闭结果集
	 */
	public static void closeResultSet( ResultSet resultSet ) {
		if ( resultSet != null )
			try {
				resultSet.close();
			} catch ( SQLException e ) {
			}// IGNORE}
	}

	/**
	 * 关闭结果集和Statement对象
	 */
	public static void closeResultSetAndStatement( ResultSet resultSet, Statement statement ) {
		if ( resultSet != null )
			try {
				resultSet.close();
			} catch ( SQLException e ) {
			}// IGNORE}
		if ( null != statement )
			try {
				statement.close();
			} catch ( SQLException e ) {
			}// IGNORE
	}

	/**
	 * 关闭Statement对象
	 */
	public static void closeStatement( Statement statement ) {
		if ( null != statement )
			try {
				statement.close();
			} catch ( SQLException e ) {
			}// IGNORE
	}

	/**
	 * 归还连接
	 */
	public static void returnBackConnectionToPool( Connection conn ) {
		if ( conn != null )
			try {
				conn.close();
			} catch ( SQLException e ) {
			}// IGNORE
	}

	/**
	 * 执行查询SQL, 注意，执行完这个方法必须执行： <br>
	 * 1. DataSourceManager.closeResultSetAndStatement( resultSet, stmt ); <br>
	 * 2. DataSourceManager.returnBackConnectionToPool( conn );
	 * @param selectSql
	 *            查询SQL语句
	 */
	public static DBConnectionResource executeQuery( String querySql ) throws Exception {
		try {
			Connection conn = DbcpUtil.getConnection();
			if ( null == conn )
				throw new Exception( "No available connection" );
			Statement stmt = conn.createStatement();
			return new DBConnectionResource( conn, stmt, stmt.executeQuery( querySql ) );
		} catch ( Exception e ) {
			throw new Exception( "执行数据库查询[" + querySql + "]出错: " + e.getMessage(), e.getCause() );
		}
	}

	/**
	 * 执行插入SQL<br>
	 * 此方法自己会释放资源，不需要调用方释放。
	 */
	public static int executeInsert( String insertSql ) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DbcpUtil.getConnection();
			if ( null == conn )
				throw new Exception( "No available connection" );
			stmt = conn.createStatement();
			return stmt.executeUpdate( insertSql );
		} catch ( Exception e ) {
			throw new Exception( "Error when execute insert [" + insertSql + "],error: " + e.getMessage() , e.getCause() );
		} finally {
			DbcpUtil.closeStatement( stmt );
			DbcpUtil.returnBackConnectionToPool( conn );
		}
	}

	/**
	 * 执行插入SQL,并获取最后一次插入主键值
	 * 此方法自己会释放资源，不需要调用方释放。
	 */
	public static int executeInsertAndReturnGeneratedKeys( String insertSql ) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DbcpUtil.getConnection();
			if ( null == conn )
				throw new Exception( "No available connection" );
			stmt = conn.prepareStatement( insertSql, Statement.RETURN_GENERATED_KEYS );
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			if ( null != rs && rs.next() ) {
				return rs.getInt( 1 );
			}
			return -1;
		} catch ( Exception e ) {
			throw new Exception( "执行数据库插入[" + insertSql + "]出错: " + e.getMessage(), e.getCause() );
		} finally {
			DbcpUtil.closeResultSetAndStatement( rs, stmt );
			DbcpUtil.returnBackConnectionToPool( conn );
		}
	}

	/**
	 * 更新数据库 update
	 * 此方法自己会释放资源，不需要调用方释放。
	 */
	public static int executeUpdate( String updateSql ) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DbcpUtil.getConnection();
			if ( null == conn )
				throw new Exception( "No available connection" );
			stmt = conn.createStatement();
			return stmt.executeUpdate( updateSql );
		} catch ( Exception e ) {
			throw new Exception( "执行数据库更新[" + updateSql + "]出错: " + e.getMessage(), e.getCause() );
		} finally {
			DbcpUtil.closeStatement( stmt );
			DbcpUtil.returnBackConnectionToPool( conn );
		}
	}

	/**
	 * 删除
	 * 此方法自己会释放资源，不需要调用方释放。
	 */
	public static int executeDelete( String deleteSql ) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DbcpUtil.getConnection();
			if ( null == conn )
				throw new Exception( "No available connection" );
			stmt = conn.createStatement();
			return stmt.executeUpdate( deleteSql );
		} catch ( Exception e ) {
			throw new Exception( "执行数据库删除[" + deleteSql + "]出错: " + e.getMessage(), e.getCause() );
		} finally {
			DbcpUtil.closeStatement( stmt );
			DbcpUtil.returnBackConnectionToPool( conn );
		}
	}

	public static void main( String[] args ) {

		DBConnectionResource myResultSet = null;
		ResultSet resultSet = null;
		String querySql = "select * from zookeeper_cluster";
		try {
			myResultSet = DbcpUtil.executeQuery( querySql );
			resultSet = myResultSet.resultSet;
			System.out.println( "Results:" );
			int numcols = resultSet.getMetaData().getColumnCount();
			while ( resultSet.next() ) {
				for ( int i = 1; i <= numcols; i++ ) {
					System.out.print( "\t" + resultSet.getString( i ) + "\t" );
				}
				System.out.println( "" );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}finally{
			if( null != myResultSet )
				DbcpUtil.closeResultSetAndStatement( resultSet, myResultSet.statement );
			DbcpUtil.returnBackConnectionToPool( myResultSet.connection );
		}
	}
	
	
	

	public void setDataSource( BasicDataSource dataSource ) {
		DbcpUtil.dataSource = dataSource;
	}

	public void setDriverClassName( String driverClassName ) {
		DbcpUtil.driverClassName = driverClassName;
	}

	public void setDbJDBCUrl( String dbJDBCUrl ) {
		DbcpUtil.dbJDBCUrl = dbJDBCUrl;
	}

	public void setUsername( String username ) {
		DbcpUtil.username = username;
	}

	public void setPassword( String password ) {
		DbcpUtil.password = password;
	}

	public void setMaxActive( int maxActive ) {
		DbcpUtil.maxActive = maxActive;
	}

	public void setMaxIdle( int maxIdle ) {
		DbcpUtil.maxIdle = maxIdle;
	}

	public void setMaxWait( int maxWait ) {
		DbcpUtil.maxWait = maxWait;
	}
	
	public void setCharacterEncoding( String characterEncoding ) {
		DbcpUtil.characterEncoding = characterEncoding;
	}

}
