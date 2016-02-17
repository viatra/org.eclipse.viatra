package org.eclipse.viatra.query.runtime.runonce.tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra.examples.library.Book;
import org.eclipse.viatra.examples.library.Writer;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.runonce.tests.LongSciFiBooksOfAuthorMatch;
import org.eclipse.viatra.query.runtime.runonce.tests.util.LongSciFiBooksOfAuthorQuerySpecification;
import org.eclipse.viatra.query.runtime.util.IncQueryLoggingUtil;

/**
 * Generated pattern matcher API of the org.eclipse.viatra.query.runtime.runonce.tests.longSciFiBooksOfAuthor pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(ViatraQueryEngine)},
 * e.g. in conjunction with {@link ViatraQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link LongSciFiBooksOfAuthorMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern longSciFiBooksOfAuthor(author : Writer, book : Book) {
 * 	Writer.scifiBooks(author, book);
 * 	Book.pages(book, pages);
 * 	check(pages {@literal >} 100);
 * }
 * </pre></code>
 * 
 * @see LongSciFiBooksOfAuthorMatch
 * @see LongSciFiBooksOfAuthorProcessor
 * @see LongSciFiBooksOfAuthorQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class LongSciFiBooksOfAuthorMatcher extends BaseMatcher<LongSciFiBooksOfAuthorMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static LongSciFiBooksOfAuthorMatcher on(final ViatraQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    LongSciFiBooksOfAuthorMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new LongSciFiBooksOfAuthorMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_AUTHOR = 0;
  
  private final static int POSITION_BOOK = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(LongSciFiBooksOfAuthorMatcher.class);
  
  /**
   * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet).
   * If a pattern matcher is already constructed with the same root, only a light-weight reference is returned.
   * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
   * The match set will be incrementally refreshed upon updates from this scope.
   * <p>The matcher will be created within the managed {@link ViatraQueryEngine} belonging to the EMF model root, so
   * multiple matchers will reuse the same engine and benefit from increased performance and reduced memory footprint.
   * @param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * @deprecated use {@link #on(ViatraQueryEngine)} instead, e.g. in conjunction with {@link ViatraQueryEngine#on(Notifier)}
   * 
   */
  @Deprecated
  public LongSciFiBooksOfAuthorMatcher(final Notifier emfRoot) throws IncQueryException {
    this(ViatraQueryEngine.on(emfRoot));
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * @deprecated use {@link #on(ViatraQueryEngine)} instead
   * 
   */
  @Deprecated
  public LongSciFiBooksOfAuthorMatcher(final ViatraQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pAuthor the fixed value of pattern parameter author, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return matches represented as a LongSciFiBooksOfAuthorMatch object.
   * 
   */
  public Collection<LongSciFiBooksOfAuthorMatch> getAllMatches(final Writer pAuthor, final Book pBook) {
    return rawGetAllMatches(new Object[]{pAuthor, pBook});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pAuthor the fixed value of pattern parameter author, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return a match represented as a LongSciFiBooksOfAuthorMatch object, or null if no match is found.
   * 
   */
  public LongSciFiBooksOfAuthorMatch getOneArbitraryMatch(final Writer pAuthor, final Book pBook) {
    return rawGetOneArbitraryMatch(new Object[]{pAuthor, pBook});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pAuthor the fixed value of pattern parameter author, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Writer pAuthor, final Book pBook) {
    return rawHasMatch(new Object[]{pAuthor, pBook});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pAuthor the fixed value of pattern parameter author, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Writer pAuthor, final Book pBook) {
    return rawCountMatches(new Object[]{pAuthor, pBook});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pAuthor the fixed value of pattern parameter author, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Writer pAuthor, final Book pBook, final IMatchProcessor<? super LongSciFiBooksOfAuthorMatch> processor) {
    rawForEachMatch(new Object[]{pAuthor, pBook}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pAuthor the fixed value of pattern parameter author, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Writer pAuthor, final Book pBook, final IMatchProcessor<? super LongSciFiBooksOfAuthorMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pAuthor, pBook}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pAuthor the fixed value of pattern parameter author, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public LongSciFiBooksOfAuthorMatch newMatch(final Writer pAuthor, final Book pBook) {
    return LongSciFiBooksOfAuthorMatch.newMatch(pAuthor, pBook);
  }
  
  /**
   * Retrieve the set of values that occur in matches for author.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Writer> rawAccumulateAllValuesOfauthor(final Object[] parameters) {
    Set<Writer> results = new HashSet<Writer>();
    rawAccumulateAllValues(POSITION_AUTHOR, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for author.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Writer> getAllValuesOfauthor() {
    return rawAccumulateAllValuesOfauthor(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for author.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Writer> getAllValuesOfauthor(final LongSciFiBooksOfAuthorMatch partialMatch) {
    return rawAccumulateAllValuesOfauthor(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for author.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Writer> getAllValuesOfauthor(final Book pBook) {
    return rawAccumulateAllValuesOfauthor(new Object[]{
    null, 
    pBook
    });
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
  
  /**
   * Retrieve the set of values that occur in matches for book.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Book> getAllValuesOfbook(final LongSciFiBooksOfAuthorMatch partialMatch) {
    return rawAccumulateAllValuesOfbook(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for book.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Book> getAllValuesOfbook(final Writer pAuthor) {
    return rawAccumulateAllValuesOfbook(new Object[]{
    pAuthor, 
    null
    });
  }
  
  @Override
  protected LongSciFiBooksOfAuthorMatch tupleToMatch(final Tuple t) {
    try {
    	return LongSciFiBooksOfAuthorMatch.newMatch((Writer) t.get(POSITION_AUTHOR), (Book) t.get(POSITION_BOOK));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected LongSciFiBooksOfAuthorMatch arrayToMatch(final Object[] match) {
    try {
    	return LongSciFiBooksOfAuthorMatch.newMatch((Writer) match[POSITION_AUTHOR], (Book) match[POSITION_BOOK]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected LongSciFiBooksOfAuthorMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return LongSciFiBooksOfAuthorMatch.newMutableMatch((Writer) match[POSITION_AUTHOR], (Book) match[POSITION_BOOK]);
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
  public static IQuerySpecification<LongSciFiBooksOfAuthorMatcher> querySpecification() throws IncQueryException {
    return LongSciFiBooksOfAuthorQuerySpecification.instance();
  }
}
