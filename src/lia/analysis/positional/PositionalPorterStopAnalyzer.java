package lia.analysis.positional;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;
import java.util.Set;

// From chapter 4

public class PositionalPorterStopAnalyzer extends Analyzer {

	private Set<?> stopWords;

	public PositionalPorterStopAnalyzer() {
		this(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
	}

	public PositionalPorterStopAnalyzer(Set<?> stopWords) {
		this.stopWords = stopWords;
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {

		StopFilter stopFilter = new StopFilter(true, new LowerCaseTokenizer(reader), stopWords);
		stopFilter.setEnablePositionIncrements(true);

		return new PorterStemFilter(stopFilter);
	}
}
