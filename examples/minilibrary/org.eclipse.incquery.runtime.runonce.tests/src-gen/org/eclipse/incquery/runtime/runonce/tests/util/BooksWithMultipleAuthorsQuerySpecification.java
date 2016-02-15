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
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.runtime.runonce.tests.BooksWithMultipleAuthorsMatch;
import org.eclipse.incquery.runtime.runonce.tests.BooksWithMultipleAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.util.BookAuthorsQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate BooksWithMultipleAuthorsMatcher in a type-safe way.
 * 
 * @see BooksWithMultipleAuthorsMatcher
 * @see BooksWithMultipleAuthorsMatch
 * 
 */
@SuppressWarnings("all")
public final class BooksWithMultipleAuthorsQuerySpecification extends BaseGeneratedQuerySpecification<BooksWithMultipleAuthorsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static BooksWithMultipleAuthorsQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected BooksWithMultipleAuthorsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return BooksWithMultipleAuthorsMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.runtime.runonce.tests.booksWithMultipleAuthors";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("book");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("book", "org.eclipse.incquery.examples.eiqlibrary.Book"));
  }
  
  @Override
  public BooksWithMultipleAuthorsMatch newEmptyMatch() {
    return BooksWithMultipleAuthorsMatch.newEmptyMatch();
  }
  
  @Override
  public BooksWithMultipleAuthorsMatch newMatch(final Object... parameters) {
    return BooksWithMultipleAuthorsMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Book) parameters[0]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_book = body.getOrCreateVariableByName("book");
      PVariable var_numberOfBooks = body.getOrCreateVariableByName("numberOfBooks");
      PVariable var__author = body.getOrCreateVariableByName("_author");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_book, "book")
      ));
      
      new TypeUnary(body, var_book, getClassifierLiteral("http:///org/incquery/examples/library/1.0", "Book"), "http:///org/incquery/examples/library/1.0/Book");
      new PatternMatchCounter(body, new FlatTuple(var_book, var__author), BookAuthorsQuerySpecification.instance().instance(), var_numberOfBooks);
      new ExpressionEvaluation(body, new IExpressionEvaluator() {
        @Override
        public String getShortDescription() {
        	return "Expression evaluation from pattern booksWithMultipleAuthors";
        }
        
        @Override
        public Iterable<String> getInputParameterNames() {
        	return Arrays.asList("numberOfBooks");
        }
        
        @Override
        public Object evaluateExpression(IValueProvider provider) throws Exception {
        	java.lang.Integer numberOfBooks = (java.lang.Integer) provider.getValue("numberOfBooks");
        	return evaluateExpression_1_1(numberOfBooks);
        }
        
        },  null); 
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static BooksWithMultipleAuthorsQuerySpecification INSTANCE = make();
    
    public static BooksWithMultipleAuthorsQuerySpecification make() {
      return new BooksWithMultipleAuthorsQuerySpecification();					
      
    }
  }
  
  
  private boolean evaluateExpression_1_1(final Integer numberOfBooks) {
    return ((numberOfBooks).intValue() > 1);
  }
}
