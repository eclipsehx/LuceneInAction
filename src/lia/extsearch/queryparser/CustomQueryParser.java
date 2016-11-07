package lia.extsearch.queryparser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.Version;

// From chapter 6

public class CustomQueryParser extends QueryParser {

	public CustomQueryParser(Version matchVersion, String field, Analyzer analyzer) {
		super(matchVersion, field, analyzer);
	}

	@Override
	protected final Query getWildcardQuery(String field, String termStr) throws ParseException {
		throw new ParseException("Wildcard not allowed");
	}

	@Override
	protected Query getFuzzyQuery(String field, String term, float minSimilarity) throws ParseException {
		throw new ParseException("Fuzzy queries not allowed");
	}

	/**
	 * Replace PhraseQuery with SpanNearQuery to force in-order phrase matching
	 * rather than reverse.
	 */
	@Override
	protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException {

		Query orig = super.getFieldQuery(field, queryText, slop);	// #1

		if (!(orig instanceof PhraseQuery)) {	// #2
			return orig;	// #2
		}

		PhraseQuery pq = (PhraseQuery) orig;

		Term[] terms = pq.getTerms();	// #3

		SpanTermQuery[] clauses = new SpanTermQuery[terms.length];

		for (int i = 0; i < terms.length; i++) {
			clauses[i] = new SpanTermQuery(terms[i]);
		}

		SpanNearQuery query = new SpanNearQuery(clauses, slop, true);	// #4

		return query;
	}

	/*
	 * #1 Delegate to QueryParser's implementation
	 * #2 Only override PhraseQuery
	 * #3 Pull all terms
	 * #4 Create SpanNearQuery
	 */
}
