package lia.admin;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

// From chapter 11

/** Utility class to get/refresh searchers when you are using multiple threads. */

public class SearcherManager {

	private IndexSearcher currentSearcher;										// A
	private IndexWriter writer;

	public SearcherManager(Directory dir) throws IOException {							// 1
		currentSearcher = new IndexSearcher(IndexReader.open(dir));				// B
		warm(currentSearcher);
	}

	public SearcherManager(IndexWriter writer) throws IOException {						// 2

		this.writer = writer;
		this.currentSearcher = new IndexSearcher(writer.getReader());			// C

		warm(currentSearcher);

		writer.setMergedSegmentWarmer( new IndexWriter.IndexReaderWarmer() {			// 3
			@Override
			public void warm(IndexReader reader) throws IOException {					// 3
				SearcherManager.this.warm(new IndexSearcher(reader));					// 3
			}																			// 3
		});																				// 3
	}

	public void warm(IndexSearcher searcher) throws IOException {				// D
	}

	private boolean reopening;

	private synchronized void startReopen() throws InterruptedException {

		while (reopening) {
			wait();
		}

		reopening = true;
	}

	private synchronized void doneReopen() {
		reopening = false;
		notifyAll();
	}

	public void maybeReopen() throws InterruptedException, IOException {		// E

		startReopen();

		try {

			final IndexSearcher searcher = get();

			try {

				IndexReader newReader = currentSearcher.getIndexReader().reopen();

				if (newReader != currentSearcher.getIndexReader()) {

					IndexSearcher newSearcher = new IndexSearcher(newReader);

					if (writer == null) {
						warm(newSearcher);
					}

					swapSearcher(newSearcher);
				}

			} finally {
				release(searcher);
			}
		} finally {
			doneReopen();
		}
	}

	public synchronized IndexSearcher get() {									// F
		currentSearcher.getIndexReader().incRef();
		return currentSearcher;
	}

	public synchronized void release(IndexSearcher searcher) throws IOException {
		searcher.getIndexReader().decRef();
	}

	private synchronized void swapSearcher(IndexSearcher newSearcher) throws IOException {
		release(currentSearcher);
		currentSearcher = newSearcher;
	}

	public void close() throws IOException {
		swapSearcher(null);
	}
}

/*
#A Current IndexSearcher
#B Create searcher from Directory
#C Create searcher from near-real-time reader
#D Implement in subclass
#E Reopen searcher
#F Returns current searcher
#G Release searcher
*/
