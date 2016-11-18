package lia.admin;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

// From chapter 11

/**
 * Drop-in replacement for IndexWriter that uses multiple threads, under the hood, to index added documents.
 */

public class ThreadedIndexWriter extends IndexWriter {

	private ExecutorService threadPool;
	private Analyzer defaultAnalyzer;

	private class Job implements Runnable {															// A

		Document doc;
		Analyzer analyzer;
		Term delTerm;

		public Job(Document doc, Term delTerm, Analyzer analyzer) {
			this.doc = doc;
			this.analyzer = analyzer;
			this.delTerm = delTerm;
		}

		@Override
		public void run() {																			// B

			try {
				if (delTerm != null) {
					ThreadedIndexWriter.super.updateDocument(delTerm, doc, analyzer);
				} else {
					ThreadedIndexWriter.super.addDocument(doc, analyzer);
				}
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
	}

	public ThreadedIndexWriter(Directory dir, Analyzer a, boolean create, int numThreads, int maxQueueSize, IndexWriter.MaxFieldLength mfl) throws CorruptIndexException, IOException {

		super(dir, a, create, mfl);

		defaultAnalyzer = a;

		threadPool = new ThreadPoolExecutor(numThreads, numThreads, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(maxQueueSize, false), new ThreadPoolExecutor.CallerRunsPolicy());		// C
	}

	@Override
	public void addDocument(Document doc) {															// D
		threadPool.execute(new Job(doc, null, defaultAnalyzer));									// D
	}

	@Override
	public void addDocument(Document doc, Analyzer a) {												// D
		threadPool.execute(new Job(doc, null, a));													// D
	}

	@Override
	public void updateDocument(Term term, Document doc) {											// D
		threadPool.execute(new Job(doc, term, defaultAnalyzer));									// D
	}

	@Override
	public void updateDocument(Term term, Document doc, Analyzer a) {								// D
		threadPool.execute(new Job(doc, term, a));													// D
	}

	@Override
	public void close() throws CorruptIndexException, IOException {
		finish();
		super.close();
	}

	@Override
	public void close(boolean doWait) throws CorruptIndexException, IOException {
		finish();
		super.close(doWait);
	}

	@Override
	public void rollback() throws CorruptIndexException, IOException {
		finish();
		super.rollback();
	}

	private void finish() {																			// E

		threadPool.shutdown();

		while (true) {
			try {
				if (threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
					break;
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(ie);
			}
		}
	}
}

/*
#A Holds one document to be added
#B Does real work to add or update document
#C Create thread pool
#D Have thread pool execute job
#E Shuts down thread pool
*/
