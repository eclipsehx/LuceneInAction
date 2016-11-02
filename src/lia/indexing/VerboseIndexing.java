package lia.indexing;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.WhitespaceAnalyzer;

import java.io.IOException;

// From chapter 2

public class VerboseIndexing {

	private void index() throws IOException {

		Directory dir = new RAMDirectory();

		IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);

		writer.setInfoStream(System.out);

		for (int i = 0; i < 100; i++) {
			Document doc = new Document();
			doc.add(new Field("keyword", "goober", Field.Store.YES, Field.Index.NOT_ANALYZED));
			writer.addDocument(doc);
		}

		writer.optimize();
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		VerboseIndexing vi = new VerboseIndexing();
		vi.index();
	}
}
