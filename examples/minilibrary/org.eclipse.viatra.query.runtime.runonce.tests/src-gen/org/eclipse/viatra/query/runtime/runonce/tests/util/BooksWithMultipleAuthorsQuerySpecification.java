package org.eclipse.viatra.query.runtime.runonce.tests.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.viatra.query.runtime.matchers.psystem.IValueProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.runonce.tests.BooksWithMultipleAuthorsMatch;
import org.eclipse.viatra.query.runtime.runonce.tests.BooksWithMultipleAuthorsMatcher;
import org.eclipse.viatra.query.runtime.runonce.tests.util.BookAuthorsQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate BooksWithMultipleAuthorsMatcher in a type-safe way.
 * 
 * @see BooksWithMultipleAuthorsMatcher
 * @see BooksWithMultipleAuthorsMatch
 * 
 */
@SuppressWarnings("all")
public final class BooksWithMultipleAuthorsQuerySpecification extends BaseGeneratedEMFQuerySpecification<BooksWithMultipleAuthorsMatcher> {
  private BooksWithMultipleAuthorsQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static BooksWithMultipleAuthorsQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected BooksWithMultipleAuthorsMatcher instantiate(final ViatraQueryEngine engine) throws IncQueryException {
    return BooksWithMultipleAuthorsMatcher.on(engine);
  }
  
  @Override
  public BooksWithMultipleAuthorsMatch newEmptyMatch() {
    return BooksWithMultipleAuthorsMatch.newEmptyMatch();
  }
  
  @Override
  public BooksWithMultipleAuthorsMatch newMatch(final Object... parameters) {
    return BooksWithMultipleAuthorsMatch.newMatch((org.eclipse.viatra.examples.library.Book) parameters[0]);
  }
  
  /**
   * Inner class allowing the singleton instance of {@link BooksWithMultipleAuthorsQuerySpecification} to be created 
   * 	<b>not</b> at the class load time of the outer class, 
   * 	but rather at the first call to {@link BooksWithMultipleAuthorsQuerySpecification#instance()}.
   * 
   * <p> This workaround is required e.g. to support recursion.
   * 
   */
  private static class LazyHolder {
    private final static BooksWithMultipleAuthorsQuerySpecification INSTANCE = new BooksWithMultipleAuthorsQuerySpecification();
    
    /**
     * Statically initializes the query specification <b>after</b> the field {@link #INSTANCE} is assigned.
     * This initialization order is required to support indirect recursion.
     * 
     * <p> The static initializer is defined using a helper field to work around limitations of the code generator.
     * 
     */
    private final static Object STATIC_INITIALIZER = ensureInitialized();
    
    public static Object ensureInitialized() {
      INSTANCE.ensureInitializedInternalSneaky();
      return null;					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static BooksWithMultipleAuthorsQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.query.runtime.runonce.tests.booksWithMultipleAuthors";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("book");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("book", "org.eclipse.viatra.examples.library.Book"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_book = body.getOrCreateVariableByName("book");
      		PVariable var_numberOfBooks = body.getOrCreateVariableByName("numberOfBooks");
      		PVariable var__author = body.getOrCreateVariableByName("_author");
      		new TypeConstraint(body, new FlatTuple(var_book), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Book")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_book, "book")
      		));
      		// 		Book(book)
      		new TypeConstraint(body, new FlatTuple(var_book), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Book")));
      		// 		numberOfBooks == count find bookAuthors(book, _author)
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		new PatternMatchCounter(body, new FlatTuple(var_book, var__author), BookAuthorsQuerySpecification.instance().getInternalQueryRepresentation(), var__virtual_0_);
      		new Equality(body, var_numberOfBooks, var__virtual_0_);
      		// 		check(numberOfBooks > 1)
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
      	// to silence compiler error
      	if (false) throw new IncQueryException("Never", "happens");
      } catch (IncQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
  
  private static boolean evaluateExpression_1_1(final Integer numberOfBooks) {
    return ((numberOfBooks).intValue() > 1);
  }
}
