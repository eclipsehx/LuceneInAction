package lia.analysis.keyword;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.CharTokenizer;

import java.io.Reader;

// From chapter 4

/**
 * CharTokenizer limits token width to 255 characters, though. This
 * implementation assumes keywords are 255 in length or less.
 */

public class SimpleKeywordAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {

		return new CharTokenizer(reader) {

			@Override
			protected boolean isTokenChar(char c) {
				return true;
			}
		};
	}
}
