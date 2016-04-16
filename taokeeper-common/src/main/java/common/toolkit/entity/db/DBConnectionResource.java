package common.toolkit.entity.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库连接占用的资源 包含 Connection, Statement, ResultSet，便于close
 * @author 银时 yinshi.nc@taobao.com
 */
public class DBConnectionResource {
	
	public DBConnectionResource( Connection connection, Statement statement, ResultSet resultSet ) {
		this.connection = connection;
		this.statement = statement;
		this.resultSet = resultSet;
	}
	
	public Connection connection;
	public Statement statement;
	public ResultSet	resultSet;
}
