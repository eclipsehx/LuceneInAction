package lia.analysis.codec;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LetterTokenizer;
import java.io.Reader;

// From chapter 4

public class MetaphoneReplacementAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new MetaphoneReplacementFilter(new LetterTokenizer(reader));
	}
}
