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
import org.eclipse.viatra.query.runtime.runonce.tests.RequestCountOfLibraryMatch;
import org.eclipse.viatra.query.runtime.runonce.tests.RequestCountOfLibraryMatcher;

/**
 * A pattern-specific query specification that can instantiate RequestCountOfLibraryMatcher in a type-safe way.
 * 
 * @see RequestCountOfLibraryMatcher
 * @see RequestCountOfLibraryMatch
 * 
 */
@SuppressWarnings("all")
public final class RequestCountOfLibraryQuerySpecification extends BaseGeneratedEMFQuerySpecification<RequestCountOfLibraryMatcher> {
  private RequestCountOfLibraryQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws ViatraQueryException if the pattern definition could not be loaded
   * 
   */
  public static RequestCountOfLibraryQuerySpecification instance() throws ViatraQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected RequestCountOfLibraryMatcher instantiate(final ViatraQueryEngine engine) throws ViatraQueryException {
    return RequestCountOfLibraryMatcher.on(engine);
  }
  
  @Override
  public RequestCountOfLibraryMatch newEmptyMatch() {
    return RequestCountOfLibraryMatch.newEmptyMatch();
  }
  
  @Override
  public RequestCountOfLibraryMatch newMatch(final Object... parameters) {
    return RequestCountOfLibraryMatch.newMatch((org.eclipse.viatra.examples.library.Library) parameters[0], (java.lang.Integer) parameters[1]);
  }
  
  /**
   * Inner class allowing the singleton instance of {@link RequestCountOfLibraryQuerySpecification} to be created 
   * 	<b>not</b> at the class load time of the outer class, 
   * 	but rather at the first call to {@link RequestCountOfLibraryQuerySpecification#instance()}.
   * 
   * <p> This workaround is required e.g. to support recursion.
   * 
   */
  private static class LazyHolder {
    private final static RequestCountOfLibraryQuerySpecification INSTANCE = new RequestCountOfLibraryQuerySpecification();
    
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
    private final static RequestCountOfLibraryQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.query.runtime.runonce.tests.requestCountOfLibrary";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("library","reqCount");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("library", "org.eclipse.viatra.examples.library.Library"),new PParameter("reqCount", "java.lang.Integer"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_library = body.getOrCreateVariableByName("library");
      		PVariable var_reqCount = body.getOrCreateVariableByName("reqCount");
      		new TypeConstraint(body, new FlatTuple(var_library), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Library")));
      		body.setSymbolicParameters(Arrays.<ExportedParameter>asList(
      		   new ExportedParameter(body, var_library, "library"),
      		   new ExportedParameter(body, var_reqCount, "reqCount")
      		));
      		// 	Library.requestCount(library, reqCount)
      		new TypeConstraint(body, new FlatTuple(var_library), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Library")));
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		new TypeConstraint(body, new FlatTuple(var_library, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://www.eclipse.org/viatra/examples/library/1.0", "Library", "requestCount")));
      		new Equality(body, var__virtual_0_, var_reqCount);
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
