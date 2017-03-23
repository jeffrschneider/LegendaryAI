package ai.legendary.chunker;

public class ApacheChunkerTester {
	public static final void main(final String[] args) throws Exception {
		final String dir = "C:\\Users\\natew\\Desktop\\LegendaryAI\\outwardCode\\LegendaryAI\\GrandUnifiedChunker\\WebContent\\WEB-INF\\";
		final ApacheChunker apache = ApacheChunker.BuildApacheChunkerFromBins(dir + "apacheTokens.bin", dir + "apachePOS.bin", dir + "apacheChunks.bin");
		final String s1 = "This is a simple sentence.";
		final String s2 = "Nobody knows why Kmart stays in business and makes a profit. Some of us think questionable finances, parallel universes, or inflation-bypassing timetravel may be involved.";
		final String s3 = "Yankee candle makes Christmas and Halloween candles, yet they do not make Thanksgiving candles. Somebody should make a candle that looks like an oven fire.";
		final ChunkerResult cr = apache.chunk(s1);
		System.out.println(ChunkerResult.jsonExport(cr));
		final ChunkerResult cr2 = apache.chunk(s2);		
		System.out.println(ChunkerResult.jsonExport(cr2));
		final ChunkerResult cr3 = apache.chunk(s3);		
		System.out.println(ChunkerResult.jsonExport(cr3));
		
		final StanfordChunker sc = new StanfordChunker();
		final ChunkerResult sr = sc.chunk(s1);
		System.out.println(ChunkerResult.jsonExport(sr));
		final ChunkerResult sr2 = sc.chunk(s2);		
		System.out.println(ChunkerResult.jsonExport(sr2));
		final ChunkerResult sr3 = sc.chunk(s3);		
		System.out.println(ChunkerResult.jsonExport(sr3));
		
		return;
	}
}
