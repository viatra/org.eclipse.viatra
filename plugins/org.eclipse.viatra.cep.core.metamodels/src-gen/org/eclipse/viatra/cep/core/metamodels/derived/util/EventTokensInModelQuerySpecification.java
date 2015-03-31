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
import org.eclipse.viatra.cep.core.metamodels.derived.EventTokensInModelMatch;
import org.eclipse.viatra.cep.core.metamodels.derived.EventTokensInModelMatcher;

/**
 * A pattern-specific query specification that can instantiate EventTokensInModelMatcher in a type-safe way.
 * 
 * @see EventTokensInModelMatcher
 * @see EventTokensInModelMatch
 * 
 */
@SuppressWarnings("all")
public final class EventTokensInModelQuerySpecification extends BaseGeneratedEMFQuerySpecification<EventTokensInModelMatcher> {
  private EventTokensInModelQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static EventTokensInModelQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected EventTokensInModelMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return EventTokensInModelMatcher.on(engine);
  }
  
  @Override
  public EventTokensInModelMatch newEmptyMatch() {
    return EventTokensInModelMatch.newEmptyMatch();
  }
  
  @Override
  public EventTokensInModelMatch newMatch(final Object... parameters) {
    return EventTokensInModelMatch.newMatch((org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel) parameters[0], (org.eclipse.viatra.cep.core.metamodels.automaton.EventToken) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static EventTokensInModelQuerySpecification INSTANCE = make();
    
    public static EventTokensInModelQuerySpecification make() {
      return new EventTokensInModelQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static EventTokensInModelQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "org.eclipse.viatra.cep.core.metamodels.derived.eventTokensInModel";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("this","eventToken");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("this", "org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel"),new PParameter("eventToken", "org.eclipse.viatra.cep.core.metamodels.automaton.EventToken"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      {
      	PBody body = new PBody(this);
      	PVariable var_this = body.getOrCreateVariableByName("this");
      	PVariable var_eventToken = body.getOrCreateVariableByName("eventToken");
      	PVariable var_automaton = body.getOrCreateVariableByName("automaton");
      	PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      	PVariable var__virtual_1_ = body.getOrCreateVariableByName(".virtual{1}");
      	body.setExportedParameters(Arrays.<ExportedParameter>asList(
      		new ExportedParameter(body, var_this, "this"),
      				
      		new ExportedParameter(body, var_eventToken, "eventToken")
      	));
      	new TypeUnary(body, var_this, getClassifierLiteral("automaton.meta", "InternalModel"), "automaton.meta/InternalModel");
      	new TypeUnary(body, var_eventToken, getClassifierLiteral("automaton.meta", "EventToken"), "automaton.meta/EventToken");
      	new TypeUnary(body, var_this, getClassifierLiteral("automaton.meta", "InternalModel"), "automaton.meta/InternalModel");
      	new TypeBinary(body, CONTEXT, var_this, var__virtual_0_, getFeatureLiteral("automaton.meta", "InternalModel", "automata"), "automaton.meta/InternalModel.automata");
      	new Equality(body, var__virtual_0_, var_automaton);
      	new TypeUnary(body, var_automaton, getClassifierLiteral("automaton.meta", "Automaton"), "automaton.meta/Automaton");
      	new TypeBinary(body, CONTEXT, var_automaton, var__virtual_1_, getFeatureLiteral("automaton.meta", "Automaton", "eventTokens"), "automaton.meta/Automaton.eventTokens");
      	new Equality(body, var__virtual_1_, var_eventToken);
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
