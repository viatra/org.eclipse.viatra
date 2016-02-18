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
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody;
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicdeferred.PatternMatchCounter;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.runonce.tests.SingleAuthoredFirstBooksMatch;
import org.eclipse.viatra.query.runtime.runonce.tests.SingleAuthoredFirstBooksMatcher;
import org.eclipse.viatra.query.runtime.runonce.tests.util.BookAuthorsQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate SingleAuthoredFirstBooksMatcher in a type-safe way.
 * 
 * @see SingleAuthoredFirstBooksMatcher
 * @see SingleAuthoredFirstBooksMatch
 * 
 */
@SuppressWarnings("all")
public final class SingleAuthoredFirstBooksQuerySpecification extends BaseGeneratedEMFQuerySpecification<SingleAuthoredFirstBooksMatcher> {
  private SingleAuthoredFirstBooksQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws ViatraQueryException if the pattern definition could not be loaded
   * 
   */
  public static SingleAuthoredFirstBooksQuerySpecification instance() throws ViatraQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected SingleAuthoredFirstBooksMatcher instantiate(final ViatraQueryEngine engine) throws ViatraQueryException {
    return SingleAuthoredFirstBooksMatcher.on(engine);
  }
  
  @Override
  public SingleAuthoredFirstBooksMatch newEmptyMatch() {
    return SingleAuthoredFirstBooksMatch.newEmptyMatch();
  }
  
  @Override
  public SingleAuthoredFirstBooksMatch newMatch(final Object... parameters) {
    return SingleAuthoredFirstBooksMatch.newMatch((org.eclipse.viatra.examples.library.Library) parameters[0], (org.eclipse.viatra.examples.library.Book) parameters[1]);
  }
  
  /**
   * Inner class allowing the singleton instance of {@link SingleAuthoredFirstBooksQuerySpecification} to be created 
   * 	<b>not</b> at the class load time of the outer class, 
   * 	but rather at the first call to {@link SingleAuthoredFirstBooksQuerySpecification#instance()}.
   * 
   * <p> This workaround is required e.g. to support recursion.
   * 
   */
  private static class LazyHolder {
    private final static SingleAuthoredFirstBooksQuerySpecification INSTANCE = new SingleAuthoredFirstBooksQuerySpecification();
    
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
    private final static SingleAuthoredFirstBooksQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.query.runtime.runonce.tests.singleAuthoredFirstBooks";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("library","firstBook");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("library", "org.eclipse.viatra.examples.library.Library"),new PParameter("firstBook", "org.eclipse.viatra.examples.library.Book"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_library = body.getOrCreateVariableByName("library");
      		PVariable var_firstBook = body.getOrCreateVariableByName("firstBook");
      		PVariable var__author = body.getOrCreateVariableByName("_author");
      		new TypeConstraint(body, new FlatTuple(var_library), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Library")));
      		new TypeConstraint(body, new FlatTuple(var_firstBook), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Book")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_library, "library"),
      		   new ExportedParameter(body, var_firstBook, "firstBook")
      		));
      		// 	Library.writers.firstBook(library, firstBook)
      		new TypeConstraint(body, new FlatTuple(var_library), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Library")));
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		new TypeConstraint(body, new FlatTuple(var_library, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Library", "writers")));
      		PVariable var__virtual_1_ = body.getOrCreateVariableByName(".virtual{1}");
      		new TypeConstraint(body, new FlatTuple(var__virtual_0_, var__virtual_1_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Writer", "firstBook")));
      		new Equality(body, var__virtual_1_, var_firstBook);
      		// 	1 == count find bookAuthors(firstBook, _author)
      		PVariable var__virtual_2_ = body.getOrCreateVariableByName(".virtual{2}");
      		new ConstantValue(body, var__virtual_2_, 1);
      		PVariable var__virtual_3_ = body.getOrCreateVariableByName(".virtual{3}");
      		new PatternMatchCounter(body, new FlatTuple(var_firstBook, var__author), BookAuthorsQuerySpecification.instance().getInternalQueryRepresentation(), var__virtual_3_);
      		new Equality(body, var__virtual_2_, var__virtual_3_);
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
}
