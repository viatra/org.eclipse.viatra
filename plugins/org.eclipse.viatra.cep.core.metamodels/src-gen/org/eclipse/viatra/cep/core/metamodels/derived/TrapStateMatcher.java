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
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState;
import org.eclipse.viatra.cep.core.metamodels.derived.TrapStateMatch;
import org.eclipse.viatra.cep.core.metamodels.derived.util.TrapStateQuerySpecification;

/**
 * Generated pattern matcher API of the org.eclipse.viatra.cep.core.metamodels.derived.trapState pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link TrapStateMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * {@literal @}QueryBasedFeature
 * pattern trapState(this : Automaton, trapState : TrapState){
 * 	Automaton.states(this, trapState);
 * }
 * </pre></code>
 * 
 * @see TrapStateMatch
 * @see TrapStateProcessor
 * @see TrapStateQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class TrapStateMatcher extends BaseMatcher<TrapStateMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static TrapStateMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    TrapStateMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new TrapStateMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_THIS = 0;
  
  private final static int POSITION_TRAPSTATE = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(TrapStateMatcher.class);
  
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
  public TrapStateMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public TrapStateMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pTrapState the fixed value of pattern parameter trapState, or null if not bound.
   * @return matches represented as a TrapStateMatch object.
   * 
   */
  public Collection<TrapStateMatch> getAllMatches(final Automaton pThis, final TrapState pTrapState) {
    return rawGetAllMatches(new Object[]{pThis, pTrapState});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pTrapState the fixed value of pattern parameter trapState, or null if not bound.
   * @return a match represented as a TrapStateMatch object, or null if no match is found.
   * 
   */
  public TrapStateMatch getOneArbitraryMatch(final Automaton pThis, final TrapState pTrapState) {
    return rawGetOneArbitraryMatch(new Object[]{pThis, pTrapState});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pTrapState the fixed value of pattern parameter trapState, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Automaton pThis, final TrapState pTrapState) {
    return rawHasMatch(new Object[]{pThis, pTrapState});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pTrapState the fixed value of pattern parameter trapState, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Automaton pThis, final TrapState pTrapState) {
    return rawCountMatches(new Object[]{pThis, pTrapState});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pTrapState the fixed value of pattern parameter trapState, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Automaton pThis, final TrapState pTrapState, final IMatchProcessor<? super TrapStateMatch> processor) {
    rawForEachMatch(new Object[]{pThis, pTrapState}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pTrapState the fixed value of pattern parameter trapState, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Automaton pThis, final TrapState pTrapState, final IMatchProcessor<? super TrapStateMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pThis, pTrapState}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pTrapState the fixed value of pattern parameter trapState, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public TrapStateMatch newMatch(final Automaton pThis, final TrapState pTrapState) {
    return TrapStateMatch.newMatch(pThis, pTrapState);
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
  public Set<Automaton> getAllValuesOfthis(final TrapStateMatch partialMatch) {
    return rawAccumulateAllValuesOfthis(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for this.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Automaton> getAllValuesOfthis(final TrapState pTrapState) {
    return rawAccumulateAllValuesOfthis(new Object[]{
    null, 
    pTrapState
    });
  }
  
  /**
   * Retrieve the set of values that occur in matches for trapState.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<TrapState> rawAccumulateAllValuesOftrapState(final Object[] parameters) {
    Set<TrapState> results = new HashSet<TrapState>();
    rawAccumulateAllValues(POSITION_TRAPSTATE, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for trapState.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<TrapState> getAllValuesOftrapState() {
    return rawAccumulateAllValuesOftrapState(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for trapState.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<TrapState> getAllValuesOftrapState(final TrapStateMatch partialMatch) {
    return rawAccumulateAllValuesOftrapState(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for trapState.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<TrapState> getAllValuesOftrapState(final Automaton pThis) {
    return rawAccumulateAllValuesOftrapState(new Object[]{
    pThis, 
    null
    });
  }
  
  @Override
  protected TrapStateMatch tupleToMatch(final Tuple t) {
    try {
    	return TrapStateMatch.newMatch((org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) t.get(POSITION_THIS), (org.eclipse.viatra.cep.core.metamodels.automaton.TrapState) t.get(POSITION_TRAPSTATE));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected TrapStateMatch arrayToMatch(final Object[] match) {
    try {
    	return TrapStateMatch.newMatch((org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) match[POSITION_THIS], (org.eclipse.viatra.cep.core.metamodels.automaton.TrapState) match[POSITION_TRAPSTATE]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected TrapStateMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return TrapStateMatch.newMutableMatch((org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) match[POSITION_THIS], (org.eclipse.viatra.cep.core.metamodels.automaton.TrapState) match[POSITION_TRAPSTATE]);
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
  public static IQuerySpecification<TrapStateMatcher> querySpecification() throws IncQueryException {
    return TrapStateQuerySpecification.instance();
  }
}
