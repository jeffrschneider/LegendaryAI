package ai.legendary.apache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;

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
	public static final String[] readFileAsLines(final String filename) throws Exception {
		final FileInputStream fstream = new FileInputStream(filename);
		final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		final LinkedList<String> result = new LinkedList<String>();
		while (true) {
			final String line = br.readLine();
			if (line==null) {
				break;
			}
			result.add(line);
		}
		br.close();
		return result.toArray(new String[result.size()]);
	}
	public static final void dumpDataToFile(final String filename, final InputStream in) throws Exception {
	    final OutputStream out = new FileOutputStream(filename);
		while (true) {
			final byte[] data = new byte[in.available()];
			final int k = in.read(data);
			if (k==-1) {
				break;
			}
			out.write(data);
		}
		in.close();
		out.close();
		return;
	}
}
