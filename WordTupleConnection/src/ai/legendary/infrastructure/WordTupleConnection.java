package ai.legendary.infrastructure;
import java.sql.*;
import java.math.BigDecimal;

public class WordTupleConnection {
	public static enum databaseMode {
		MySQL,
		MariaDB,
		Aurora, //I want to hear Scooby Doo say this name. I don't know why.
		
		MSSQL,
		Postgres,
		Oracle,
		
		TBD
	};
	public static final BigDecimal NONE = new BigDecimal("-1");
	public static WordTupleConnection MakeWordTupleConnection (final String username, final String password, final String domain, final String databasename, final databaseMode connectionType) throws Exception {
		return new WordTupleConnection(username, password, domain, databasename, connectionType);
	}
	private String username = "", password = "", domain = "", databasename = "";
	private databaseMode databaseType = databaseMode.TBD;
	Connection conn = null;
	private WordTupleConnection(final String _username, final String _password, final String _domain, final String _databasename, final databaseMode _connectionType) throws Exception {
		username = _username;
		password = _password;
		domain = _domain;
		databasename = _databasename;
		databaseType = _connectionType;
		start();
	}
	private void start() throws Exception{
		switch (databaseType) {
			case Aurora:
			case MySQL:
			case MariaDB:
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				conn = DriverManager.getConnection("jdbc:mysql://" + domain + "/" + stringUtils.urlEncode(databasename), username , password);
				return;
				
			case MSSQL:
			case Oracle:
			case Postgres:
				throw new Exception("Pending development exception.");
				
			case TBD:
			default:
				return;
		}
		//return;
	}
	public void close() throws Exception{
		conn.close();
	}
	public void reset() throws Exception{
		close();
		start();
	}
	public ResultSet query (final String QueryString) throws SQLException {
		return conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(QueryString);
	}
}
