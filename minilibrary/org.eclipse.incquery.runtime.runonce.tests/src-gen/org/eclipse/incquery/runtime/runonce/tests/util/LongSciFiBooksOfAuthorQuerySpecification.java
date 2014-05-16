package org.eclipse.incquery.runtime.runonce.tests.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.psystem.IValueProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.runonce.tests.LongSciFiBooksOfAuthorMatcher;

/**
 * A pattern-specific query specification that can instantiate LongSciFiBooksOfAuthorMatcher in a type-safe way.
 * 
 * @see LongSciFiBooksOfAuthorMatcher
 * @see LongSciFiBooksOfAuthorMatch
 * 
 */
@SuppressWarnings("all")
public final class LongSciFiBooksOfAuthorQuerySpecification extends BaseGeneratedQuerySpecification<LongSciFiBooksOfAuthorMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static LongSciFiBooksOfAuthorQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected LongSciFiBooksOfAuthorMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return LongSciFiBooksOfAuthorMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.runtime.runonce.tests.longSciFiBooksOfAuthor";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("author","book");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("author", "org.eclipse.incquery.examples.eiqlibrary.Writer"),new PParameter("book", "org.eclipse.incquery.examples.eiqlibrary.Book"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_author = body.getOrCreateVariableByName("author");
      PVariable var_book = body.getOrCreateVariableByName("book");
      PVariable var_pages = body.getOrCreateVariableByName("pages");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_author, "author"), 
        new ExportedParameter(body, var_book, "book")
      ));
      
      
      new TypeBinary(body, CONTEXT, var_author, var_book, getFeatureLiteral("http:///org/incquery/examples/library/1.0", "Writer", "scifiBooks"), "http:///org/incquery/examples/library/1.0/Writer.scifiBooks");
      new TypeBinary(body, CONTEXT, var_book, var_pages, getFeatureLiteral("http:///org/incquery/examples/library/1.0", "Book", "pages"), "http:///org/incquery/examples/library/1.0/Book.pages");
      new ExpressionEvaluation(body, new IExpressionEvaluator() {
        @Override
        public String getShortDescription() {
        	return "Expression evaluation from pattern longSciFiBooksOfAuthor";
        }
        
        @Override
        public Iterable<String> getInputParameterNames() {
        	return Arrays.asList("pages");
        }
        
        @Override
        public Object evaluateExpression(IValueProvider provider) throws Exception {
        	java.lang.Integer pages = (java.lang.Integer) provider.getValue("pages");
        	return evaluateExpression_1_1(pages);
        }
        
        },  null); 
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static LongSciFiBooksOfAuthorQuerySpecification INSTANCE = make();
    
    public static LongSciFiBooksOfAuthorQuerySpecification make() {
      return new LongSciFiBooksOfAuthorQuerySpecification();					
      
    }
  }
  
  
  private boolean evaluateExpression_1_1(final Integer pages) {
    return ((pages).intValue() > 100);
  }
}
