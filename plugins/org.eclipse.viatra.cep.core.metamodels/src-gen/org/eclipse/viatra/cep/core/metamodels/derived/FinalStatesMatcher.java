package org.eclipse.viatra.cep.core.metamodels.derived;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.derived.FinalStatesMatch;
import org.eclipse.viatra.cep.core.metamodels.derived.util.FinalStatesQuerySpecification;

/**
 * Generated pattern matcher API of the org.eclipse.viatra.cep.core.metamodels.derived.finalStates pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link FinalStatesMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * {@literal @}QueryBasedFeature
 * pattern finalStates(this : Automaton, finalState : FinalState){
 * 	Automaton.states(this, finalState);
 * }
 * </pre></code>
 * 
 * @see FinalStatesMatch
 * @see FinalStatesProcessor
 * @see FinalStatesQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class FinalStatesMatcher extends BaseMatcher<FinalStatesMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static FinalStatesMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    FinalStatesMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new FinalStatesMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_THIS = 0;
  
  private final static int POSITION_FINALSTATE = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(FinalStatesMatcher.class);
  
  /**
   * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet).
   * If a pattern matcher is already constructed with the same root, only a light-weight reference is returned.
   * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
   * The match set will be incrementally refreshed upon updates from this scope.
   * <p>The matcher will be created within the managed {@link IncQueryEngine} belonging to the EMF model root, so
   * multiple matchers will reuse the same engine and benefit from increased performance and reduced memory footprint.
   * @param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * @deprecated use {@link #on(IncQueryEngine)} instead, e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}
   * 
   */
  @Deprecated
  public FinalStatesMatcher(final Notifier emfRoot) throws IncQueryException {
    this(IncQueryEngine.on(emfRoot));
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * @deprecated use {@link #on(IncQueryEngine)} instead
   * 
   */
  @Deprecated
  public FinalStatesMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pFinalState the fixed value of pattern parameter finalState, or null if not bound.
   * @return matches represented as a FinalStatesMatch object.
   * 
   */
  public Collection<FinalStatesMatch> getAllMatches(final Automaton pThis, final FinalState pFinalState) {
    return rawGetAllMatches(new Object[]{pThis, pFinalState});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pFinalState the fixed value of pattern parameter finalState, or null if not bound.
   * @return a match represented as a FinalStatesMatch object, or null if no match is found.
   * 
   */
  public FinalStatesMatch getOneArbitraryMatch(final Automaton pThis, final FinalState pFinalState) {
    return rawGetOneArbitraryMatch(new Object[]{pThis, pFinalState});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pFinalState the fixed value of pattern parameter finalState, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Automaton pThis, final FinalState pFinalState) {
    return rawHasMatch(new Object[]{pThis, pFinalState});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pFinalState the fixed value of pattern parameter finalState, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Automaton pThis, final FinalState pFinalState) {
    return rawCountMatches(new Object[]{pThis, pFinalState});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pFinalState the fixed value of pattern parameter finalState, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Automaton pThis, final FinalState pFinalState, final IMatchProcessor<? super FinalStatesMatch> processor) {
    rawForEachMatch(new Object[]{pThis, pFinalState}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pFinalState the fixed value of pattern parameter finalState, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Automaton pThis, final FinalState pFinalState, final IMatchProcessor<? super FinalStatesMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pThis, pFinalState}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pFinalState the fixed value of pattern parameter finalState, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public FinalStatesMatch newMatch(final Automaton pThis, final FinalState pFinalState) {
    return FinalStatesMatch.newMatch(pThis, pFinalState);
  }
  
  /**
   * Retrieve the set of values that occur in matches for this.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Automaton> rawAccumulateAllValuesOfthis(final Object[] parameters) {
    Set<Automaton> results = new HashSet<Automaton>();
    rawAccumulateAllValues(POSITION_THIS, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for this.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Automaton> getAllValuesOfthis() {
    return rawAccumulateAllValuesOfthis(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for this.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Automaton> getAllValuesOfthis(final FinalStatesMatch partialMatch) {
    return rawAccumulateAllValuesOfthis(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for this.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Automaton> getAllValuesOfthis(final FinalState pFinalState) {
    return rawAccumulateAllValuesOfthis(new Object[]{
    null, 
    pFinalState
    });
  }
  
  /**
   * Retrieve the set of values that occur in matches for finalState.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<FinalState> rawAccumulateAllValuesOffinalState(final Object[] parameters) {
    Set<FinalState> results = new HashSet<FinalState>();
    rawAccumulateAllValues(POSITION_FINALSTATE, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for finalState.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<FinalState> getAllValuesOffinalState() {
    return rawAccumulateAllValuesOffinalState(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for finalState.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<FinalState> getAllValuesOffinalState(final FinalStatesMatch partialMatch) {
    return rawAccumulateAllValuesOffinalState(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for finalState.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<FinalState> getAllValuesOffinalState(final Automaton pThis) {
    return rawAccumulateAllValuesOffinalState(new Object[]{
    pThis, 
    null
    });
  }
  
  @Override
  protected FinalStatesMatch tupleToMatch(final Tuple t) {
    try {
    	return FinalStatesMatch.newMatch((org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) t.get(POSITION_THIS), (org.eclipse.viatra.cep.core.metamodels.automaton.FinalState) t.get(POSITION_FINALSTATE));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected FinalStatesMatch arrayToMatch(final Object[] match) {
    try {
    	return FinalStatesMatch.newMatch((org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) match[POSITION_THIS], (org.eclipse.viatra.cep.core.metamodels.automaton.FinalState) match[POSITION_FINALSTATE]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected FinalStatesMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return FinalStatesMatch.newMutableMatch((org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) match[POSITION_THIS], (org.eclipse.viatra.cep.core.metamodels.automaton.FinalState) match[POSITION_FINALSTATE]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<FinalStatesMatcher> querySpecification() throws IncQueryException {
    return FinalStatesQuerySpecification.instance();
  }
}
