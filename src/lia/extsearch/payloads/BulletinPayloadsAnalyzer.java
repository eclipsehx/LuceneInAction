package lia.extsearch.payloads;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

// From chapter 6

public class BulletinPayloadsAnalyzer extends Analyzer {

	private float boost;
	private boolean isBulletin;

	BulletinPayloadsAnalyzer(float boost) {
		this.boost = boost;
	}

	void setIsBulletin(boolean v) {
		isBulletin = v;
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		BulletinPayloadsFilter stream = new BulletinPayloadsFilter(new StandardAnalyzer(Version.LUCENE_30).tokenStream(fieldName, reader), boost);
		stream.setIsBulletin(isBulletin);
		return stream;
	}
}
