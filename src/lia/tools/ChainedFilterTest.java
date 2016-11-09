package lia.tools;

import junit.framework.TestCase;

import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.misc.ChainedFilter;

import java.util.Calendar;

import lia.common.TestUtil;

// From chapter 9

public class ChainedFilterTest extends TestCase {

	private static final int MAX = 500;
	private RAMDirectory directory;
	private IndexSearcher searcher;
	private Query query;
	private Filter dateFilter;
	private Filter bobFilter;
	private Filter sueFilter;

	@Override
	public void setUp() throws Exception {

		directory = new RAMDirectory();
		IndexWriter writer = new IndexWriter(directory, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);

		Calendar cal = Calendar.getInstance();
		cal.set(2009, 0, 1, 0, 0);							// A

		for (int i = 0; i < MAX; i++) {

			Document doc = new Document();

			doc.add(new Field("key", "" + (i + 1), Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("owner", (i < MAX / 2) ? "bob" : "sue", Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("date", DateTools.timeToString(cal.getTimeInMillis(), DateTools.Resolution.DAY), Field.Store.YES, Field.Index.NOT_ANALYZED));

			writer.addDocument(doc);

			cal.add(Calendar.DATE, 1);
		}

		writer.close();

		searcher = new IndexSearcher(directory);

		BooleanQuery bq = new BooleanQuery();											// B
		bq.add(new TermQuery(new Term("owner", "bob")), BooleanClause.Occur.SHOULD);	// B
		bq.add(new TermQuery(new Term("owner", "sue")), BooleanClause.Occur.SHOULD);	// B

		query = bq;

		cal.set(2099, 0, 1, 0, 0);

		dateFilter = TermRangeFilter.Less("date", DateTools.timeToString(cal.getTimeInMillis(), DateTools.Resolution.DAY));		// C

		bobFilter = new CachingWrapperFilter(new QueryWrapperFilter(new TermQuery(new Term("owner", "bob"))));					// D

		sueFilter = new CachingWrapperFilter(new QueryWrapperFilter(new TermQuery(new Term("owner", "sue"))));					// E
	}

  /*
    #A 2009 January 01
    #B BooleanQuery that matches all documents
    #C Date filter also matches all documents
    #D Matches only Bob's documents
    #E Matches only Sue's documents
   */

	public void testSingleFilter() throws Exception {

		ChainedFilter chain = new ChainedFilter(new Filter[] { dateFilter });

		TopDocs hits = searcher.search(query, chain, 10);

		assertEquals(MAX, hits.totalHits);

		chain = new ChainedFilter(new Filter[] { bobFilter });

		assertEquals(MAX / 2, TestUtil.hitCount(searcher, query, chain), hits.totalHits);
	}

	public void testOR() throws Exception {

		ChainedFilter chain = new ChainedFilter(new Filter[] { sueFilter, bobFilter });

		assertEquals("OR matches all", MAX, TestUtil.hitCount(searcher, query, chain));
	}

	public void testAND() throws Exception {

		ChainedFilter chain = new ChainedFilter(new Filter[] { dateFilter, bobFilter }, ChainedFilter.AND);

		TopDocs hits = searcher.search(query, chain, 10);

		assertEquals("AND matches just Bob", MAX / 2, hits.totalHits);

		Document firstDoc = searcher.doc(hits.scoreDocs[0].doc);
		assertEquals("bob", firstDoc.get("owner"));
	}

	public void testXOR() throws Exception {

		ChainedFilter chain = new ChainedFilter(new Filter[] { dateFilter, bobFilter }, ChainedFilter.XOR);		// a⊕b = (¬a ∧ b) ∨ (a ∧¬b)
//		ChainedFilter chain = new ChainedFilter(new Filter[] { bobFilter, sueFilter }, ChainedFilter.XOR);		// hits.totalHits = 500

		TopDocs hits = searcher.search(query, chain, 10);

		assertEquals("XOR matches Sue", MAX / 2, hits.totalHits);

		Document firstDoc = searcher.doc(hits.scoreDocs[0].doc);
		assertEquals("sue", firstDoc.get("owner"));
	}

	public void testANDNOT() throws Exception {

		ChainedFilter chain = new ChainedFilter(new Filter[] { dateFilter, sueFilter }, new int[] { ChainedFilter.AND, ChainedFilter.ANDNOT });

		TopDocs hits = searcher.search(query, chain, 10);

		assertEquals("ANDNOT matches just Bob", MAX / 2, hits.totalHits);

		Document firstDoc = searcher.doc(hits.scoreDocs[0].doc);
		assertEquals("bob", firstDoc.get("owner"));
	}
}
