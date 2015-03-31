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
import org.eclipse.viatra.cep.core.metamodels.derived.TrapStateMatch;
import org.eclipse.viatra.cep.core.metamodels.derived.TrapStateMatcher;

/**
 * A pattern-specific query specification that can instantiate TrapStateMatcher in a type-safe way.
 * 
 * @see TrapStateMatcher
 * @see TrapStateMatch
 * 
 */
@SuppressWarnings("all")
public final class TrapStateQuerySpecification extends BaseGeneratedEMFQuerySpecification<TrapStateMatcher> {
  private TrapStateQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static TrapStateQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected TrapStateMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return TrapStateMatcher.on(engine);
  }
  
  @Override
  public TrapStateMatch newEmptyMatch() {
    return TrapStateMatch.newEmptyMatch();
  }
  
  @Override
  public TrapStateMatch newMatch(final Object... parameters) {
    return TrapStateMatch.newMatch((org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) parameters[0], (org.eclipse.viatra.cep.core.metamodels.automaton.TrapState) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static TrapStateQuerySpecification INSTANCE = make();
    
    public static TrapStateQuerySpecification make() {
      return new TrapStateQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static TrapStateQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.cep.core.metamodels.derived.trapState";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("this","trapState");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("this", "org.eclipse.viatra.cep.core.metamodels.automaton.Automaton"),new PParameter("trapState", "org.eclipse.viatra.cep.core.metamodels.automaton.TrapState"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_this = body.getOrCreateVariableByName("this");
      	PVariable var_trapState = body.getOrCreateVariableByName("trapState");
      	PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_this, "this"),
      				
      		new ExportedParameter(body, var_trapState, "trapState")
      	));
      	new TypeUnary(body, var_this, getClassifierLiteral("automaton.meta", "Automaton"), "automaton.meta/Automaton");
      	new TypeUnary(body, var_trapState, getClassifierLiteral("automaton.meta", "TrapState"), "automaton.meta/TrapState");
      	new TypeUnary(body, var_this, getClassifierLiteral("automaton.meta", "Automaton"), "automaton.meta/Automaton");
      	new TypeBinary(body, CONTEXT, var_this, var__virtual_0_, getFeatureLiteral("automaton.meta", "Automaton", "states"), "automaton.meta/Automaton.states");
      	new Equality(body, var__virtual_0_, var_trapState);
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
