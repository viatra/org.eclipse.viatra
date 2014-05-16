package org.eclipse.incquery.runtime.runonce.tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.examples.eiqlibrary.Book;
import org.eclipse.incquery.examples.eiqlibrary.Library;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.runtime.runonce.tests.SingleAuthoredFirstBooksMatch;
import org.eclipse.incquery.runtime.runonce.tests.util.SingleAuthoredFirstBooksQuerySpecification;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * Generated pattern matcher API of the org.eclipse.incquery.runtime.runonce.tests.singleAuthoredFirstBooks pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link SingleAuthoredFirstBooksMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern singleAuthoredFirstBooks(library, firstBook) {
 * 	Library.writers.firstBook(library, firstBook);
 * 	1 == count find bookAuthors(firstBook, _author);
 * }
 * </pre></code>
 * 
 * @see SingleAuthoredFirstBooksMatch
 * @see SingleAuthoredFirstBooksProcessor
 * @see SingleAuthoredFirstBooksQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class SingleAuthoredFirstBooksMatcher extends BaseMatcher<SingleAuthoredFirstBooksMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<SingleAuthoredFirstBooksMatcher> querySpecification() throws IncQueryException {
    return SingleAuthoredFirstBooksQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static SingleAuthoredFirstBooksMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    SingleAuthoredFirstBooksMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new SingleAuthoredFirstBooksMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_LIBRARY = 0;
  
  private final static int POSITION_FIRSTBOOK = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(SingleAuthoredFirstBooksMatcher.class);
  
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
  public SingleAuthoredFirstBooksMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public SingleAuthoredFirstBooksMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @return matches represented as a SingleAuthoredFirstBooksMatch object.
   * 
   */
  public Collection<SingleAuthoredFirstBooksMatch> getAllMatches(final Library pLibrary, final Book pFirstBook) {
    return rawGetAllMatches(new Object[]{pLibrary, pFirstBook});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @return a match represented as a SingleAuthoredFirstBooksMatch object, or null if no match is found.
   * 
   */
  public SingleAuthoredFirstBooksMatch getOneArbitraryMatch(final Library pLibrary, final Book pFirstBook) {
    return rawGetOneArbitraryMatch(new Object[]{pLibrary, pFirstBook});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Library pLibrary, final Book pFirstBook) {
    return rawHasMatch(new Object[]{pLibrary, pFirstBook});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Library pLibrary, final Book pFirstBook) {
    return rawCountMatches(new Object[]{pLibrary, pFirstBook});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Library pLibrary, final Book pFirstBook, final IMatchProcessor<? super SingleAuthoredFirstBooksMatch> processor) {
    rawForEachMatch(new Object[]{pLibrary, pFirstBook}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Library pLibrary, final Book pFirstBook, final IMatchProcessor<? super SingleAuthoredFirstBooksMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pLibrary, pFirstBook}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<SingleAuthoredFirstBooksMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Library pLibrary, final Book pFirstBook) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pLibrary, pFirstBook});
  }
  
  /**
   * Returns a new (partial) Match object for the matcher.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public SingleAuthoredFirstBooksMatch newMatch(final Library pLibrary, final Book pFirstBook) {
    return new SingleAuthoredFirstBooksMatch.Immutable(pLibrary, pFirstBook);
    
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
  public Set<Library> getAllValuesOflibrary(final SingleAuthoredFirstBooksMatch partialMatch) {
    return rawAccumulateAllValuesOflibrary(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for library.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Library> getAllValuesOflibrary(final Book pFirstBook) {
    return rawAccumulateAllValuesOflibrary(new Object[]{null, pFirstBook});
  }
  
  /**
   * Retrieve the set of values that occur in matches for firstBook.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Book> rawAccumulateAllValuesOffirstBook(final Object[] parameters) {
    Set<Book> results = new HashSet<Book>();
    rawAccumulateAllValues(POSITION_FIRSTBOOK, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for firstBook.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Book> getAllValuesOffirstBook() {
    return rawAccumulateAllValuesOffirstBook(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for firstBook.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Book> getAllValuesOffirstBook(final SingleAuthoredFirstBooksMatch partialMatch) {
    return rawAccumulateAllValuesOffirstBook(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for firstBook.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Book> getAllValuesOffirstBook(final Library pLibrary) {
    return rawAccumulateAllValuesOffirstBook(new Object[]{pLibrary, null});
  }
  
  @Override
  protected SingleAuthoredFirstBooksMatch tupleToMatch(final Tuple t) {
    try {
      return new SingleAuthoredFirstBooksMatch.Immutable((org.eclipse.incquery.examples.eiqlibrary.Library) t.get(POSITION_LIBRARY), (org.eclipse.incquery.examples.eiqlibrary.Book) t.get(POSITION_FIRSTBOOK));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected SingleAuthoredFirstBooksMatch arrayToMatch(final Object[] match) {
    try {
      return new SingleAuthoredFirstBooksMatch.Immutable((org.eclipse.incquery.examples.eiqlibrary.Library) match[POSITION_LIBRARY], (org.eclipse.incquery.examples.eiqlibrary.Book) match[POSITION_FIRSTBOOK]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected SingleAuthoredFirstBooksMatch arrayToMatchMutable(final Object[] match) {
    try {
      return new SingleAuthoredFirstBooksMatch.Mutable((org.eclipse.incquery.examples.eiqlibrary.Library) match[POSITION_LIBRARY], (org.eclipse.incquery.examples.eiqlibrary.Book) match[POSITION_FIRSTBOOK]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
