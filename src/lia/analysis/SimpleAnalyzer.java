package lia.analysis;

import java.io.Reader;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.Tokenizer;

// From chapter 4

public final class SimpleAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new LowerCaseTokenizer(reader);
	}

	@Override
	public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {

		Tokenizer tokenizer = (Tokenizer) getPreviousTokenStream();

		if (tokenizer == null) {
			tokenizer = new LowerCaseTokenizer(reader);
			setPreviousTokenStream(tokenizer);
		} else {
			tokenizer.reset(reader);
		}

		return tokenizer;
	}

	public static void main(String[] args) throws IOException {
		AnalyzerUtils.displayTokensWithFullDetails(new SimpleAnalyzer(), "The quick brown fox....");
	}
}
