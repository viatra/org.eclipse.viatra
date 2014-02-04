package org.eclipse.incquery.examples.bpm.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.examples.bpm.queries.NextActivityMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.context.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;

/**
 * A pattern-specific query specification that can instantiate NextActivityMatcher in a type-safe way.
 * 
 * @see NextActivityMatcher
 * @see NextActivityMatch
 * 
 */
@SuppressWarnings("all")
public final class NextActivityQuerySpecification extends BaseGeneratedQuerySpecification<NextActivityMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static NextActivityQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected NextActivityMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return NextActivityMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.examples.bpm.queries.nextActivity";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Act","Next");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Act", "process.Activity"),new PParameter("Next", "process.Activity"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() {
    return bodies;
  }
  
  private NextActivityQuerySpecification() throws IncQueryException {
    super();
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    {
      PBody body = new PBody(this);
      PVariable var_Act = body.getOrCreateVariableByName("Act");
      PVariable var_Next = body.getOrCreateVariableByName("Next");
      new ExportedParameter(body, var_Act, "Act");
      new ExportedParameter(body, var_Next, "Next");
      new TypeBinary(body, context, var_Act, var_Next, getFeatureLiteral("http://process/1.0", "Activity", "next"), "http://process/1.0/Activity.next");
      body.setSymbolicParameters(Arrays.asList(var_Act, var_Next));
      bodies.add(body);
    }
    setStatus(PQueryStatus.OK);
  }
  
  private Set<PBody> bodies = Sets.newHashSet();;
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<NextActivityQuerySpecification> {
    @Override
    public NextActivityQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static NextActivityQuerySpecification INSTANCE = make();
    
    public static NextActivityQuerySpecification make() {
      try {
      	return new NextActivityQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
