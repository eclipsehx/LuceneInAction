package lia.admin;

import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.store.Directory;

// From chapter 11

public class Fragments {

	public void test() throws Exception {

		Directory dir = null;
		Analyzer analyzer = null;
		// START
		IndexDeletionPolicy policy = new KeepOnlyLastCommitDeletionPolicy();
		SnapshotDeletionPolicy snapshotter = new SnapshotDeletionPolicy(policy);
		IndexWriter writer = new IndexWriter(dir, analyzer, snapshotter, IndexWriter.MaxFieldLength.UNLIMITED);
		// END

		try {
			IndexCommit commit = (IndexCommit) snapshotter.snapshot();
			Collection<String> fileNames = commit.getFileNames();
			/* <iterate over & copy files from fileNames> */
		} finally {
			snapshotter.release();
		}
	}
}
