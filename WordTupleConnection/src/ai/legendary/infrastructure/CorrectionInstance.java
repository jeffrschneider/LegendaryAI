package ai.legendary.infrastructure;

import java.util.Arrays;

public class CorrectionInstance {
	/**
	 * This function exists so there can be an easily used command line interface for setting up SentenceCorrector Objects. This makes it much easier to handle a multiprocess setup.
	 * @param String[] args This is all of the input. It's a standard CLI interface. The 1st String is the connection's username. The 2st String is the connection's password. The 3rd String is the connection's domain or IP address. The 4th String is the connection's database's name. The 5th String is the connection's database's servervendor, which is ignored and replaced with MySQL. The rest of the Strings are the sentence in question.
	 * @exception Exception Pretty much any exception thrown by WordTupleConnection, SentenceCorrector, QueryKit, StringUtils, or SpeedConcat. 
	 */
	public static final void main (String[] args) throws Exception {
		final String username = args[0];
		final String password = args[1];
		final String domain = args[2];
		final String database = args[3];
		final String mode = args[4];
		final String input = stringUtils.join(Arrays.copyOfRange(args, 5, args.length), " ");
		final WordTupleConnection wtc = WordTupleConnection.MakeWordTupleConnection(username,password,domain,database, WordTupleConnection.databaseMode.MySQL);
		final SentenceCorrector sc = new SentenceCorrector(wtc);
		System.out.print(sc.getBestEffortFix(input));
		wtc.close();
		return;
	}
}
