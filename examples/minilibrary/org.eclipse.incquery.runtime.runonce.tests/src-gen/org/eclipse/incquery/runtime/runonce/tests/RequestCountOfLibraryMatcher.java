package org.eclipse.incquery.runtime.runonce.tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.examples.eiqlibrary.Library;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.runtime.runonce.tests.RequestCountOfLibraryMatch;
import org.eclipse.incquery.runtime.runonce.tests.util.RequestCountOfLibraryQuerySpecification;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * Generated pattern matcher API of the org.eclipse.incquery.runtime.runonce.tests.requestCountOfLibrary pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link RequestCountOfLibraryMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern requestCountOfLibrary(library, reqCount) {
 * 	Library.requestCount(library, reqCount);
 * }
 * </pre></code>
 * 
 * @see RequestCountOfLibraryMatch
 * @see RequestCountOfLibraryProcessor
 * @see RequestCountOfLibraryQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class RequestCountOfLibraryMatcher extends BaseMatcher<RequestCountOfLibraryMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<RequestCountOfLibraryMatcher> querySpecification() throws IncQueryException {
    return RequestCountOfLibraryQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static RequestCountOfLibraryMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    RequestCountOfLibraryMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new RequestCountOfLibraryMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_LIBRARY = 0;
  
  private final static int POSITION_REQCOUNT = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(RequestCountOfLibraryMatcher.class);
  
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
  public RequestCountOfLibraryMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public RequestCountOfLibraryMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pReqCount the fixed value of pattern parameter reqCount, or null if not bound.
   * @return matches represented as a RequestCountOfLibraryMatch object.
   * 
   */
  public Collection<RequestCountOfLibraryMatch> getAllMatches(final Library pLibrary, final Integer pReqCount) {
    return rawGetAllMatches(new Object[]{pLibrary, pReqCount});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pReqCount the fixed value of pattern parameter reqCount, or null if not bound.
   * @return a match represented as a RequestCountOfLibraryMatch object, or null if no match is found.
   * 
   */
  public RequestCountOfLibraryMatch getOneArbitraryMatch(final Library pLibrary, final Integer pReqCount) {
    return rawGetOneArbitraryMatch(new Object[]{pLibrary, pReqCount});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pReqCount the fixed value of pattern parameter reqCount, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Library pLibrary, final Integer pReqCount) {
    return rawHasMatch(new Object[]{pLibrary, pReqCount});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pReqCount the fixed value of pattern parameter reqCount, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Library pLibrary, final Integer pReqCount) {
    return rawCountMatches(new Object[]{pLibrary, pReqCount});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pReqCount the fixed value of pattern parameter reqCount, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Library pLibrary, final Integer pReqCount, final IMatchProcessor<? super RequestCountOfLibraryMatch> processor) {
    rawForEachMatch(new Object[]{pLibrary, pReqCount}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pReqCount the fixed value of pattern parameter reqCount, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Library pLibrary, final Integer pReqCount, final IMatchProcessor<? super RequestCountOfLibraryMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pLibrary, pReqCount}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pReqCount the fixed value of pattern parameter reqCount, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<RequestCountOfLibraryMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Library pLibrary, final Integer pReqCount) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pLibrary, pReqCount});
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pReqCount the fixed value of pattern parameter reqCount, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public RequestCountOfLibraryMatch newMatch(final Library pLibrary, final Integer pReqCount) {
    return RequestCountOfLibraryMatch.newMatch(pLibrary, pReqCount);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for library.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Library> rawAccumulateAllValuesOflibrary(final Object[] parameters) {
    Set<Library> results = new HashSet<Library>();
    rawAccumulateAllValues(POSITION_LIBRARY, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for library.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Library> getAllValuesOflibrary() {
    return rawAccumulateAllValuesOflibrary(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for library.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Library> getAllValuesOflibrary(final RequestCountOfLibraryMatch partialMatch) {
    return rawAccumulateAllValuesOflibrary(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for library.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Library> getAllValuesOflibrary(final Integer pReqCount) {
    return rawAccumulateAllValuesOflibrary(new Object[]{null, pReqCount});
  }
  
  /**
   * Retrieve the set of values that occur in matches for reqCount.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Integer> rawAccumulateAllValuesOfreqCount(final Object[] parameters) {
    Set<Integer> results = new HashSet<Integer>();
    rawAccumulateAllValues(POSITION_REQCOUNT, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for reqCount.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Integer> getAllValuesOfreqCount() {
    return rawAccumulateAllValuesOfreqCount(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for reqCount.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Integer> getAllValuesOfreqCount(final RequestCountOfLibraryMatch partialMatch) {
    return rawAccumulateAllValuesOfreqCount(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for reqCount.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Integer> getAllValuesOfreqCount(final Library pLibrary) {
    return rawAccumulateAllValuesOfreqCount(new Object[]{pLibrary, null});
  }
  
  @Override
  protected RequestCountOfLibraryMatch tupleToMatch(final Tuple t) {
    try {
      return RequestCountOfLibraryMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Library) t.get(POSITION_LIBRARY), (java.lang.Integer) t.get(POSITION_REQCOUNT));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected RequestCountOfLibraryMatch arrayToMatch(final Object[] match) {
    try {
      return RequestCountOfLibraryMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Library) match[POSITION_LIBRARY], (java.lang.Integer) match[POSITION_REQCOUNT]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected RequestCountOfLibraryMatch arrayToMatchMutable(final Object[] match) {
    try {
      return RequestCountOfLibraryMatch.newMutableMatch((org.eclipse.incquery.examples.eiqlibrary.Library) match[POSITION_LIBRARY], (java.lang.Integer) match[POSITION_REQCOUNT]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
