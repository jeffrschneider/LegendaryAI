package ai.legendary.infrastructure;

public class QueryString {
	private String[] keys = null;
	private String[] values = null;
	/**
	 * Constructor. Returns a new QueryString Object. QueryString Objects are basically an interface to easily access values in URL query strings.
	 * @param String input All of the URL after the ? character
	 * @return QueryString result the resulting object.
	 * @exception Exception E An error that may occur due to REGEX issues or improperly formatted parameters.
	 */
	public QueryString(final String input) throws Exception {
		if (input==null) {
			keys = new String[0];
			values = new String[0];
			return;
		}
		if (input.equals("")) {
			keys = new String[0];
			values = new String[0];
			return;
		}
		generate(input.split("\\&"));
	}
	/**
	 * Constructor. Returns a new QueryString Object. QueryString Objects are basically an interface to easily access values in URL query strings.
	 * @param String[] input The URL parameters broken up upon the Ampersand character, but only the characters after the question mark.
	 * @return QueryString result The resulting object.
	 * @exception Exception E An error that may occur due to REGEX issues or improperly formatted parameters.
	 */
	public QueryString(final String[] input) throws Exception {
		generate(input);
	}
	private final void generate(final String[] input) throws Exception {
		keys = new String[input.length];
		values = new String[input.length];
		for (int index = 0; index < input.length; index++) {
			final String[] pair = input[index].split("\\=");
			keys[index] = stringUtils.urlDecode(pair[0]);
			values[index] = stringUtils.urlDecode(pair[1]);
		}
	}
	/**
	 * If there is a parameter within the query object whose name is the same as the input, then this function will return the value of the parameter. If none exists, it will return the default value.
	 * @param String input A name of a parameter which may or may not be in the parameters.
	 * @param String defaultVal The default value to be returned if no parameter is found with the same name as the input. 
	 * @return String result If a parameter was found with the same name as the input, then this will be the parameter's value. If one wasn't found, it will be the default value.
	 */
	public final String get(final String input, final String defaultVal) {
		for (int index = 0; index < keys.length; index++) {
			if (keys[index].equals(input)) {
				return values[index];
			}
		}
		return defaultVal;
	}
	/**
	 * If there is a parameter within the query object whose name is the same as the input, then this function will return the value of the parameter. If none exists, it will return "".
	 * @param String input A name of a parameter which may or may not be in the parameters.
	 * @return String result If a parameter was found with the same name as the input, then this will be the parameter's value. If one wasn't found, it will be "".
	 */
	public final String get(final String input) {
		return get(input, "");
	}
}
