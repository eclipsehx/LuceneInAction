package lia.analysis.codec;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.commons.codec.language.Metaphone;

import java.io.IOException;

// From chapter 4

public class MetaphoneReplacementFilter extends TokenFilter {

	public static final String METAPHONE = "metaphone";

	private Metaphone metaphoner = new Metaphone();
	private TermAttribute termAttr;
	private TypeAttribute typeAttr;

	public MetaphoneReplacementFilter(TokenStream input) {
		super(input);
		termAttr = addAttribute(TermAttribute.class);
		typeAttr = addAttribute(TypeAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {

		if (!input.incrementToken())	// #A
			return false;	// #A

		String encoded;
		encoded = metaphoner.encode(termAttr.term());	// #B
		termAttr.setTermBuffer(encoded);	// #C
		typeAttr.setType(METAPHONE);		// #D

		return true;
	}
}

/*
 * #A Advance to next token
 * #B Convert to Metaphone encoding
 * #C Overwrite with encoded text
 * #D Set token type
 */
