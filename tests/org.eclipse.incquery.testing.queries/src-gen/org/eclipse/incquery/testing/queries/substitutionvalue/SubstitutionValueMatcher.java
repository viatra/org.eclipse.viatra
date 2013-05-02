package org.eclipse.incquery.testing.queries.substitutionvalue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryEngineManager;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord;
import org.eclipse.incquery.testing.queries.substitutionvalue.SubstitutionValueMatch;
import org.eclipse.incquery.testing.queries.substitutionvalue.SubstitutionValueQuerySpecification;

/**
 * Generated pattern matcher API of the org.eclipse.incquery.testing.queries.SubstitutionValue pattern, 
 * providing pattern-specific query methods.
 * 
 * <p>Original source:
 * <code><pre>
 * {@literal @}QueryExplorer(display = false)
 * {@literal @}QueryBasedFeature(feature = "derivedValue")
 * pattern SubstitutionValue(
 * 	Substitution : MatchSubstitutionRecord,
 * 	Value
 * ) = {
 * 	MiscellaneousSubstitution.value(Substitution,Value);
 * } or {
 * 	EMFSubstitution.value(Substitution,Value);
 * } or {
 * 	IntSubstitution.value(Substitution,Value);
 * } or {
 * 	LongSubstitution.value(Substitution,Value);
 * } or {
 * 	DoubleSubstitution.value(Substitution,Value);
 * } or {
 * 	FloatSubstitution.value(Substitution,Value);
 * } or {
 * 	BooleanSubstitution.value(Substitution,Value);
 * } or {
 * 	StringSubstitution.value(Substitution,Value);
 * } or {
 * 	DateSubstitution.value(Substitution,Value);
 * } or {
 * 	EnumSubstitution.valueLiteral(Substitution,Value);
 * }
 * </pre></code>
 * 
 * @see SubstitutionValueMatch
 * @see SubstitutionValueQuerySpecification
 * @see SubstitutionValueProcessor
 * 
 */
public class SubstitutionValueMatcher extends BaseGeneratedMatcher<SubstitutionValueMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
   * If the pattern matcher is already constructed in the engine, only a lightweight reference is created.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static SubstitutionValueMatcher on(final IncQueryEngine engine) throws IncQueryException {
    return new SubstitutionValueMatcher(engine);
  }
  
  private final static int POSITION_SUBSTITUTION = 0;
  
  private final static int POSITION_VALUE = 1;
  
  /**
   * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet). 
   * If a pattern matcher is already constructed with the same root, only a lightweight reference is created.
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
  public SubstitutionValueMatcher(final Notifier emfRoot) throws IncQueryException {
    this(IncQueryEngineManager.getInstance().getIncQueryEngine(emfRoot));
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
   * If the pattern matcher is already constructed in the engine, only a lightweight reference is created.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * @deprecated use {@link #on(IncQueryEngine)} instead
   * 
   */
  @Deprecated
  public SubstitutionValueMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @return matches represented as a SubstitutionValueMatch object.
   * 
   */
  public Collection<SubstitutionValueMatch> getAllMatches(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
    return rawGetAllMatches(new Object[]{pSubstitution, pValue});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @return a match represented as a SubstitutionValueMatch object, or null if no match is found.
   * 
   */
  public SubstitutionValueMatch getOneArbitraryMatch(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
    return rawGetOneArbitraryMatch(new Object[]{pSubstitution, pValue});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
    return rawHasMatch(new Object[]{pSubstitution, pValue});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
    return rawCountMatches(new Object[]{pSubstitution, pValue});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final MatchSubstitutionRecord pSubstitution, final Object pValue, final IMatchProcessor<? super SubstitutionValueMatch> processor) {
    rawForEachMatch(new Object[]{pSubstitution, pValue}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @param processor the action that will process the selected match. 
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final MatchSubstitutionRecord pSubstitution, final Object pValue, final IMatchProcessor<? super SubstitutionValueMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pSubstitution, pValue}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters. 
   * It can also be reset to track changes from a later point in time, 
   * and changes can even be acknowledged on an individual basis. 
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @return the delta monitor.
   * 
   */
  public DeltaMonitor<SubstitutionValueMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final MatchSubstitutionRecord pSubstitution, final Object pValue) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pSubstitution, pValue});
  }
  
  /**
   * Returns a new (partial) Match object for the matcher. 
   * This can be used e.g. to call the matcher with a partial match. 
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public SubstitutionValueMatch newMatch(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
    return new SubstitutionValueMatch.Immutable(pSubstitution, pValue);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for Substitution.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<MatchSubstitutionRecord> rawAccumulateAllValuesOfSubstitution(final Object[] parameters) {
    Set<MatchSubstitutionRecord> results = new HashSet<MatchSubstitutionRecord>();
    rawAccumulateAllValues(POSITION_SUBSTITUTION, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Substitution.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSubstitutionRecord> getAllValuesOfSubstitution() {
    return rawAccumulateAllValuesOfSubstitution(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Substitution.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSubstitutionRecord> getAllValuesOfSubstitution(final SubstitutionValueMatch partialMatch) {
    return rawAccumulateAllValuesOfSubstitution(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Substitution.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<MatchSubstitutionRecord> getAllValuesOfSubstitution(final Object pValue) {
    return rawAccumulateAllValuesOfSubstitution(new Object[]{null, pValue});
  }
  
  /**
   * Retrieve the set of values that occur in matches for Value.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Object> rawAccumulateAllValuesOfValue(final Object[] parameters) {
    Set<Object> results = new HashSet<Object>();
    rawAccumulateAllValues(POSITION_VALUE, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Value.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfValue() {
    return rawAccumulateAllValuesOfValue(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Value.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfValue(final SubstitutionValueMatch partialMatch) {
    return rawAccumulateAllValuesOfValue(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Value.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Object> getAllValuesOfValue(final MatchSubstitutionRecord pSubstitution) {
    return rawAccumulateAllValuesOfValue(new Object[]{pSubstitution, null});
  }
  
  @Override
  protected SubstitutionValueMatch tupleToMatch(final Tuple t) {
    try {
    	return new SubstitutionValueMatch.Immutable((org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord) t.get(POSITION_SUBSTITUTION), (java.lang.Object) t.get(POSITION_VALUE));	
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in tuple not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  protected SubstitutionValueMatch arrayToMatch(final Object[] match) {
    try {
    	return new SubstitutionValueMatch.Immutable((org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord) match[POSITION_SUBSTITUTION], (java.lang.Object) match[POSITION_VALUE]);
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  protected SubstitutionValueMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return new SubstitutionValueMatch.Mutable((org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord) match[POSITION_SUBSTITUTION], (java.lang.Object) match[POSITION_VALUE]);
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<SubstitutionValueMatcher> querySpecification() throws IncQueryException {
    return SubstitutionValueQuerySpecification.instance();
  }
}
