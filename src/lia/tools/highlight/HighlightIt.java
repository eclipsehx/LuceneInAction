package lia.tools.highlight;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;

import java.io.FileWriter;
import java.io.StringReader;

// From chapter 8

public class HighlightIt {

	private static final String text = "In this section we'll show you how to make the simplest "
		+ "programmatic query, searching for a single term, and then "
		+ "we'll see how to use QueryParser to accept textual queries. "
		+ "In the sections that follow, we’ll take this simple example "
		+ "further by detailing all the query types built into Lucene. "
		+ "We begin with the simplest search of all: searching for all "
		+ "documents that contain a single term.";

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.err.println("Usage: HighlightIt <filename-out>");
			System.exit(-1);
		}

		String filename = args[0];

		String searchText = "term";										// #1

		QueryParser parser = new QueryParser(Version.LUCENE_30, "f", new StandardAnalyzer(Version.LUCENE_30));	// #1
		Query query = parser.parse(searchText); // #1

		SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");		// #2

		TokenStream tokens = new StandardAnalyzer(Version.LUCENE_30).tokenStream("f", new StringReader(text));	// #3

		QueryScorer scorer = new QueryScorer(query, "f");														// #4

		Highlighter highlighter = new Highlighter(formatter, scorer);											// #5
		highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));										// #6

		String result = highlighter.getBestFragments(tokens, text, 3, "...");									// #7

		FileWriter writer = new FileWriter(filename);															// #8
		writer.write("<html>\n");																					// #8
		writer.write("<style>\n" + ".highlight {\n" + " background: yellow;\n" + "}\n" + "</style>\n");			// #8
		writer.write("<body>\n");																					// #8
		writer.write(result);																					// #8
		writer.write("\n</body>\n</html>");																			// #8
		writer.close();																							// #8
	}
}

/*
#1 Create the query
#2 Customize surrounding tags
#3 Tokenize text
#4 Create QueryScorer
#5 Create highlighter
#6 Use SimpleSpanFragmenter to fragment
#7 Highlight best 3 fragments
#8 Write highlighted HTML
*/
