package ai.legendary.core;

import java.sql.*;
import java.math.BigDecimal;

public class WordTupleConnection {
	/**
	 * An enumeration of different database server systems. Some are intercompatible.
	 * Valid values are:
	 * <ul>
	 * <li>MySQL: MySQL, a common DBMS.</li>
	 * <li>MariaDB: MariaDB, It is to MySQL what CentOS is to Redhat.</li>
	 * <li>Aurora: Amazon Aurora, a MySQL compatible.</li>
	 * <li>MMSQL: Microsoft SQL Server. Pending support.</li>
	 * <li>Postgres: Postres SQL. Pending support.</li>
	 * <li>Oracle: Oracle's SQL implementation. Pending support.</li>
	 * <li>TBD: A placeholder. Don't use this.</li>
	 * </ul>
	 */
	public static enum databaseMode {
		MySQL,
		MariaDB,
		Aurora, //I want to hear Scooby Doo say this name. I don't know why.
		
		MSSQL,
		Postgres,
		Oracle,
		
		TBD
	};
	/**
	 * NONE Is a constant BigDecimal, which is used by QueryKit.
	 */
	public static final BigDecimal NONE = new BigDecimal("-1");
	/**
	 * This makes a WordTupleConnection.
	 * @param Strimg username The username to be used by the connection, in string form.
	 * @param Strimg password The password to be used by the connection, in string form.
	 * @param Strimg domain The domain name or IP address to be used by the connection, in string form.
	 * @param Strimg databasename The database name to be used by the connection, in string form.
	 * @param WordTupleConnection.databaseMode connectionType A databaseMode to indicate what kind of database is being connected to.
	 * @return WordTupleConnection connection An already started WordTupleConnection.
	 * @exception Exception An exception that may be thrown due to either an unsupported database type, or a failed connection start.
	 */
	public static final WordTupleConnection MakeWordTupleConnection (final String username, final String password, final String domain, final String databasename, final databaseMode connectionType) throws Exception {
		return new WordTupleConnection(username, password, domain, databasename, connectionType);
	}
	private String username = "", password = "", domain = "", databasename = "";
	private databaseMode databaseType = databaseMode.TBD;
	private boolean AmOpen = false;
	private Connection conn = null;
	private WordTupleConnection(final String _username, final String _password, final String _domain, final String _databasename, final databaseMode _connectionType) throws Exception {
		username = _username;
		password = _password;
		domain = _domain;
		databasename = _databasename;
		databaseType = _connectionType;
		open();
	}
	/**
	 * Attempts to start the connection to the database. Note that this is automatically called when a new WordTupleConnection object is constructed.
	 * @exception Exception This exception will be called if the databaseMode of the connection is not yet supported, an unknown type of databaseMode is used, or if there is an error when attempting to connect to the database. The connection error may be caused by network issues, bad username/password combinations, a bad address, an incorrect name of the database, or some combination thereof.
	 */
	public final void open() throws Exception {
		switch (databaseType) {
			case Aurora:
			case MySQL:
			case MariaDB:
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				conn = DriverManager.getConnection("jdbc:mysql://" + domain + "/" + stringUtils.urlEncode(databasename), username , password);
				AmOpen = true;
				return;
				
			case MSSQL:
			case Oracle:
			case Postgres:
				throw new Exception("Pending development exception.");
				
			case TBD:
			default:
				throw new Exception("Unknown database type used.");
		}
		//return;
	}
	/**
	 * Terminates this connection. Note that if this is never called for this instance, the garbage collector may or may not call it.
	 * @exception Exception A possible exception from internal objects.
	 */
	public final void close() throws Exception{
		conn.close();
		AmOpen = false;
	}
	/**
	 * This function is for situations where everything except the connection is stable, and it's preferable to try to salvage the connection, rather than just have everything fail. This function attempts to terminate the connection, and attempts to start it again, in that order.
	 * @exception Exception any exception thrown by open() or close().
	 * */
	public final void reset() throws Exception{
		close();
		Thread.sleep(4000);
		open();
	}
	/**
	 * Runs a properly formatted SQL query on the connection in question.
	 * @param String QueryString the SQL query.
	 * @return ResultSet The results of the query, which will have 0 or more results. 
	 * @exception Exception An exception may be thrown if the query is improperly formatted to the point that the server can not understand it.
	 */
	public final ResultSet query (final String QueryString) throws SQLException {
		return conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(QueryString);
	}
	/**
	 * This function is only supposed to be called by the garbage collector when this WordTupleConnection is being flagged as free memory. When run, if this WordTupleConnection is not closed, it will attempt to close itself.
	 * @exception Exception E An exception which may eb thrown if close() throws an Exception.
	 */
	protected void finalize() throws Exception{
		if (AmOpen) {
			close();
		}
	}
}
