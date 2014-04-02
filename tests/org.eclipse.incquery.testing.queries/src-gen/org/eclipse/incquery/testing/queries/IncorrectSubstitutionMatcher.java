package org.eclipse.incquery.testing.queries;

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
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.incquery.testing.queries.IncorrectSubstitutionMatch;
import org.eclipse.incquery.testing.queries.util.IncorrectSubstitutionQuerySpecification;

/**
 * Generated pattern matcher API of the org.eclipse.incquery.testing.queries.IncorrectSubstitution pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link IncorrectSubstitutionMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * pattern IncorrectSubstitution(
 * 	Record : MatchRecord,
 * 	CorrespondingRecord : MatchRecord
 * ) = {
 * 	MatchRecord.substitutions(Record,Substitution);
 * 	MatchSubstitutionRecord.parameterName(Substitution,Name);
 * 	MatchRecord.substitutions(CorrespondingRecord,CorrespondingSubstitution);
 * 	MatchSubstitutionRecord.parameterName(CorrespondingSubstitution,Name);
 * 	MatchSubstitutionRecord.derivedValue(Substitution,Value1);
 * 	MatchSubstitutionRecord.derivedValue(CorrespondingSubstitution,Value2);
 * 	Value1 != Value2;
 * }
 * </pre></code>
 * 
 * @see IncorrectSubstitutionMatch
 * @see IncorrectSubstitutionProcessor
 * @see IncorrectSubstitutionQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class IncorrectSubstitutionMatcher extends BaseMatcher<IncorrectSubstitutionMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<IncorrectSubstitutionMatcher> querySpecification() throws IncQueryException {
    return IncorrectSubstitutionQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static IncorrectSubstitutionMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    IncorrectSubstitutionMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new IncorrectSubstitutionMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_RECORD = 0;
  
  private final static int POSITION_CORRESPONDINGRECORD = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(IncorrectSubstitutionMatcher.class);
  
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
  public IncorrectSubstitutionMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public IncorrectSubstitutionMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespondingRecord the fixed value of pattern parameter CorrespondingRecord, or null if not bound.
   * @return matches represented as a IncorrectSubstitutionMatch object.
   * 
   */
  public Collection<IncorrectSubstitutionMatch> getAllMatches(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
    return rawGetAllMatches(new Object[]{pRecord, pCorrespondingRecord});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespondingRecord the fixed value of pattern parameter CorrespondingRecord, or null if not bound.
   * @return a match represented as a IncorrectSubstitutionMatch object, or null if no match is found.
   * 
   */
  public IncorrectSubstitutionMatch getOneArbitraryMatch(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
    return rawGetOneArbitraryMatch(new Object[]{pRecord, pCorrespondingRecord});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespondingRecord the fixed value of pattern parameter CorrespondingRecord, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
    return rawHasMatch(new Object[]{pRecord, pCorrespondingRecord});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespondingRecord the fixed value of pattern parameter CorrespondingRecord, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
    return rawCountMatches(new Object[]{pRecord, pCorrespondingRecord});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespondingRecord the fixed value of pattern parameter CorrespondingRecord, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord, final IMatchProcessor<? super IncorrectSubstitutionMatch> processor) {
    rawForEachMatch(new Object[]{pRecord, pCorrespondingRecord}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespondingRecord the fixed value of pattern parameter CorrespondingRecord, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord, final IMatchProcessor<? super IncorrectSubstitutionMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pRecord, pCorrespondingRecord}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespondingRecord the fixed value of pattern parameter CorrespondingRecord, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<IncorrectSubstitutionMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pRecord, pCorrespondingRecord});
  }
  
  /**
   * Returns a new (partial) Match object for the matcher.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pCorrespondingRecord the fixed value of pattern parameter CorrespondingRecord, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public IncorrectSubstitutionMatch newMatch(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
    return new IncorrectSubstitutionMatch.Immutable(pRecord, pCorrespondingRecord);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<MatchRecord> rawAccumulateAllValuesOfRecord(final Object[] parameters) {
    Set<MatchRecord> results = new HashSet<MatchRecord>();
    rawAccumulateAllValues(POSITION_RECORD, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfRecord() {
    return rawAccumulateAllValuesOfRecord(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfRecord(final IncorrectSubstitutionMatch partialMatch) {
    return rawAccumulateAllValuesOfRecord(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Record.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfRecord(final MatchRecord pCorrespondingRecord) {
    return rawAccumulateAllValuesOfRecord(new Object[]{null, pCorrespondingRecord});
  }
  
  /**
   * Retrieve the set of values that occur in matches for CorrespondingRecord.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<MatchRecord> rawAccumulateAllValuesOfCorrespondingRecord(final Object[] parameters) {
    Set<MatchRecord> results = new HashSet<MatchRecord>();
    rawAccumulateAllValues(POSITION_CORRESPONDINGRECORD, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for CorrespondingRecord.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfCorrespondingRecord() {
    return rawAccumulateAllValuesOfCorrespondingRecord(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for CorrespondingRecord.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfCorrespondingRecord(final IncorrectSubstitutionMatch partialMatch) {
    return rawAccumulateAllValuesOfCorrespondingRecord(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for CorrespondingRecord.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchRecord> getAllValuesOfCorrespondingRecord(final MatchRecord pRecord) {
    return rawAccumulateAllValuesOfCorrespondingRecord(new Object[]{pRecord, null});
  }
  
  @Override
  protected IncorrectSubstitutionMatch tupleToMatch(final Tuple t) {
    try {
      return new IncorrectSubstitutionMatch.Immutable((org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) t.get(POSITION_RECORD), (org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) t.get(POSITION_CORRESPONDINGRECORD));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected IncorrectSubstitutionMatch arrayToMatch(final Object[] match) {
    try {
      return new IncorrectSubstitutionMatch.Immutable((org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) match[POSITION_RECORD], (org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) match[POSITION_CORRESPONDINGRECORD]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected IncorrectSubstitutionMatch arrayToMatchMutable(final Object[] match) {
    try {
      return new IncorrectSubstitutionMatch.Mutable((org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) match[POSITION_RECORD], (org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) match[POSITION_CORRESPONDINGRECORD]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
