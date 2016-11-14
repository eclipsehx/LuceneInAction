package lia.benchmark;

import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.benchmark.quality.*;
import org.apache.lucene.benchmark.quality.utils.*;
import org.apache.lucene.benchmark.quality.trec.*;

// From appendix C

/* This code was extracted from the Lucene
   contrib/benchmark sources */

public class PrecisionRecall {

	public static void main(String[] args) throws Throwable {

		File topicsFile = new File("src/lia/benchmark/topics.txt");
		File qrelsFile = new File("src/lia/benchmark/qrels.txt");

		Directory dir = FSDirectory.open(new File("index/meetlucene"));
		Searcher searcher = new IndexSearcher(dir, true);

		String docNameField = "filename";

		PrintWriter logger = new PrintWriter(System.out, true);

		TrecTopicsReader qReader = new TrecTopicsReader(); // #1
		QualityQuery qqs[] = qReader.readQueries(new BufferedReader(new FileReader(topicsFile))); // #1 Read TREC topics as QualityQuery[]

		Judge judge = new TrecJudge(new BufferedReader(new FileReader(qrelsFile))); // #2 Create Judge from TREC Qrel file

		judge.validateData(qqs, logger); // #3 Verify query and Judge match

		QualityQueryParser qqParser = new SimpleQQParser("title", "contents"); // #4 Create parser to translate queries into Lucene queries

		QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, docNameField);
		SubmissionReport submitLog = null;
		QualityStats stats[] = qrun.execute(judge, submitLog, logger); // #5 Run benchmark

		QualityStats avg = QualityStats.average(stats); // #6 Print precision and recall measures
		avg.log("SUMMARY", 2, logger, "  ");
		dir.close();
	}
}

/*
#1 Read TREC topics as QualityQuery[]
#2 Create Judge from TREC Qrel file
#3 Verify query and Judge match
#4 Create parser to translate queries into Lucene queries
#5 Run benchmark
#6 Print precision and recall measures
*/
