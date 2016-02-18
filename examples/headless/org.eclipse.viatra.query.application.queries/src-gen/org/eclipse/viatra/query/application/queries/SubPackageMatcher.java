package org.eclipse.viatra.query.application.queries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.application.queries.SubPackageMatch;
import org.eclipse.viatra.query.application.queries.util.SubPackageQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * Generated pattern matcher API of the org.eclipse.viatra.query.application.queries.subPackage pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(ViatraQueryEngine)},
 * e.g. in conjunction with {@link ViatraQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link SubPackageMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * {@literal @}Edge(source = p, target = sp, label = "sub")
 * pattern subPackage(p: EPackage, sp: EPackage){ EPackage.eSubpackages(p,sp); }
 * </pre></code>
 * 
 * @see SubPackageMatch
 * @see SubPackageProcessor
 * @see SubPackageQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class SubPackageMatcher extends BaseMatcher<SubPackageMatch> {
  /**
   * Initializes the pattern matcher within an existing VIATRA Query engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing VIATRA Query engine in which this matcher will be created.
   * @throws ViatraQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static SubPackageMatcher on(final ViatraQueryEngine engine) throws ViatraQueryException {
    // check if matcher already exists
    SubPackageMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new SubPackageMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_P = 0;
  
  private final static int POSITION_SP = 1;
  
  private final static Logger LOGGER = ViatraQueryLoggingUtil.getLogger(SubPackageMatcher.class);
  
  /**
   * Initializes the pattern matcher over a given EMF model root (recommended: Resource or ResourceSet).
   * If a pattern matcher is already constructed with the same root, only a light-weight reference is returned.
   * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition).
   * The match set will be incrementally refreshed upon updates from this scope.
   * <p>The matcher will be created within the managed {@link ViatraQueryEngine} belonging to the EMF model root, so
   * multiple matchers will reuse the same engine and benefit from increased performance and reduced memory footprint.
   * @param emfRoot the root of the EMF containment hierarchy where the pattern matcher will operate. Recommended: Resource or ResourceSet.
   * @throws ViatraQueryException if an error occurs during pattern matcher creation
   * @deprecated use {@link #on(ViatraQueryEngine)} instead, e.g. in conjunction with {@link ViatraQueryEngine#on(Notifier)}
   * 
   */
  @Deprecated
  public SubPackageMatcher(final Notifier emfRoot) throws ViatraQueryException {
    this(ViatraQueryEngine.on(emfRoot));
  }
  
  /**
   * Initializes the pattern matcher within an existing VIATRA Query engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing VIATRA Query engine in which this matcher will be created.
   * @throws ViatraQueryException if an error occurs during pattern matcher creation
   * @deprecated use {@link #on(ViatraQueryEngine)} instead
   * 
   */
  @Deprecated
  public SubPackageMatcher(final ViatraQueryEngine engine) throws ViatraQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pSp the fixed value of pattern parameter sp, or null if not bound.
   * @return matches represented as a SubPackageMatch object.
   * 
   */
  public Collection<SubPackageMatch> getAllMatches(final EPackage pP, final EPackage pSp) {
    return rawGetAllMatches(new Object[]{pP, pSp});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pSp the fixed value of pattern parameter sp, or null if not bound.
   * @return a match represented as a SubPackageMatch object, or null if no match is found.
   * 
   */
  public SubPackageMatch getOneArbitraryMatch(final EPackage pP, final EPackage pSp) {
    return rawGetOneArbitraryMatch(new Object[]{pP, pSp});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pSp the fixed value of pattern parameter sp, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final EPackage pP, final EPackage pSp) {
    return rawHasMatch(new Object[]{pP, pSp});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pSp the fixed value of pattern parameter sp, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final EPackage pP, final EPackage pSp) {
    return rawCountMatches(new Object[]{pP, pSp});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pSp the fixed value of pattern parameter sp, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final EPackage pP, final EPackage pSp, final IMatchProcessor<? super SubPackageMatch> processor) {
    rawForEachMatch(new Object[]{pP, pSp}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pSp the fixed value of pattern parameter sp, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final EPackage pP, final EPackage pSp, final IMatchProcessor<? super SubPackageMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pP, pSp}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pSp the fixed value of pattern parameter sp, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public SubPackageMatch newMatch(final EPackage pP, final EPackage pSp) {
    return SubPackageMatch.newMatch(pP, pSp);
  }
  
  /**
   * Retrieve the set of values that occur in matches for p.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<EPackage> rawAccumulateAllValuesOfp(final Object[] parameters) {
    Set<EPackage> results = new HashSet<EPackage>();
    rawAccumulateAllValues(POSITION_P, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for p.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfp() {
    return rawAccumulateAllValuesOfp(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for p.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfp(final SubPackageMatch partialMatch) {
    return rawAccumulateAllValuesOfp(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for p.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfp(final EPackage pSp) {
    return rawAccumulateAllValuesOfp(new Object[]{
    null, 
    pSp
    });
  }
  
  /**
   * Retrieve the set of values that occur in matches for sp.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<EPackage> rawAccumulateAllValuesOfsp(final Object[] parameters) {
    Set<EPackage> results = new HashSet<EPackage>();
    rawAccumulateAllValues(POSITION_SP, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for sp.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfsp() {
    return rawAccumulateAllValuesOfsp(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for sp.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfsp(final SubPackageMatch partialMatch) {
    return rawAccumulateAllValuesOfsp(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for sp.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfsp(final EPackage pP) {
    return rawAccumulateAllValuesOfsp(new Object[]{
    pP, 
    null
    });
  }
  
  @Override
  protected SubPackageMatch tupleToMatch(final Tuple t) {
    try {
    	return SubPackageMatch.newMatch((EPackage) t.get(POSITION_P), (EPackage) t.get(POSITION_SP));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected SubPackageMatch arrayToMatch(final Object[] match) {
    try {
    	return SubPackageMatch.newMatch((EPackage) match[POSITION_P], (EPackage) match[POSITION_SP]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected SubPackageMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return SubPackageMatch.newMutableMatch((EPackage) match[POSITION_P], (EPackage) match[POSITION_SP]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws ViatraQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<SubPackageMatcher> querySpecification() throws ViatraQueryException {
    return SubPackageQuerySpecification.instance();
  }
}
