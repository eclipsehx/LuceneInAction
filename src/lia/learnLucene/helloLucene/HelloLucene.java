package lia.learnLucene.helloLucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

/**
 * lucene建立索引并且搜索的步骤
 * @author eclipsehx
 */
public class HelloLucene {

	public static void main(String[] args) {

		System.out.println("开始建立索引...");

		long start = System.currentTimeMillis();
		index();
		long end = System.currentTimeMillis();

		System.out.println("索引共用时" + (end - start) + "毫秒！");

		search("lucene");
	}

	/**
	 * 建立索引
	 */
	public static void index() {

		IndexWriter writer = null;

		try {
			// 1.创建Directory
			Directory directory = FSDirectory.open(new File("./index/HelloLucene"));  // 打开索引位置,[Directory directory = new RAMDirectory();	// 索引建立在内存中]

			// 2.创建IndexWriter
			writer = new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_30), true, IndexWriter.MaxFieldLength.UNLIMITED);

			// 3.创建Document对象
			Document document = null;

			// 4.为Document添加Field
			File f = new File("./data/HelloLucene");

			for (File file : f.listFiles()) {
				document = new Document();
				document.add(new Field("content", new FileReader(file)));
				document.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
				document.add(new Field("path", file.getAbsolutePath(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));

				System.out.println("正在索引文件->" + file.getCanonicalPath());

				// 5.通过IndexWriter添加文档到索引中
				writer.addDocument(document);
			}

			System.out.print("索引建立完毕，共索引了" + writer.numDocs() + "个文件！");

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();  // 6.关闭writer
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 搜索
	 */
	public static void search(String queryStr) {

		IndexReader reader = null;
		IndexSearcher searcher = null;

		try {
			// 1.创建Directory
			Directory directory = FSDirectory.open(new File("./index/HelloLucene"));  // 打开索引目录

			// 2.创建IndexReader
			reader = IndexReader.open(directory);

			// 3.根据IndexReader创建IndexSearcher
			searcher = new IndexSearcher(reader);

			// 4.创建搜索的Query
			QueryParser parser = new QueryParser(Version.LUCENE_30, "content", new StandardAnalyzer(Version.LUCENE_30));  // 创建parser来确定要搜索文件的内容，第二个参数表示搜索的域
			Query query = parser.parse(queryStr);  // 创建query，表示搜索域为content中包含queryStr的文档

			// 5.根据searcher搜索并且返回TopDocs
			TopDocs tds = searcher.search(query, 10);

			// 6.根据TopDocs获取ScoreDoc对象
			ScoreDoc[] sds = tds.scoreDocs;

			for (ScoreDoc sd : sds) {
				// 7.根据searcher和ScoreDoc对象获取具体的Document对象
				Document document = searcher.doc(sd.doc);
				// 8.根据Document对象获取需要的值
				System.out.println(document.get("filename") + "->[" + document.get("path") + "]");
			}

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (searcher != null) {
				try {
					searcher.close();  // 9.关闭searcher
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (reader != null) {
				try {
					reader.close();	 // 9.关闭reader
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
