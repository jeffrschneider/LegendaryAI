package ai.legendary.infrastructure;
import java.util.Scanner;
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
		
		final WordTupleConnection tester = WordTupleConnection.MakeWordTupleConnection(username, password, domain, database, WordTupleConnection.databaseMode.MySQL);
		final ResultSet results = tester.query("SELECT COUNT(*) AS 'k' FROM `2Grams`;");
		results.first();
		System.out.println(results.getInt("k"));
		
		final QueryKit qk = new QueryKit(tester);
		System.out.println(qk.ngram2Pct("of", "the"));
		final String[] strings = qk.getFinalWordFromNGram2("going");
		for (final String k: strings) {
			System.out.println(k);
		}
		tester.close();
		
		input.close();
		return;
	}
}
