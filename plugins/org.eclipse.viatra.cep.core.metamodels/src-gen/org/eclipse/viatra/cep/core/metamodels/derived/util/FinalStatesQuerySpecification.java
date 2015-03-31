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
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.viatra.cep.core.metamodels.derived.FinalStatesMatch;
import org.eclipse.viatra.cep.core.metamodels.derived.FinalStatesMatcher;

/**
 * A pattern-specific query specification that can instantiate FinalStatesMatcher in a type-safe way.
 * 
 * @see FinalStatesMatcher
 * @see FinalStatesMatch
 * 
 */
@SuppressWarnings("all")
public final class FinalStatesQuerySpecification extends BaseGeneratedEMFQuerySpecification<FinalStatesMatcher> {
  private FinalStatesQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static FinalStatesQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected FinalStatesMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return FinalStatesMatcher.on(engine);
  }
  
  @Override
  public FinalStatesMatch newEmptyMatch() {
    return FinalStatesMatch.newEmptyMatch();
  }
  
  @Override
  public FinalStatesMatch newMatch(final Object... parameters) {
    return FinalStatesMatch.newMatch((org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) parameters[0], (org.eclipse.viatra.cep.core.metamodels.automaton.FinalState) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static FinalStatesQuerySpecification INSTANCE = make();
    
    public static FinalStatesQuerySpecification make() {
      return new FinalStatesQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static FinalStatesQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.cep.core.metamodels.derived.finalStates";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("this","finalState");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("this", "org.eclipse.viatra.cep.core.metamodels.automaton.Automaton"),new PParameter("finalState", "org.eclipse.viatra.cep.core.metamodels.automaton.FinalState"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_this = body.getOrCreateVariableByName("this");
      	PVariable var_finalState = body.getOrCreateVariableByName("finalState");
      	PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_this, "this"),
      				
      		new ExportedParameter(body, var_finalState, "finalState")
      	));
      	new TypeUnary(body, var_this, getClassifierLiteral("automaton.meta", "Automaton"), "automaton.meta/Automaton");
      	new TypeUnary(body, var_finalState, getClassifierLiteral("automaton.meta", "FinalState"), "automaton.meta/FinalState");
      	new TypeUnary(body, var_this, getClassifierLiteral("automaton.meta", "Automaton"), "automaton.meta/Automaton");
      	new TypeBinary(body, CONTEXT, var_this, var__virtual_0_, getFeatureLiteral("automaton.meta", "Automaton", "states"), "automaton.meta/Automaton.states");
      	new Equality(body, var__virtual_0_, var_finalState);
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
