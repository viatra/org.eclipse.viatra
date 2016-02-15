package headless;

import headless.ClassesInPackageMatch;
import headless.util.ClassesInPackageQuerySpecification;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;

/**
 * Generated pattern matcher API of the headless.classesInPackage pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link ClassesInPackageMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * {@literal @}Edge(source = p, target = ec, label = "classIn")
 * pattern classesInPackage(p : EPackage, ec: EClass) { EPackage.eClassifiers(p,ec); }
 * </pre></code>
 * 
 * @see ClassesInPackageMatch
 * @see ClassesInPackageProcessor
 * @see ClassesInPackageQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class ClassesInPackageMatcher extends BaseMatcher<ClassesInPackageMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<ClassesInPackageMatcher> querySpecification() throws IncQueryException {
    return ClassesInPackageQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static ClassesInPackageMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    ClassesInPackageMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new ClassesInPackageMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_P = 0;
  
  private final static int POSITION_EC = 1;
  
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
  public ClassesInPackageMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public ClassesInPackageMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @return matches represented as a ClassesInPackageMatch object.
   * 
   */
  public Collection<ClassesInPackageMatch> getAllMatches(final EPackage pP, final EClass pEc) {
    return rawGetAllMatches(new Object[]{pP, pEc});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @return a match represented as a ClassesInPackageMatch object, or null if no match is found.
   * 
   */
  public ClassesInPackageMatch getOneArbitraryMatch(final EPackage pP, final EClass pEc) {
    return rawGetOneArbitraryMatch(new Object[]{pP, pEc});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final EPackage pP, final EClass pEc) {
    return rawHasMatch(new Object[]{pP, pEc});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final EPackage pP, final EClass pEc) {
    return rawCountMatches(new Object[]{pP, pEc});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final EPackage pP, final EClass pEc, final IMatchProcessor<? super ClassesInPackageMatch> processor) {
    rawForEachMatch(new Object[]{pP, pEc}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final EPackage pP, final EClass pEc, final IMatchProcessor<? super ClassesInPackageMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pP, pEc}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<ClassesInPackageMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final EPackage pP, final EClass pEc) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pP, pEc});
  }
  
  /**
   * Returns a new (partial) Match object for the matcher.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public ClassesInPackageMatch newMatch(final EPackage pP, final EClass pEc) {
    return new ClassesInPackageMatch.Immutable(pP, pEc);
    
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
  public Set<EPackage> getAllValuesOfp(final ClassesInPackageMatch partialMatch) {
    return rawAccumulateAllValuesOfp(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for p.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfp(final EClass pEc) {
    return rawAccumulateAllValuesOfp(new Object[]{null, pEc});
  }
  
  /**
   * Retrieve the set of values that occur in matches for ec.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<EClass> rawAccumulateAllValuesOfec(final Object[] parameters) {
    Set<EClass> results = new HashSet<EClass>();
    rawAccumulateAllValues(POSITION_EC, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for ec.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EClass> getAllValuesOfec() {
    return rawAccumulateAllValuesOfec(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ec.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EClass> getAllValuesOfec(final ClassesInPackageMatch partialMatch) {
    return rawAccumulateAllValuesOfec(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ec.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EClass> getAllValuesOfec(final EPackage pP) {
    return rawAccumulateAllValuesOfec(new Object[]{pP, null});
  }
  
  @Override
  protected ClassesInPackageMatch tupleToMatch(final Tuple t) {
    try {
    	return new ClassesInPackageMatch.Immutable((org.eclipse.emf.ecore.EPackage) t.get(POSITION_P), (org.eclipse.emf.ecore.EClass) t.get(POSITION_EC));
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in tuple not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  protected ClassesInPackageMatch arrayToMatch(final Object[] match) {
    try {
    	return new ClassesInPackageMatch.Immutable((org.eclipse.emf.ecore.EPackage) match[POSITION_P], (org.eclipse.emf.ecore.EClass) match[POSITION_EC]);
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  protected ClassesInPackageMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return new ClassesInPackageMatch.Mutable((org.eclipse.emf.ecore.EPackage) match[POSITION_P], (org.eclipse.emf.ecore.EClass) match[POSITION_EC]);
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
}
