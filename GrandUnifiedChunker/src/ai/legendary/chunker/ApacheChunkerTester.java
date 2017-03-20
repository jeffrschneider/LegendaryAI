package ai.legendary.chunker;

public class ApacheChunkerTester {
	public static final void main(final String[] args) throws Exception {
		final String dir = "C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\GrandUnifiedChunker\\WebContent\\WEB-INF\\";
		final ApacheChunker apache = ApacheChunker.BuildApacheChunkerFromBins(dir + "apacheTokens.bin", dir + "apachePOS.bin", dir + "apacheChunks.bin");
		final ChunkerResult cr = apache.chunk("This is a simple sentence.");
		System.out.println(ChunkerResult.jsonExport(cr));
		return;
	}
}
