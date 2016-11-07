package lia.extsearch.filters;

import junit.framework.TestCase;
import lia.common.TestUtil;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.index.Term;

// From chapter 6

public class SpecialsFilterTest extends TestCase {

	private Query allBooks;
	private IndexSearcher searcher;

	@Override
	protected void setUp() throws Exception {
		allBooks = new MatchAllDocsQuery();
		searcher = new IndexSearcher(TestUtil.getBookIndexDirectory(), true);
	}

	public void testCustomFilter() throws Exception {

		String[] isbns = new String[] { "9780061142666", "9780394756820" };

		SpecialsAccessor accessor = new TestSpecialsAccessor(isbns);
		Filter filter = new SpecialsFilter(accessor);

		TopDocs hits = searcher.search(allBooks, filter, 10);
		assertEquals("the specials", isbns.length, hits.totalHits);
	}

	public void testFilteredQuery() throws Exception {

		String[] isbns = new String[] { "9780880105118" };				// #1

		SpecialsAccessor accessor = new TestSpecialsAccessor(isbns);
		Filter filter = new SpecialsFilter(accessor);

		WildcardQuery educationBooks = new WildcardQuery(new Term("category", "*education*"));	// #2
		FilteredQuery edBooksOnSpecial = new FilteredQuery(educationBooks, filter);				// #2

		TermQuery logoBooks = new TermQuery(new Term("subject", "logo"));						// #3

		BooleanQuery logoOrEdBooks = new BooleanQuery();						// #4
		logoOrEdBooks.add(logoBooks, BooleanClause.Occur.SHOULD);				// #4
		logoOrEdBooks.add(edBooksOnSpecial, BooleanClause.Occur.SHOULD);		// #4

		TopDocs hits = searcher.search(logoOrEdBooks, 10);
		System.out.println(logoOrEdBooks.toString());
		assertEquals("Papert and Steiner", 2, hits.totalHits);
	}

	/*
	 * #1 Rudolf Steiner's book
	 * #2 All education books on special
	 * #3 All books with "logo" in subject
	 * #4 Combine queries
	 */
}
