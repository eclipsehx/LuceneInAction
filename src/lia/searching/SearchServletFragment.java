package lia.searching;

import java.io.IOException;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import lia.extsearch.queryparser.NumericQueryParserTest.NumericDateRangeQueryParser;

// From chapter 6
public class SearchServletFragment extends HttpServlet {

	private IndexSearcher searcher;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		QueryParser parser = new NumericDateRangeQueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));

		parser.setLocale(request.getLocale());
		parser.setDateResolution(DateTools.Resolution.DAY);

		Query query = null;

		try {
			query = parser.parse(request.getParameter("q"));
		} catch (ParseException e) {
			e.printStackTrace(System.err); 	// 1
		}

		TopDocs docs = searcher.search(query, 10);	 // 2
	}

	/*
	 * 1 Handle exception
	 * 2 Perfom search and render results
	 */
}
