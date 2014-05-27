package org.eclipse.incquery.runtime.runonce.tests.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.runonce.tests.RequestCountOfLibraryMatch;
import org.eclipse.incquery.runtime.runonce.tests.RequestCountOfLibraryMatcher;

/**
 * A pattern-specific query specification that can instantiate RequestCountOfLibraryMatcher in a type-safe way.
 * 
 * @see RequestCountOfLibraryMatcher
 * @see RequestCountOfLibraryMatch
 * 
 */
@SuppressWarnings("all")
public final class RequestCountOfLibraryQuerySpecification extends BaseGeneratedQuerySpecification<RequestCountOfLibraryMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static RequestCountOfLibraryQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected RequestCountOfLibraryMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return RequestCountOfLibraryMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.runtime.runonce.tests.requestCountOfLibrary";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("library","reqCount");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("library", "org.eclipse.incquery.examples.eiqlibrary.Library"),new PParameter("reqCount", "java.lang.Integer"));
  }
  
  @Override
  public RequestCountOfLibraryMatch newEmptyMatch() {
    return RequestCountOfLibraryMatch.newEmptyMatch();
  }
  
  @Override
  public RequestCountOfLibraryMatch newMatch(final Object... parameters) {
    return RequestCountOfLibraryMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Library) parameters[0], (java.lang.Integer) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_library = body.getOrCreateVariableByName("library");
      PVariable var_reqCount = body.getOrCreateVariableByName("reqCount");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_library, "library"), 
        new ExportedParameter(body, var_reqCount, "reqCount")
      ));
      
      
      new TypeBinary(body, CONTEXT, var_library, var_reqCount, getFeatureLiteral("http:///org/incquery/examples/library/1.0", "Library", "requestCount"), "http:///org/incquery/examples/library/1.0/Library.requestCount");
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static RequestCountOfLibraryQuerySpecification INSTANCE = make();
    
    public static RequestCountOfLibraryQuerySpecification make() {
      return new RequestCountOfLibraryQuerySpecification();					
      
    }
  }
  
}
