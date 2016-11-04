package lia.analysis.codec;

import junit.framework.TestCase;

import org.apache.commons.codec.language.Metaphone;
import org.junit.Test;

// From chapter 4

public class CodecTest extends TestCase {

	@Test
	public void testMetaphone() throws Exception {
		Metaphone metaphoner = new Metaphone();
		assertEquals(metaphoner.encode("cute"), metaphoner.encode("cat"));
	}
}
