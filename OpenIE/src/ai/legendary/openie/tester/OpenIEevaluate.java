package ai.legendary.openie.tester;

import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.ClearPostagger;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.ClearTokenizer;
import scala.collection.Seq;

public class OpenIEevaluate {
	public static final void main (final String[] args) {
		final OpenIE oie = new OpenIE(new ClearParser(new ClearPostagger(
                new ClearTokenizer(ClearTokenizer.defaultModelUrl()))),
                new ClearSrl(), false);
		final String sentence  = "U.S. President Obama gave a speech.\n\r";
		final Seq<Instance> intermediate = oie.extract(sentence);
		final Instance[] instances = new Instance[intermediate.size()];
		intermediate.copyToArray(instances);
		for (final Instance i: instances) {
			System.out.println(i.toString());
		}
		return;
	}
}
