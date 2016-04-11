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
import org.eclipse.viatra.query.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.query.runtime.matchers.tuple.FlatTuple;
import org.eclipse.viatra.query.runtime.runonce.tests.SumOfPagesInLibraryMatch;
import org.eclipse.viatra.query.runtime.runonce.tests.SumOfPagesInLibraryMatcher;

/**
 * A pattern-specific query specification that can instantiate SumOfPagesInLibraryMatcher in a type-safe way.
 * 
 * @see SumOfPagesInLibraryMatcher
 * @see SumOfPagesInLibraryMatch
 * 
 */
@SuppressWarnings("all")
public final class SumOfPagesInLibraryQuerySpecification extends BaseGeneratedEMFQuerySpecification<SumOfPagesInLibraryMatcher> {
  private SumOfPagesInLibraryQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws ViatraQueryException if the pattern definition could not be loaded
   * 
   */
  public static SumOfPagesInLibraryQuerySpecification instance() throws ViatraQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected SumOfPagesInLibraryMatcher instantiate(final ViatraQueryEngine engine) throws ViatraQueryException {
    return SumOfPagesInLibraryMatcher.on(engine);
  }
  
  @Override
  public SumOfPagesInLibraryMatch newEmptyMatch() {
    return SumOfPagesInLibraryMatch.newEmptyMatch();
  }
  
  @Override
  public SumOfPagesInLibraryMatch newMatch(final Object... parameters) {
    return SumOfPagesInLibraryMatch.newMatch((org.eclipse.viatra.examples.library.Library) parameters[0], (java.lang.Integer) parameters[1]);
  }
  
  /**
   * Inner class allowing the singleton instance of {@link SumOfPagesInLibraryQuerySpecification} to be created 
   * 	<b>not</b> at the class load time of the outer class, 
   * 	but rather at the first call to {@link SumOfPagesInLibraryQuerySpecification#instance()}.
   * 
   * <p> This workaround is required e.g. to support recursion.
   * 
   */
  private static class LazyHolder {
    private final static SumOfPagesInLibraryQuerySpecification INSTANCE = new SumOfPagesInLibraryQuerySpecification();
    
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
    private final static SumOfPagesInLibraryQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.query.runtime.runonce.tests.sumOfPagesInLibrary";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("library","sumOfPages");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(
      			 new PParameter("library", "org.eclipse.viatra.examples.library.Library", new EClassTransitiveInstancesKey((EClass)getClassifierLiteralSafe("http://www.eclipse.org/viatra/examples/library/1.0", "Library"))),
      			 new PParameter("sumOfPages", "java.lang.Integer", null)
      			);
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_library = body.getOrCreateVariableByName("library");
      		PVariable var_sumOfPages = body.getOrCreateVariableByName("sumOfPages");
      		new TypeConstraint(body, new FlatTuple(var_library), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Library")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_library, "library"),
      		   new ExportedParameter(body, var_sumOfPages, "sumOfPages")
      		));
      		// 	Library.sumOfPages(library, sumOfPages)
      		new TypeConstraint(body, new FlatTuple(var_library), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Library")));
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		new TypeConstraint(body, new FlatTuple(var_library, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Library", "sumOfPages")));
      		new Equality(body, var__virtual_0_, var_sumOfPages);
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
