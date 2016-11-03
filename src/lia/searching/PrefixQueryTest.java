package lia.searching;

import junit.framework.TestCase;

import lia.common.TestUtil;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

// From chapter 3
public class PrefixQueryTest extends TestCase {

	public void testPrefix() throws Exception {

		Directory dir = TestUtil.getBookIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(dir);

		Term term = new Term("category", "/technology/computers/programming"); 	// #A
		PrefixQuery query = new PrefixQuery(term); 	// #A

		TopDocs matches = searcher.search(query, 10); 	// #A
		int programmingAndBelow = matches.totalHits;

		matches = searcher.search(new TermQuery(term), 10); 	// #B
		int justProgramming = matches.totalHits;

		assertTrue(programmingAndBelow > justProgramming);

		searcher.close();
		dir.close();
	}
}

/*
 * #A Search, including subcategories
 * #B Search, without subcategories
 */
