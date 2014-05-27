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
import org.eclipse.incquery.runtime.runonce.tests.SumOfPagesInLibraryMatch;
import org.eclipse.incquery.runtime.runonce.tests.SumOfPagesInLibraryMatcher;

/**
 * A pattern-specific query specification that can instantiate SumOfPagesInLibraryMatcher in a type-safe way.
 * 
 * @see SumOfPagesInLibraryMatcher
 * @see SumOfPagesInLibraryMatch
 * 
 */
@SuppressWarnings("all")
public final class SumOfPagesInLibraryQuerySpecification extends BaseGeneratedQuerySpecification<SumOfPagesInLibraryMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SumOfPagesInLibraryQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected SumOfPagesInLibraryMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return SumOfPagesInLibraryMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.runtime.runonce.tests.sumOfPagesInLibrary";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("library","sumOfPages");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("library", "org.eclipse.incquery.examples.eiqlibrary.Library"),new PParameter("sumOfPages", "java.lang.Integer"));
  }
  
  @Override
  public SumOfPagesInLibraryMatch newEmptyMatch() {
    return SumOfPagesInLibraryMatch.newEmptyMatch();
  }
  
  @Override
  public SumOfPagesInLibraryMatch newMatch(final Object... parameters) {
    return SumOfPagesInLibraryMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Library) parameters[0], (java.lang.Integer) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_library = body.getOrCreateVariableByName("library");
      PVariable var_sumOfPages = body.getOrCreateVariableByName("sumOfPages");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_library, "library"), 
        new ExportedParameter(body, var_sumOfPages, "sumOfPages")
      ));
      
      
      new TypeBinary(body, CONTEXT, var_library, var_sumOfPages, getFeatureLiteral("http:///org/incquery/examples/library/1.0", "Library", "sumOfPages"), "http:///org/incquery/examples/library/1.0/Library.sumOfPages");
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static SumOfPagesInLibraryQuerySpecification INSTANCE = make();
    
    public static SumOfPagesInLibraryQuerySpecification make() {
      return new SumOfPagesInLibraryQuerySpecification();					
      
    }
  }
  
}
