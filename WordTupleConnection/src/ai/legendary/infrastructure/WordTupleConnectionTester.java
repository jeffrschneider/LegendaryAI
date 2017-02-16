package ai.legendary.infrastructure;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.math.BigDecimal;
public class WordTupleConnectionTester {
	/**
	 * This is just a test function to test WordTupleConnection.java and QueryKit.java. It doubles as example code for those who are stuck trying to understand my code. 
	 * @param String[] args unused
	 * @exception Exception I didn't feel like coding try/catches for this.
	 */
	public final static void main(final String[] args) throws Exception {
		final Scanner input = new Scanner(System.in);
		System.out.println("username:");
		final String username = input.nextLine();
		System.out.println("password:");
		final String password = input.nextLine();
		System.out.println("domain:");
		final String domain = input.nextLine();
		System.out.println("database:");
		final String database = input.nextLine();
		System.out.println();
		
		final WordTupleConnection tester = WordTupleConnection.MakeWordTupleConnection(username, password, domain, database, WordTupleConnection.databaseMode.MySQL);
		final ResultSet results = tester.query("SELECT COUNT(*) AS 'k' FROM `2Grams`;");
		results.first();
		System.out.println(results.getInt("k"));
		System.out.println();
		
		final QueryKit qk = new QueryKit(tester);
		System.out.println(qk.ngram2Pct("of", "the"));
		System.out.println();
		final String[] strings = qk.getFinalWordFromNGram2("going");
		for (final String k: strings) {
			System.out.println(k);
		}
		System.out.println();

		System.out.println(qk.ngram2Pct("clown", "disco"));
		System.out.println();
		
		System.out.println(qk.ngram3Pct("toilet", "p*", "a%"));
		System.out.println();

		final String[] strings2 = qk.getFinalWordFromNGram3("Merkle", "");
		if (strings2 != null) {
			for (final String k: strings2) {
				System.out.println(k);
			}
		} else {
			System.out.println("NULL");
		}
		System.out.println();
		
		System.out.println("testfile location");
		final SentenceCorrector sc = new SentenceCorrector(tester);
		final BufferedReader br = new BufferedReader(new FileReader(new File(input.nextLine())));
	 
		while (true) {
			final String lineA = br.readLine();
			if (lineA==null) {
				break;
			}
			final String lineB = br.readLine();
			final String stringC = sc.getBestEffortFix(lineA);
			System.out.println(stringC);
			final String[] words = stringUtils.convertToSentence(lineB);
			String stringd = words[0];
			for (int index = 1; index < words.length; index++) {
				stringd = stringd + (" " + words[index]);
			}
			System.out.println(stringd);
			System.out.println(stringd.equals(stringC));
			System.out.println();
		}
		br.close();
		
		System.out.println(sc.getstatinfo());
		
		tester.reset();
		tester.close();
		input.close();
		return;
	}
}
