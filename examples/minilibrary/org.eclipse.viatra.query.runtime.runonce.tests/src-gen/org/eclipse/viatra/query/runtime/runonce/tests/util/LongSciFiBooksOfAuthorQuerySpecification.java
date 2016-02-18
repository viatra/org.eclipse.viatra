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
import org.eclipse.viatra.query.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.viatra.query.runtime.matchers.psystem.IValueProvider;
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.runonce.tests.LongSciFiBooksOfAuthorMatch;
import org.eclipse.viatra.query.runtime.runonce.tests.LongSciFiBooksOfAuthorMatcher;

/**
 * A pattern-specific query specification that can instantiate LongSciFiBooksOfAuthorMatcher in a type-safe way.
 * 
 * @see LongSciFiBooksOfAuthorMatcher
 * @see LongSciFiBooksOfAuthorMatch
 * 
 */
@SuppressWarnings("all")
public final class LongSciFiBooksOfAuthorQuerySpecification extends BaseGeneratedEMFQuerySpecification<LongSciFiBooksOfAuthorMatcher> {
  private LongSciFiBooksOfAuthorQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws ViatraQueryException if the pattern definition could not be loaded
   * 
   */
  public static LongSciFiBooksOfAuthorQuerySpecification instance() throws ViatraQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected LongSciFiBooksOfAuthorMatcher instantiate(final ViatraQueryEngine engine) throws ViatraQueryException {
    return LongSciFiBooksOfAuthorMatcher.on(engine);
  }
  
  @Override
  public LongSciFiBooksOfAuthorMatch newEmptyMatch() {
    return LongSciFiBooksOfAuthorMatch.newEmptyMatch();
  }
  
  @Override
  public LongSciFiBooksOfAuthorMatch newMatch(final Object... parameters) {
    return LongSciFiBooksOfAuthorMatch.newMatch((org.eclipse.viatra.examples.library.Writer) parameters[0], (org.eclipse.viatra.examples.library.Book) parameters[1]);
  }
  
  /**
   * Inner class allowing the singleton instance of {@link LongSciFiBooksOfAuthorQuerySpecification} to be created 
   * 	<b>not</b> at the class load time of the outer class, 
   * 	but rather at the first call to {@link LongSciFiBooksOfAuthorQuerySpecification#instance()}.
   * 
   * <p> This workaround is required e.g. to support recursion.
   * 
   */
  private static class LazyHolder {
    private final static LongSciFiBooksOfAuthorQuerySpecification INSTANCE = new LongSciFiBooksOfAuthorQuerySpecification();
    
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
    private final static LongSciFiBooksOfAuthorQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.query.runtime.runonce.tests.longSciFiBooksOfAuthor";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("author","book");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("author", "org.eclipse.viatra.examples.library.Writer"),new PParameter("book", "org.eclipse.viatra.examples.library.Book"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_author = body.getOrCreateVariableByName("author");
      		PVariable var_book = body.getOrCreateVariableByName("book");
      		PVariable var_pages = body.getOrCreateVariableByName("pages");
      		new TypeConstraint(body, new FlatTuple(var_author), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Writer")));
      		new TypeConstraint(body, new FlatTuple(var_book), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Book")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_author, "author"),
      		   new ExportedParameter(body, var_book, "book")
      		));
      		// 	Writer.scifiBooks(author, book)
      		new TypeConstraint(body, new FlatTuple(var_author), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Writer")));
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		new TypeConstraint(body, new FlatTuple(var_author, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Writer", "scifiBooks")));
      		new Equality(body, var__virtual_0_, var_book);
      		// 	Book.pages(book, pages)
      		new TypeConstraint(body, new FlatTuple(var_book), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Book")));
      		PVariable var__virtual_1_ = body.getOrCreateVariableByName(".virtual{1}");
      		new TypeConstraint(body, new FlatTuple(var_book, var__virtual_1_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Book", "pages")));
      		new Equality(body, var__virtual_1_, var_pages);
      		// 	check(pages > 100)
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
      	// to silence compiler error
      	if (false) throw new ViatraQueryException("Never", "happens");
      } catch (ViatraQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
  
  private static boolean evaluateExpression_1_1(final Integer pages) {
    return ((pages).intValue() > 100);
  }
}
