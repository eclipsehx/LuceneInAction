package lia.tools.queryParser;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.messages.MessageImpl;
import org.apache.lucene.queryParser.core.QueryNodeException;
import org.apache.lucene.queryParser.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryParser.core.nodes.FieldQueryNode;
import org.apache.lucene.queryParser.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryParser.core.nodes.QueryNode;
import org.apache.lucene.queryParser.core.nodes.SlopQueryNode;
import org.apache.lucene.queryParser.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryParser.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryParser.core.processors.QueryNodeProcessorPipeline;
import org.apache.lucene.queryParser.standard.StandardQueryParser;
import org.apache.lucene.queryParser.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryParser.standard.nodes.WildcardQueryNode;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

// From chapter 9

public class CustomFlexibleQueryParser extends StandardQueryParser {

	public CustomFlexibleQueryParser(Analyzer analyzer) {

		super(analyzer);

		QueryNodeProcessorPipeline processors = (QueryNodeProcessorPipeline) getQueryNodeProcessor();
		processors.addProcessor(new NoFuzzyOrWildcardQueryProcessor());										// A

		QueryTreeBuilder builders = (QueryTreeBuilder) getQueryBuilder();									// B
		builders.setBuilder(TokenizedPhraseQueryNode.class, new SpanNearPhraseQueryBuilder());				// B
		builders.setBuilder(SlopQueryNode.class, new SlopQueryNodeBuilder());								// B
	}

	private final class NoFuzzyOrWildcardQueryProcessor extends QueryNodeProcessorImpl {

		@Override
		protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {

			if (node instanceof FuzzyQueryNode || node instanceof WildcardQueryNode) {						// C
				throw new QueryNodeException(new MessageImpl("no"));
			}

			return node;
		}

		@Override
		protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
			return node;
		}

		@Override
		protected List<QueryNode> setChildrenOrder(List<QueryNode> children) {
			return children;
		}
	}

	private class SpanNearPhraseQueryBuilder implements StandardQueryBuilder {

		@Override
		public Query build(QueryNode queryNode) throws QueryNodeException {

			TokenizedPhraseQueryNode phraseNode = (TokenizedPhraseQueryNode) queryNode;
			PhraseQuery phraseQuery = new PhraseQuery();

			List<QueryNode> children = phraseNode.getChildren();											// D

			SpanTermQuery[] clauses;

			if (children != null) {

				int numTerms = children.size();

				clauses = new SpanTermQuery[numTerms];

				for (int i = 0; i < numTerms; i++) {

					FieldQueryNode termNode = (FieldQueryNode) children.get(i);
					TermQuery termQuery = (TermQuery) termNode.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
					clauses[i] = new SpanTermQuery(termQuery.getTerm());
				}

			} else {
				clauses = new SpanTermQuery[0];
			}

			return new SpanNearQuery(clauses, phraseQuery.getSlop(), true);									// E
		}
	}

	public class SlopQueryNodeBuilder implements StandardQueryBuilder {										// F

		@Override
		public Query build(QueryNode queryNode) throws QueryNodeException {

			SlopQueryNode phraseSlopNode = (SlopQueryNode) queryNode;

			Query query = (Query) phraseSlopNode.getChild().getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);

			if (query instanceof PhraseQuery) {
				((PhraseQuery) query).setSlop(phraseSlopNode.getValue());
			} else if (query instanceof MultiPhraseQuery) {
				((MultiPhraseQuery) query).setSlop(phraseSlopNode.getValue());
			}

			return query;
		}
	}
}

/*
  #A Install our custom node processor
  #B Install our two custom query builders
  #C Prevent Fuzzy and Wildcard queries
  #D Pull all terms for phrase
  #E Create SpanNearQuery
  #F Override slop query node
*/
