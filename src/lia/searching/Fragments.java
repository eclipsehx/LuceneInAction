package lia.searching;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

// From chapter 3
public class Fragments {

	public void openSearcher() throws Exception {
		Directory dir = FSDirectory.open(new File("/path/to/index"));
		IndexReader reader = IndexReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
	}

	public void nrtReader() throws Exception {

		IndexReader reader = null;
		IndexSearcher searcher;

		// START
		IndexReader newReader = reader.reopen();

		if (reader != newReader) {
			reader.close();
			reader = newReader;
			searcher = new IndexSearcher(reader);
		}
		// END
	}

	public void testSearchSigs() throws Exception {

		Query query = null;
		Filter filter = null;
		TopDocs hits;
		TopFieldDocs fieldHits;
		Sort sort = null;
		Collector collector = null;
		int n = 10;
		IndexSearcher searcher = null;

		hits = searcher.search(query, n);
		hits = searcher.search(query, filter, n);
		fieldHits = searcher.search(query, filter, n, sort);
		searcher.search(query, collector);
		searcher.search(query, filter, collector);
	}

	public void queryParserOperator() throws Exception {

		Analyzer analyzer = null;
		// START
		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", analyzer);
		parser.setDefaultOperator(QueryParser.AND_OPERATOR);
		// END
	}
}
