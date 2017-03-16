package ai.legendary.infrastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LineAdd {
	public static final void add(final String line, final String file) throws IOException {
		final File f = new File(file);
		final FileWriter writer = new FileWriter(f.getAbsoluteFile(), true);
		final BufferedWriter buffer = new BufferedWriter(writer);
		buffer.write(line);
		buffer.write("\r\n");
		buffer.close();
		writer.close();
		return;
	}
}
