package headless;

import headless.EClassNamesKeywordMatch;
import headless.util.EClassNamesKeywordQuerySpecification;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;

/**
 * Generated pattern matcher API of the headless.eClassNamesKeyword pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link EClassNamesKeywordMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern eClassNamesKeyword(c: EClass, n: EString)
 * {
 * 	EClass.name(c,n);
 * 	check("A" == n);
 * }
 * </pre></code>
 * 
 * @see EClassNamesKeywordMatch
 * @see EClassNamesKeywordProcessor
 * @see EClassNamesKeywordQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class EClassNamesKeywordMatcher extends BaseMatcher<EClassNamesKeywordMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<EClassNamesKeywordMatcher> querySpecification() throws IncQueryException {
    return EClassNamesKeywordQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static EClassNamesKeywordMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    EClassNamesKeywordMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new EClassNamesKeywordMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_C = 0;
  
  private final static int POSITION_N = 1;
  
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
  public EClassNamesKeywordMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public EClassNamesKeywordMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @return matches represented as a EClassNamesKeywordMatch object.
   * 
   */
  public Collection<EClassNamesKeywordMatch> getAllMatches(final EClass pC, final String pN) {
    return rawGetAllMatches(new Object[]{pC, pN});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @return a match represented as a EClassNamesKeywordMatch object, or null if no match is found.
   * 
   */
  public EClassNamesKeywordMatch getOneArbitraryMatch(final EClass pC, final String pN) {
    return rawGetOneArbitraryMatch(new Object[]{pC, pN});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final EClass pC, final String pN) {
    return rawHasMatch(new Object[]{pC, pN});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final EClass pC, final String pN) {
    return rawCountMatches(new Object[]{pC, pN});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final EClass pC, final String pN, final IMatchProcessor<? super EClassNamesKeywordMatch> processor) {
    rawForEachMatch(new Object[]{pC, pN}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final EClass pC, final String pN, final IMatchProcessor<? super EClassNamesKeywordMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pC, pN}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<EClassNamesKeywordMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final EClass pC, final String pN) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pC, pN});
  }
  
  /**
   * Returns a new (partial) Match object for the matcher.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public EClassNamesKeywordMatch newMatch(final EClass pC, final String pN) {
    return new EClassNamesKeywordMatch.Immutable(pC, pN);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for c.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<EClass> rawAccumulateAllValuesOfc(final Object[] parameters) {
    Set<EClass> results = new HashSet<EClass>();
    rawAccumulateAllValues(POSITION_C, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for c.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EClass> getAllValuesOfc() {
    return rawAccumulateAllValuesOfc(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for c.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EClass> getAllValuesOfc(final EClassNamesKeywordMatch partialMatch) {
    return rawAccumulateAllValuesOfc(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for c.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EClass> getAllValuesOfc(final String pN) {
    return rawAccumulateAllValuesOfc(new Object[]{null, pN});
  }
  
  /**
   * Retrieve the set of values that occur in matches for n.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<String> rawAccumulateAllValuesOfn(final Object[] parameters) {
    Set<String> results = new HashSet<String>();
    rawAccumulateAllValues(POSITION_N, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for n.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfn() {
    return rawAccumulateAllValuesOfn(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for n.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfn(final EClassNamesKeywordMatch partialMatch) {
    return rawAccumulateAllValuesOfn(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for n.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<String> getAllValuesOfn(final EClass pC) {
    return rawAccumulateAllValuesOfn(new Object[]{pC, null});
  }
  
  @Override
  protected EClassNamesKeywordMatch tupleToMatch(final Tuple t) {
    try {
    	return new EClassNamesKeywordMatch.Immutable((org.eclipse.emf.ecore.EClass) t.get(POSITION_C), (java.lang.String) t.get(POSITION_N));
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in tuple not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  protected EClassNamesKeywordMatch arrayToMatch(final Object[] match) {
    try {
    	return new EClassNamesKeywordMatch.Immutable((org.eclipse.emf.ecore.EClass) match[POSITION_C], (java.lang.String) match[POSITION_N]);
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  protected EClassNamesKeywordMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return new EClassNamesKeywordMatch.Mutable((org.eclipse.emf.ecore.EClass) match[POSITION_C], (java.lang.String) match[POSITION_N]);
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
}
