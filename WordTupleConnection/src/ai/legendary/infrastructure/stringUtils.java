package ai.legendary.infrastructure;

import java.net.URLEncoder;

public class stringUtils {
	/**
	 * A function which is identical to Java's URLEncoder.encode, except it doesn't have a special exception for space characters. It can also handle null values.
	 * @param String src The source String.
	 * @return String The result.
	 * @exception What happens if there's a REGEX error or a URLEncoder error.
	 */
	public static final String urlEncode(final String src) throws Exception{
		if (src==null) {
			return "";
		}
		return URLEncoder.encode(src, "UTF-8").replaceAll("\\+","%20");
	}
}
