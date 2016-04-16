package common.toolkit.util.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import common.toolkit.entity.db.DBConnectionResource;

/**
 * 数据库连接池管理,需要注意调用方法后是否需要释放相应资源。
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class C3p0Util {

	private static Log LOG = LogFactory.getLog(C3p0Util.class);
	
	private static ComboPooledDataSource dataSource = null;

	
	public static String driverClassName    = "com.mysql.jdbc.Driver";
	public static String dbJDBCUrl             = "jdbc:mysql://10.232.31.154:3806/kenan";
	public static String characterEncoding ="UTF-8";
	public static String username			     ="yinshi";
	public static String password			  ="yinshi";
	public static int      maxActive			  = 30;
	public static int 	  maxIdle				  = 10;
	public static int 	  maxWait               = 10000;
	
	public C3p0Util() {
	}
	
	public C3p0Util( String driverClassName, String dbJDBCUrl, String characterEncoding, String username, String password, int maxActive, int maxIdle,int maxWait ) {
		C3p0Util.driverClassName		= driverClassName;
		C3p0Util.dbJDBCUrl           	= dbJDBCUrl;
		C3p0Util.characterEncoding 	= characterEncoding;
		C3p0Util.username			  	= username;
		C3p0Util.password				= password;
		C3p0Util.maxActive				= maxActive;
		C3p0Util.maxIdle				= maxIdle;
		C3p0Util.maxWait				= maxWait;
	}
	
	

	/**
	 * 初始化数据库连接
	 */
	private static void init() throws Exception {

		/** 如果之前有连接池塘，关闭数据源  */
		shutdownDataSource();
		try {
			
			dataSource = new ComboPooledDataSource();
			dataSource.setUser( C3p0Util.username );  
            dataSource.setPassword( C3p0Util.password );  
            dataSource.setJdbcUrl( C3p0Util.dbJDBCUrl);  
            dataSource.setDriverClass("com.mysql.jdbc.Driver");  
            // 设置初始连接池的大小！  
            dataSource.setInitialPoolSize(2);  
            // 设置连接池的最小值！   
            dataSource.setMinPoolSize(1);  
            // 设置连接池的最大值！   
            dataSource.setMaxPoolSize(10);  
            // 设置连接池中的最大Statements数量！  
            dataSource.setMaxStatements(50);  
            // 设置连接池的最大空闲时间！  
            dataSource.setMaxIdleTime(60);  
			LOG.warn( "Start init datasource[driverName:" + driverClassName + ", url: " + C3p0Util.dbJDBCUrl + ", username: [" + username + "], password: [" + password + "]" );
		} catch ( Exception e ) {
			throw new Exception( "创建数据源失败: " + e.getMessage(), e.getCause() );
		}
	}

	/**
	 * 关闭数据源
	 */
	public static void shutdownDataSource() {
		if ( null != C3p0Util.dataSource ) {
			try {
				C3p0Util.dataSource.close();
			} catch ( Exception e ) {
				// ignore
			}
			C3p0Util.dataSource = null;
		}
	}

	/**
	 * 从连接池中获取数据库连接
	 */
	private static synchronized Connection getConnection() throws Exception {

		if ( null == C3p0Util.dataSource ) {
			init();
		}
		Connection conn = null;
		if ( null != C3p0Util.dataSource ) {
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
			Connection conn = C3p0Util.getConnection();
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
			conn = C3p0Util.getConnection();
			if ( null == conn )
				throw new Exception( "No available connection" );
			stmt = conn.createStatement();
			return stmt.executeUpdate( insertSql );
		} catch ( Exception e ) {
			throw new Exception( "Error when execute insert [" + insertSql + "],error: " + e.getMessage() , e.getCause() );
		} finally {
			C3p0Util.closeStatement( stmt );
			C3p0Util.returnBackConnectionToPool( conn );
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
			conn = C3p0Util.getConnection();
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
			C3p0Util.closeResultSetAndStatement( rs, stmt );
			C3p0Util.returnBackConnectionToPool( conn );
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
			conn = C3p0Util.getConnection();
			if ( null == conn )
				throw new Exception( "No available connection" );
			stmt = conn.createStatement();
			return stmt.executeUpdate( updateSql );
		} catch ( Exception e ) {
			throw new Exception( "执行数据库更新[" + updateSql + "]出错: " + e.getMessage(), e.getCause() );
		} finally {
			C3p0Util.closeStatement( stmt );
			C3p0Util.returnBackConnectionToPool( conn );
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
			conn = C3p0Util.getConnection();
			if ( null == conn )
				throw new Exception( "No available connection" );
			stmt = conn.createStatement();
			return stmt.executeUpdate( deleteSql );
		} catch ( Exception e ) {
			throw new Exception( "执行数据库删除[" + deleteSql + "]出错: " + e.getMessage(), e.getCause() );
		} finally {
			C3p0Util.closeStatement( stmt );
			C3p0Util.returnBackConnectionToPool( conn );
		}
	}

	public static void main( String[] args ) {

		DBConnectionResource myResultSet = null;
		ResultSet resultSet = null;
		String querySql = "select * from biz_log_rule";
		try {
			myResultSet = C3p0Util.executeQuery( querySql );
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
				C3p0Util.closeResultSetAndStatement( resultSet, myResultSet.statement );
			C3p0Util.returnBackConnectionToPool( myResultSet.connection );
		}
	}
	
	public void setDriverClassName( String driverClassName ) {
		C3p0Util.driverClassName = driverClassName;
	}

	public void setDbJDBCUrl( String dbJDBCUrl ) {
		C3p0Util.dbJDBCUrl = dbJDBCUrl;
	}

	public void setUsername( String username ) {
		C3p0Util.username = username;
	}

	public void setPassword( String password ) {
		C3p0Util.password = password;
	}

	public void setMaxActive( int maxActive ) {
		C3p0Util.maxActive = maxActive;
	}

	public void setMaxIdle( int maxIdle ) {
		C3p0Util.maxIdle = maxIdle;
	}

	public void setMaxWait( int maxWait ) {
		C3p0Util.maxWait = maxWait;
	}
	
	public void setCharacterEncoding( String characterEncoding ) {
		C3p0Util.characterEncoding = characterEncoding;
	}

}
