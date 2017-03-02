package ai.legendary.infrastructure;

public class ConnectionBuilder {
	public final static WordTupleConnection build() throws Exception {
		return WordTupleConnection.MakeWordTupleConnection("MasterUser", "MasterPassword", "ngrams.cbndyymb1za5.us-east-1.rds.amazonaws.com", "NGrams", WordTupleConnection.databaseMode.MySQL);
	}
}
