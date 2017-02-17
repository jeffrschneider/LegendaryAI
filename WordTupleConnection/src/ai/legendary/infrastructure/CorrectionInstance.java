package ai.legendary.infrastructure;

import java.util.Arrays;

public class CorrectionInstance {
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
