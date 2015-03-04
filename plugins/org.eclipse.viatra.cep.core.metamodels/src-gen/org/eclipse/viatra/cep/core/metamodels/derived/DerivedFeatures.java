package org.eclipse.viatra.cep.core.metamodels.derived;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.cep.core.metamodels.derived.EventTokensInModelMatcher;
import org.eclipse.viatra.cep.core.metamodels.derived.FinalStatesMatcher;
import org.eclipse.viatra.cep.core.metamodels.derived.InitialStateMatcher;
import org.eclipse.viatra.cep.core.metamodels.derived.TrapStateMatcher;
import org.eclipse.viatra.cep.core.metamodels.derived.util.EventTokensInModelQuerySpecification;
import org.eclipse.viatra.cep.core.metamodels.derived.util.FinalStatesQuerySpecification;
import org.eclipse.viatra.cep.core.metamodels.derived.util.InitialStateQuerySpecification;
import org.eclipse.viatra.cep.core.metamodels.derived.util.TrapStateQuerySpecification;

/**
 * A pattern group formed of all patterns defined in derivedFeatures.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file derivedFeatures.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package org.eclipse.viatra.cep.core.metamodels.derived, the group contains the definition of the following patterns: <ul>
 * <li>initialState</li>
 * <li>finalStates</li>
 * <li>trapState</li>
 * <li>eventTokensInModel</li>
 * </ul>
 * 
 * @see IPatternGroup
 * 
 */
@SuppressWarnings("all")
public final class DerivedFeatures extends BaseGeneratedPatternGroup {
  /**
   * Access the pattern group.
   * 
   * @return the singleton instance of the group
   * @throws IncQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static DerivedFeatures instance() throws IncQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new DerivedFeatures();
    }
    return INSTANCE;
  }
  
  private static DerivedFeatures INSTANCE;
  
  private DerivedFeatures() throws IncQueryException {
    querySpecifications.add(InitialStateQuerySpecification.instance());
    querySpecifications.add(FinalStatesQuerySpecification.instance());
    querySpecifications.add(TrapStateQuerySpecification.instance());
    querySpecifications.add(EventTokensInModelQuerySpecification.instance());
  }
  
  public InitialStateQuerySpecification getInitialState() throws IncQueryException {
    return InitialStateQuerySpecification.instance();
  }
  
  public InitialStateMatcher getInitialState(final IncQueryEngine engine) throws IncQueryException {
    return InitialStateMatcher.on(engine);
  }
  
  public FinalStatesQuerySpecification getFinalStates() throws IncQueryException {
    return FinalStatesQuerySpecification.instance();
  }
  
  public FinalStatesMatcher getFinalStates(final IncQueryEngine engine) throws IncQueryException {
    return FinalStatesMatcher.on(engine);
  }
  
  public TrapStateQuerySpecification getTrapState() throws IncQueryException {
    return TrapStateQuerySpecification.instance();
  }
  
  public TrapStateMatcher getTrapState(final IncQueryEngine engine) throws IncQueryException {
    return TrapStateMatcher.on(engine);
  }
  
  public EventTokensInModelQuerySpecification getEventTokensInModel() throws IncQueryException {
    return EventTokensInModelQuerySpecification.instance();
  }
  
  public EventTokensInModelMatcher getEventTokensInModel(final IncQueryEngine engine) throws IncQueryException {
    return EventTokensInModelMatcher.on(engine);
  }
}
