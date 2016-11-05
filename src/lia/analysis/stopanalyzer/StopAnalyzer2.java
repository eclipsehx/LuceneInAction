package lia.analysis.stopanalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;
import java.util.Set;

// From chapter 4

public class StopAnalyzer2 extends Analyzer {

	private Set<?> stopWords;

	public StopAnalyzer2() {
		this.stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
	}

	public StopAnalyzer2(String[] stopWords) {
		this.stopWords = StopFilter.makeStopSet(stopWords);
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new StopFilter(true, new LowerCaseFilter(new LetterTokenizer(reader)), stopWords);
	}
}
