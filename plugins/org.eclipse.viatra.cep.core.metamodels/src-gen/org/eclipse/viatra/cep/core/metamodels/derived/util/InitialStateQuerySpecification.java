package org.eclipse.viatra.cep.core.metamodels.derived.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.cep.core.metamodels.derived.InitialStateMatch;
import org.eclipse.viatra.cep.core.metamodels.derived.InitialStateMatcher;

/**
 * A pattern-specific query specification that can instantiate InitialStateMatcher in a type-safe way.
 * 
 * @see InitialStateMatcher
 * @see InitialStateMatch
 * 
 */
@SuppressWarnings("all")
public final class InitialStateQuerySpecification extends BaseGeneratedEMFQuerySpecification<InitialStateMatcher> {
  private InitialStateQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static InitialStateQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected InitialStateMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return InitialStateMatcher.on(engine);
  }
  
  @Override
  public InitialStateMatch newEmptyMatch() {
    return InitialStateMatch.newEmptyMatch();
  }
  
  @Override
  public InitialStateMatch newMatch(final Object... parameters) {
    return InitialStateMatch.newMatch((org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) parameters[0], (org.eclipse.viatra.cep.core.metamodels.automaton.InitState) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static InitialStateQuerySpecification INSTANCE = make();
    
    public static InitialStateQuerySpecification make() {
      return new InitialStateQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static InitialStateQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.cep.core.metamodels.derived.initialState";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("this","initState");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("this", "org.eclipse.viatra.cep.core.metamodels.automaton.Automaton"),new PParameter("initState", "org.eclipse.viatra.cep.core.metamodels.automaton.InitState"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_this = body.getOrCreateVariableByName("this");
      	PVariable var_initState = body.getOrCreateVariableByName("initState");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_this, "this"),
      				
      		new ExportedParameter(body, var_initState, "initState")
      	));
      	new TypeUnary(body, var_initState, getClassifierLiteral("automaton.meta", "InitState"), "automaton.meta/InitState");
      	new TypeBinary(body, CONTEXT, var_this, var_initState, getFeatureLiteral("automaton.meta", "Automaton", "states"), "automaton.meta/Automaton.states");
      	bodies.add(body);
      }
      	{
      	PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      	addAnnotation(annotation);
      }
      	// to silence compiler error
      	if (false) throw new IncQueryException("Never", "happens");
      } catch (IncQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
}
