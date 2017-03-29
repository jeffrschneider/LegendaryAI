package ai.legendary.infrastructure;

import ai.legendary.core.SpeedConcat;
import ai.legendary.core.stringUtils;

public class LemmaResult {
	public String[] Apache = null;
	public LemmaResult() {
		return;
	}
	public static final String JSONexport(final LemmaResult input, final String otherData) {
		final SpeedConcat result = new SpeedConcat().append("{").append(otherData);
		if (!(otherData.equals(""))) {
			result.append(",");
		}
		result.append("\"Apache\":[\"").append(stringUtils.join(input.Apache,"\",\"")).append("\"]");
		return result.append("}").concat();
	}
}
