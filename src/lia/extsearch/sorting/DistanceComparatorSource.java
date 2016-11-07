package lia.extsearch.sorting;

import org.apache.lucene.search.SortField;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;

// From chapter 6

public class DistanceComparatorSource extends FieldComparatorSource {	// #1  Extend FieldComparatorSource

	private int x;
	private int y;

	public DistanceComparatorSource(int x, int y) {		// #2  Give constructor base location
		this.x = x;
		this.y = y;
	}

	@Override
	public FieldComparator newComparator(String fieldName, int numHits, int sortPos, boolean reversed) throws IOException {		// #3  Create comparator
		return new DistanceScoreDocLookupComparator(fieldName, numHits);
	}

	private class DistanceScoreDocLookupComparator extends FieldComparator {	// #4  FieldComparator implementation

		private int[] xDoc, yDoc;	// #5  Array of x, y per document
		private float[] values;		// #6  Distances for documents in the queue
		private float bottom;		// #7  Worst distance in the queue

		private String fieldName;

		public DistanceScoreDocLookupComparator(String fieldName, int numHits) throws IOException {
			this.values = new float[numHits];
			this.fieldName = fieldName;
		}

		@Override
		public void setNextReader(IndexReader reader, int docBase) throws IOException {
			xDoc = FieldCache.DEFAULT.getInts(reader, "x");		// #8  Get x, y values from field cache
			yDoc = FieldCache.DEFAULT.getInts(reader, "y");		// #8  Get x, y values from field cache
		}

		private float getDistance(int doc) {	// #9  Compute distance for one document
			int deltax = xDoc[doc] - x;			// #9
			int deltay = yDoc[doc] - y;			// #9
			return (float) Math.sqrt(deltax * deltax + deltay * deltay);	// #9
		}

		@Override
		public int compare(int slot1, int slot2) {	// #10  Compare two docs in the top N
			if (values[slot1] < values[slot2])
				return -1;	// #10
			if (values[slot1] > values[slot2])
				return 1;	// #10
			return 0;	// #10
		}

		@Override
		public void setBottom(int slot) {	// #11  Record worst scoring doc in the top N
			bottom = values[slot];
		}

		@Override
		public int compareBottom(int doc) {		// #12  Compare new doc to worst scoring doc

			float docDistance = getDistance(doc);

			if (bottom < docDistance)
				return -1;	// #12
			if (bottom > docDistance)
				return 1;	// #12
			return 0;		// #12
		}

		@Override
		public void copy(int slot, int doc) {	// #13  Insert new doc into top N
			values[slot] = getDistance(doc);	// #13
		}

		@Override
		public Comparable value(int slot) {		// #14  Extract value from top N
			return new Float(values[slot]);		// #14
		}

		public int sortType() {
			return SortField.CUSTOM;
		}
	}

	@Override
	public String toString() {
		return "Distance from (" + x + "," + y + ")";
	}
}

/*
#1  Extend FieldComparatorSource
#2  Give constructor base location
#3  Create comparator
#4  FieldComparator implementation
#5  Array of x, y per document
#6  Distances for documents in the queue
#7  Worst distance in the queue
#8  Get x, y values from field cache
#9  Compute distance for one document
#10 Compare two docs in the top N
#11 Record worst scoring doc in the top N
#12 Compare new doc to worst scoring doc
#13 Insert new doc into top N
#14 Extract value from top N
*/
