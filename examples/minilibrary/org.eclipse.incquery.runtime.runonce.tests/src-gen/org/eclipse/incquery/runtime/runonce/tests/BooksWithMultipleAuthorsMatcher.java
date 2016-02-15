package org.eclipse.incquery.runtime.runonce.tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.examples.eiqlibrary.Book;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.runtime.runonce.tests.BooksWithMultipleAuthorsMatch;
import org.eclipse.incquery.runtime.runonce.tests.util.BooksWithMultipleAuthorsQuerySpecification;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * Generated pattern matcher API of the org.eclipse.incquery.runtime.runonce.tests.booksWithMultipleAuthors pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link BooksWithMultipleAuthorsMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern booksWithMultipleAuthors(book) {
 * 		Book(book);
 * 		numberOfBooks == count find bookAuthors(book, _author);
 * 		check(numberOfBooks {@literal >} 1);
 * }
 * </pre></code>
 * 
 * @see BooksWithMultipleAuthorsMatch
 * @see BooksWithMultipleAuthorsProcessor
 * @see BooksWithMultipleAuthorsQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class BooksWithMultipleAuthorsMatcher extends BaseMatcher<BooksWithMultipleAuthorsMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<BooksWithMultipleAuthorsMatcher> querySpecification() throws IncQueryException {
    return BooksWithMultipleAuthorsQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static BooksWithMultipleAuthorsMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    BooksWithMultipleAuthorsMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new BooksWithMultipleAuthorsMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_BOOK = 0;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(BooksWithMultipleAuthorsMatcher.class);
  
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
  public BooksWithMultipleAuthorsMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public BooksWithMultipleAuthorsMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return matches represented as a BooksWithMultipleAuthorsMatch object.
   * 
   */
  public Collection<BooksWithMultipleAuthorsMatch> getAllMatches(final Book pBook) {
    return rawGetAllMatches(new Object[]{pBook});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return a match represented as a BooksWithMultipleAuthorsMatch object, or null if no match is found.
   * 
   */
  public BooksWithMultipleAuthorsMatch getOneArbitraryMatch(final Book pBook) {
    return rawGetOneArbitraryMatch(new Object[]{pBook});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Book pBook) {
    return rawHasMatch(new Object[]{pBook});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Book pBook) {
    return rawCountMatches(new Object[]{pBook});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Book pBook, final IMatchProcessor<? super BooksWithMultipleAuthorsMatch> processor) {
    rawForEachMatch(new Object[]{pBook}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Book pBook, final IMatchProcessor<? super BooksWithMultipleAuthorsMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pBook}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<BooksWithMultipleAuthorsMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Book pBook) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pBook});
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public BooksWithMultipleAuthorsMatch newMatch(final Book pBook) {
    return BooksWithMultipleAuthorsMatch.newMatch(pBook);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for book.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Book> rawAccumulateAllValuesOfbook(final Object[] parameters) {
    Set<Book> results = new HashSet<Book>();
    rawAccumulateAllValues(POSITION_BOOK, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for book.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Book> getAllValuesOfbook() {
    return rawAccumulateAllValuesOfbook(emptyArray());
  }
  
  @Override
  protected BooksWithMultipleAuthorsMatch tupleToMatch(final Tuple t) {
    try {
      return BooksWithMultipleAuthorsMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Book) t.get(POSITION_BOOK));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected BooksWithMultipleAuthorsMatch arrayToMatch(final Object[] match) {
    try {
      return BooksWithMultipleAuthorsMatch.newMatch((org.eclipse.incquery.examples.eiqlibrary.Book) match[POSITION_BOOK]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected BooksWithMultipleAuthorsMatch arrayToMatchMutable(final Object[] match) {
    try {
      return BooksWithMultipleAuthorsMatch.newMutableMatch((org.eclipse.incquery.examples.eiqlibrary.Book) match[POSITION_BOOK]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
