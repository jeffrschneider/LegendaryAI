package ai.legendary.infrastructure;

import java.net.URLEncoder;

public class stringUtils {
	public static final String urlEncode(final String src) throws Exception{
		if (src==null) {
			return "";
		}
		return URLEncoder.encode(src, "UTF-8").replaceAll("\\+","%20");
	}
}
