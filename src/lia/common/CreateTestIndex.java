package lia.common;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

public class CreateTestIndex {

	public static Document getDocument(String rootDir, File file) throws IOException {

		Properties props = new Properties();
		props.load(new FileInputStream(file)); 	// 文件里的内容就是 key-value 形式的，这样来表示图书，又能直接读出 图书 的各个属性。

		Document doc = new Document();

		// category comes from relative path below the base directory
		String category = file.getParent().substring(rootDir.length()); 	// 1
		category = category.replace(File.separatorChar, '/');	 // 1

		String isbn = props.getProperty("isbn"); 		// 2
		String title = props.getProperty("title"); 		// 2
		String author = props.getProperty("author"); 	// 2
		String url = props.getProperty("url"); 			// 2
		String subject = props.getProperty("subject"); 	// 2
		String pubmonth = props.getProperty("pubmonth"); // 2

		System.out.println(title + "\n" + author + "\n" + subject + "\n" + pubmonth + "\n" + category + "\n---------");

		doc.add(new Field("isbn", isbn, Field.Store.YES, Field.Index.NOT_ANALYZED));			// 3
		doc.add(new Field("category", category, Field.Store.YES, Field.Index.NOT_ANALYZED));	// 3
		doc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS)); 	// 3
		doc.add(new Field("title2", title.toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS, Field.TermVector.WITH_POSITIONS_OFFSETS));	 // 3

		// split multiple authors into unique field instances
		String[] authors = author.split(",");	 // 3

		for (String authorStr : authors) { 	// 3
			doc.add(new Field("author", authorStr, Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS)); 	// 3
		}

		doc.add(new Field("url", url, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));	 // 3
		doc.add(new Field("subject", subject, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));	 // 3 //4

		doc.add(new NumericField("pubmonth", Field.Store.YES, true).setIntValue(Integer.parseInt(pubmonth)));	 // 3

		Date d;	 // 3
		try {	 // 3
			d = DateTools.stringToDate(pubmonth);	 // 3
		} catch (ParseException pe) {	 	// 3
			throw new RuntimeException(pe); // 3
		}

		doc.add(new NumericField("pubmonthAsDay").setIntValue((int) (d.getTime() / (1000 * 3600 * 24))));	 // 3

		for (String text : new String[] { title, subject, author, category }) {	 // 3

			doc.add(new Field("contents", text, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));	 // 3 // 5
		}

		return doc;
	}

	private static String aggregate(String[] strings) {

		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < strings.length; i++) {
			buffer.append(strings[i]);
			buffer.append(" ");
		}

		return buffer.toString();
	}

	/**
	 * 递归找到所有文件(以 .properties 结尾的文件)，并将它们加到列表中返回
	 * @param result 返回的结果列表：包含dir目录下的所有文件名以 .properties 结尾的文件
	 * @param dir 文件存放目录(里面有文件夹和文件，层次任意[会递归查找])
	 */
	private static void findFiles(List<File> result, File dir) {

		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".properties")) {
				result.add(file);	 // 加入到结果集
			} else if (file.isDirectory()) {
				findFiles(result, file);	 // 递归查找，这种递归很好呀，把结果单参数传入，直接改变该引用，递归设计的很好，很适合操作文件目录这种树形结构
			}
		}
	}

	private static class MyStandardAnalyzer extends StandardAnalyzer {	 // 6

		public MyStandardAnalyzer(Version matchVersion) {	 // 6
			super(matchVersion);	 // 6
		}

		@Override
		public int getPositionIncrementGap(String field) {	 // 6
			if (field.equals("contents")) {	 // 6
				return 100;	 // 6
			} else {
				return 0;
			}
		}
	}

	public static void main(String[] args) throws IOException {

		String dataDir = args[0];	 // 数据目录
		String indexDir = args[1];	 // 索引位置

		List<File> results = new ArrayList<File>();
		findFiles(results, new File(dataDir));		 // 找到数据目录下所有特定文件(.properties文件)

		System.out.println(results.size() + " books to index");

		Directory dir = FSDirectory.open(new File(indexDir));
		IndexWriter writer = new IndexWriter(dir, new MyStandardAnalyzer(Version.LUCENE_30), true, IndexWriter.MaxFieldLength.UNLIMITED);

		for (File file : results) {
			Document doc = getDocument(dataDir, file);
			writer.addDocument(doc);
		}

		writer.close();
		dir.close();
	}
}

/*
 * #1 Get category
 * #2 Pull fields
 * #3 Add fields to Document instance
 * #4 Flag subject field
 * #5 Add catch-all contents field
 * #6 Custom analyzer to override multi-valued position increment
 */
