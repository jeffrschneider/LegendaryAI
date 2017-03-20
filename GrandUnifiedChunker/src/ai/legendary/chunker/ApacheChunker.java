package ai.legendary.chunker;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class ApacheChunker {
	public static final ApacheChunker BuildApacheChunkerFromBins(final String tokenString, final String POSstring, final String chunkerString) throws Exception {
		if (tokenString==null) {
			throw new Exception();
		}
		if (POSstring==null) {
			throw new Exception();
		}
		if (chunkerString==null) {
			throw new Exception();
		}
		final ApacheChunker result = new ApacheChunker();
		
		final InputStream tokenIn = new FileInputStream(tokenString);
		final TokenizerModel tokenModel = new TokenizerModel(tokenIn);
		result.tokenizer = new TokenizerME(tokenModel);
		
		final InputStream posIn = new FileInputStream(POSstring);
		final POSModel model = new POSModel(posIn);
		result.pos = new POSTaggerME(model);
		
		final InputStream chunkerIn = new FileInputStream(chunkerString);
		final ChunkerModel chunkerModel = new ChunkerModel(chunkerIn);
		result.chunker = new ChunkerME(chunkerModel);
		
		
		
		chunkerIn.close();
		tokenIn.close();
		posIn.close();
		return result;
	}
	private POSTaggerME pos = null;
	private TokenizerME tokenizer = null;
	private ChunkerME chunker = null;
	private ApacheChunker() {}
	public final String[] tokenize(final String input) throws Exception {
		if (input==null) {
			throw new Exception();
		}
		return tokenizer.tokenize(input);
	}
	public final String[] POS(final String[] input) throws Exception {
		if (input==null) {
			throw new Exception();
		}
		return pos.tag(input);
	}
	public final String[] chunk(final String[] words, final String[] parts) throws Exception {
		if (words==null) {
			throw new Exception();
		}
		if (parts==null) {
			throw new Exception();
		}
		return chunker.chunk(words, parts);
	}
	
	public final ChunkerResult chunk(final String input) throws Exception {
		final ChunkerResult result = new ChunkerResult();
		result.tokens = this.tokenize(input);
		result.parts = this.POS(result.tokens);		

		final LinkedList<Integer> indexes = new LinkedList<Integer>();
		final LinkedList<String> tokens = new LinkedList<String>();
		tokens.addAll(Arrays.asList(result.tokens));
		final String[] chunks = this.chunk(result.tokens, result.parts);
		
		indexes.add(0);
		int tokenIndex = 1;
		for (int index = 1; index < chunks.length; index++) {
			final char label = chunks[index-1].charAt(0);
			final char nextlabel = chunks[index].charAt(0);
			if (label==nextlabel) {
				final String condensed = tokens.get(tokenIndex-1) + (" " + tokens.get(tokenIndex));
				tokens.set(tokenIndex-1, condensed);
				tokens.remove(tokenIndex);
			} else {
				tokenIndex++;
				indexes.add(index);
				indexes.add(index);
			}
			
		}
		indexes.add(result.tokens.length);
		
		result.sections = tokens.toArray(new String[tokens.size()]);
		result.indexes = new int[indexes.size()];
		for (int k = 0; k < result.indexes.length; k++) {
			result.indexes[k] = indexes.get(k).intValue();
		}
		result.vendor = "Apache";
		return result;
	}
}
