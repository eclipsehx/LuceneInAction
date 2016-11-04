package lia.analysis;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

// From chapter 4

public class Fragments {

	public void frag1() throws Exception {

		Directory directory = null;
		// START
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		IndexWriter writer = new IndexWriter(directory, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
		// END
	}

	public void frag2() throws Exception {

		IndexWriter writer = null;
		// START
		Document doc = new Document();
		doc.add(new Field("title", "This is the title", Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("contents", "...document contents...", Field.Store.NO, Field.Index.ANALYZED));
		writer.addDocument(doc);
		// END
	}

	public void frag3() throws Exception {

		Analyzer analyzer = null;
		String text = null;
		// START
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
		PositionIncrementAttribute posIncr = (PositionIncrementAttribute) stream.addAttribute(PositionIncrementAttribute.class);

		while (stream.incrementToken()) {
			System.out.println("posIncr=" + posIncr.getPositionIncrement());
		}
		// END
	}
}