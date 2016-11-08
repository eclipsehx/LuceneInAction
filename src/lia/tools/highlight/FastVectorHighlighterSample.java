package lia.tools.highlight;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

// From chapter 8

public class FastVectorHighlighterSample {

	static final String[] DOCS = {										// #A
		"the quick brown fox jumps over the lazy dog",					// #A
		"the quick gold fox jumped over the lazy black dog",			// #A
		"the quick fox jumps over the black dog",						// #A
		"the red fox jumped over the lazy dark gray dog"				// #A
	};

	static final String QUERY = "quick OR fox OR \"lazy dog\"~1";		// #B
	static final String F = "f";
	static Directory dir = new RAMDirectory();
	static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.err.println("Usage: FastVectorHighlighterSample <filename>");
			System.exit(-1);
		}

		makeIndex();

		searchIndex(args[0]);
	}

	static void makeIndex() throws IOException {

		IndexWriter writer = new IndexWriter(dir, analyzer, true, MaxFieldLength.UNLIMITED);

		for (String d : DOCS) {
			Document doc = new Document();
			doc.add(new Field(F, d, Store.YES, Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));
			writer.addDocument(doc);
		}

		writer.close();
	}

	static void searchIndex(String filename) throws Exception {

		QueryParser parser = new QueryParser(Version.LUCENE_30, F, analyzer);
		Query query = parser.parse(QUERY);

		FastVectorHighlighter highlighter = getHighlighter();					// #C
		FieldQuery fieldQuery = highlighter.getFieldQuery(query);				// #D

		IndexSearcher searcher = new IndexSearcher(dir);
		TopDocs docs = searcher.search(query, 10);

		FileWriter writer = new FileWriter(filename);
		writer.write("<html>\n");
		writer.write("<body>\n");
		writer.write("<p>QUERY : " + QUERY + "</p>\n");

		for (ScoreDoc scoreDoc : docs.scoreDocs) {

			String snippet = highlighter.getBestFragment(fieldQuery, searcher.getIndexReader(), scoreDoc.doc, F, 100);		// #E

			if (snippet != null) {
				writer.write(scoreDoc.doc + " : " + snippet + "<br/>\n");
			}
		}

		writer.write("</body>\n</html>\n");
		writer.close();
		searcher.close();
	}

	static FastVectorHighlighter getHighlighter() {

		FragListBuilder fragListBuilder = new SimpleFragListBuilder();		// #F
		FragmentsBuilder fragmentBuilder = new ScoreOrderFragmentsBuilder(BaseFragmentsBuilder.COLORED_PRE_TAGS, BaseFragmentsBuilder.COLORED_POST_TAGS);	// #F

		return new FastVectorHighlighter(true, true, fragListBuilder, fragmentBuilder);		// #F
	}
}

/*
#A Index these documents
#B Run this query
#C Get FastVectorHighlighter
#D Create FieldQuery
#E Highlight top fragment
#F Create FastVectorHighlighter
*/
