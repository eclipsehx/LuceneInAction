package lia.extsearch.collector;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.FieldCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// From chapter 6

public class BookLinkCollector extends Collector {

	private Map<String, String> documents = new HashMap<String, String>();
	private Scorer scorer;
	private String[] urls;
	private String[] titles;

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return true;	// #A
	}

	@Override
	public void setScorer(Scorer scorer) {
		this.scorer = scorer;
	}

	@Override
	public void setNextReader(IndexReader reader, int docBase) throws IOException {
		urls = FieldCache.DEFAULT.getStrings(reader, "url");		// #B
		titles = FieldCache.DEFAULT.getStrings(reader, "title2");	// #B
	}

	@Override
	public void collect(int docID) {
		try {
			String url = urls[docID];		// #C
			String title = titles[docID];	// #C
			documents.put(url, title);		// #C
			System.out.println(title + ":" + scorer.score());
		} catch (IOException e) {
			// ignore
		}
	}

	public Map<String, String> getLinks() {
		return Collections.unmodifiableMap(documents);
	}
}

/*
 * #A Accept docIDs out of order
 * #B Load FieldCache values
 * #C Store details for the match
 */
