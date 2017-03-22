package ai.legendary.chunker;

import java.util.Iterator;
import java.util.List;

public class ChunkerResult {
	public String vendor = null;
	public String[] tokens = null;
	public String[] parts = null;
	public String[] sections = null;
	public int[] indexes = null;
	public ChunkerResult(){
		
	}
	private static final void exportSection (final String section, final int a, final int b, final SpeedConcat sc) {
		sc.append("{\"StartIndex\": ")
		.append(a).append(", \"EndIndex\": ")
		.append(b).append(", \"text\": \"")
		.append(section.replaceAll("\"", "\\\"")).append("\"}");
		return;
	}
	public static final String jsonExport(final ChunkerResult input) {
		final SpeedConcat result = (new SpeedConcat()).append("{\"vendor\": \"").append(input.vendor).append("\",\"tokens\":[");
		if (input.tokens.length > 0) {
			result.append("\"").append(input.tokens[0]).append("\"");
		}
		for (int index = 1; index < input.tokens.length; index++) {
			result.append(",\"").append(input.tokens[index]).append("\"");
		}
		result.append("], \"parts\": [");
		if (input.parts.length > 0) {
			result.append("\"").append(input.parts[0]).append("\"");
		}
		for (int index = 1; index < input.parts.length; index++) {
			result.append(",\"").append(input.parts[index]).append("\"");
		}
		result.append("], \"sections\": [");
		int intIndex = 0;
		if (input.sections.length > 0) {
			exportSection(input.sections[0], input.indexes[intIndex], input.indexes[intIndex+1], result);
		}
		intIndex++;
		intIndex++;
		for (int index = 1; index < input.sections.length; index++) {
			result.append(",");
			exportSection(input.sections[index], input.indexes[intIndex], input.indexes[intIndex+1], result);
			intIndex++;
			intIndex++;
		}
		return result.append("]}").concat();
	}
	public static final int[] IntegerListToIntArray(final List<Integer> input) throws Exception {
		if (input==null) {
			throw new Exception();
		}
		final int[] result = new int[input.size()];
		final Iterator<Integer> i = input.iterator();
		for (int index = 0; index < result.length; index++) {
			result[index] = i.next().intValue();
		}
		return result;
	}
}
