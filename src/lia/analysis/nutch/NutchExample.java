package lia.analysis.nutch;

import java.io.IOException;
import java.io.StringReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.QueryFilters;

// From chapter 4

/* lucene-core-2.4.0.jar

public class NutchExample {

	public static void main(String[] args) throws IOException {

		Configuration conf = new Configuration();
		conf.addResource("nutch-default.xml");
		NutchDocumentAnalyzer analyzer = new NutchDocumentAnalyzer(conf);								// 1

		TokenStream ts = analyzer.tokenStream("content", new StringReader("The quick brown fox..."));

		int position = 0;
		while (true) {																					// 2

			Token token = ts.next();

			if (token == null) {
				break;
			}

			int increment = token.getPositionIncrement();

			if (increment > 0) {
				position = position + increment;
				System.out.println();
				System.out.print(position + ": ");
			}

			System.out.print("[" + token.termText() + ":" + token.startOffset() + "->" + token.endOffset() + ":" + token.type() + "] ");
		}

		System.out.println();

		Query nutchQuery = Query.parse("\"the quick brown\"", conf);									// 3
		org.apache.lucene.search.Query luceneQuery = new QueryFilters(conf).filter(nutchQuery);							// A
		System.out.println("Translated: " + luceneQuery);
	}
}


*/


/*
#1 Custom analyzer
#2 Display token details
#3 Parse to Nutch's Query
#A Create corresponding translated Lucene Query
*/
