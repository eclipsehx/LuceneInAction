package lia.searching;

import junit.framework.TestCase;
import lia.common.TestUtil;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.store.Directory;

// From chapter 3
public class BooleanQueryTest extends TestCase {

	public void testAnd() throws Exception {

		TermQuery searchingBooks = new TermQuery(new Term("subject", "search")); 	// #1

		Query books2010 = NumericRangeQuery.newIntRange("pubmonth", 201001, 201012, true, true); 	// #2

		BooleanQuery searchingBooks2010 = new BooleanQuery(); 	// #3
		searchingBooks2010.add(searchingBooks, BooleanClause.Occur.MUST); 	// #3
		searchingBooks2010.add(books2010, BooleanClause.Occur.MUST); 		// #3

		Directory dir = TestUtil.getBookIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(dir);

		TopDocs matches = searcher.search(searchingBooks2010, 10);

		assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Lucene in Action, Second Edition"));

		searcher.close();
		dir.close();
	}

	/*
	 * #1 Match books with subject “search”
	 * #2 Match books in 2004
	 * #3 Combines two queries
	 */

	public void testOr() throws Exception {

		TermQuery methodologyBooks = new TermQuery(new Term("category", "/technology/computers/programming/methodology"));	 // #1

		TermQuery easternPhilosophyBooks = new TermQuery(new Term("category", "/philosophy/eastern")); 	// #2

		BooleanQuery enlightenmentBooks = new BooleanQuery(); 	// #3
		enlightenmentBooks.add(methodologyBooks, BooleanClause.Occur.SHOULD); 			// #3
		enlightenmentBooks.add(easternPhilosophyBooks, BooleanClause.Occur.SHOULD); 	// #3

		Directory dir = TestUtil.getBookIndexDirectory();
		IndexSearcher searcher = new IndexSearcher(dir);

		TopDocs matches = searcher.search(enlightenmentBooks, 10);

		System.out.println("or = " + enlightenmentBooks);

		assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Extreme Programming Explained"));
		assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Tao Te Ching \u9053\u5FB7\u7D93"));

		searcher.close();
		dir.close();
	}

	/*
	 * #1 Match 1st category
	 * #2 Match 2nd category
	 * #3 Combine
	 */
}
