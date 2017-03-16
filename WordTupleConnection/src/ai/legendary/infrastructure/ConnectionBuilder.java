package ai.legendary.infrastructure;

public class ConnectionBuilder {
	/**
	 * Makes connections so there's only one point where Authentication information needs to be changed.
	 * @return WordTupleConnection output A new WordTupleConnection which is preconfigured for Legendary's database.
	 * @exception Exception E An exception that may be thrown by WordTupleConnection.MakeWordTupleConnection
	 */
	public final static WordTupleConnection build() throws Exception {
		return WordTupleConnection.MakeWordTupleConnection("MasterUser", "MasterPassword", "ngrams.cbndyymb1za5.us-east-1.rds.amazonaws.com", "NGrams", WordTupleConnection.databaseMode.MySQL);
	}
}
