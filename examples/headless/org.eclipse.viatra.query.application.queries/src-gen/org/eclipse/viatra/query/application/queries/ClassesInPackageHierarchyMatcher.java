package org.eclipse.viatra.query.application.queries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.application.queries.ClassesInPackageHierarchyMatch;
import org.eclipse.viatra.query.application.queries.util.ClassesInPackageHierarchyQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseMatcher;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.query.runtime.matchers.tuple.Tuple;
import org.eclipse.viatra.query.runtime.util.IncQueryLoggingUtil;

/**
 * Generated pattern matcher API of the org.eclipse.viatra.query.application.queries.classesInPackageHierarchy pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(ViatraQueryEngine)},
 * e.g. in conjunction with {@link ViatraQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link ClassesInPackageHierarchyMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * {@literal @}Edge(source = rootP, target = containedClass, label = "classIn+")
 * {@literal @}Format(color = "#0033ff")
 * pattern classesInPackageHierarchy(rootP: EPackage, containedClass: EClass)
 * {
 * 	find classesInPackage(rootP,containedClass);
 * } or {
 * 	find subPackage+(rootP,somePackage);
 * 	find classesInPackage(somePackage,containedClass);
 * }
 * </pre></code>
 * 
 * @see ClassesInPackageHierarchyMatch
 * @see ClassesInPackageHierarchyProcessor
 * @see ClassesInPackageHierarchyQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class ClassesInPackageHierarchyMatcher extends BaseMatcher<ClassesInPackageHierarchyMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static ClassesInPackageHierarchyMatcher on(final ViatraQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    ClassesInPackageHierarchyMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new ClassesInPackageHierarchyMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_ROOTP = 0;
  
  private final static int POSITION_CONTAINEDCLASS = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(ClassesInPackageHierarchyMatcher.class);
  
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
  public ClassesInPackageHierarchyMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public ClassesInPackageHierarchyMatcher(final ViatraQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRootP the fixed value of pattern parameter rootP, or null if not bound.
   * @param pContainedClass the fixed value of pattern parameter containedClass, or null if not bound.
   * @return matches represented as a ClassesInPackageHierarchyMatch object.
   * 
   */
  public Collection<ClassesInPackageHierarchyMatch> getAllMatches(final EPackage pRootP, final EClass pContainedClass) {
    return rawGetAllMatches(new Object[]{pRootP, pContainedClass});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRootP the fixed value of pattern parameter rootP, or null if not bound.
   * @param pContainedClass the fixed value of pattern parameter containedClass, or null if not bound.
   * @return a match represented as a ClassesInPackageHierarchyMatch object, or null if no match is found.
   * 
   */
  public ClassesInPackageHierarchyMatch getOneArbitraryMatch(final EPackage pRootP, final EClass pContainedClass) {
    return rawGetOneArbitraryMatch(new Object[]{pRootP, pContainedClass});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pRootP the fixed value of pattern parameter rootP, or null if not bound.
   * @param pContainedClass the fixed value of pattern parameter containedClass, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final EPackage pRootP, final EClass pContainedClass) {
    return rawHasMatch(new Object[]{pRootP, pContainedClass});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pRootP the fixed value of pattern parameter rootP, or null if not bound.
   * @param pContainedClass the fixed value of pattern parameter containedClass, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final EPackage pRootP, final EClass pContainedClass) {
    return rawCountMatches(new Object[]{pRootP, pContainedClass});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pRootP the fixed value of pattern parameter rootP, or null if not bound.
   * @param pContainedClass the fixed value of pattern parameter containedClass, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final EPackage pRootP, final EClass pContainedClass, final IMatchProcessor<? super ClassesInPackageHierarchyMatch> processor) {
    rawForEachMatch(new Object[]{pRootP, pContainedClass}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pRootP the fixed value of pattern parameter rootP, or null if not bound.
   * @param pContainedClass the fixed value of pattern parameter containedClass, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final EPackage pRootP, final EClass pContainedClass, final IMatchProcessor<? super ClassesInPackageHierarchyMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pRootP, pContainedClass}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pRootP the fixed value of pattern parameter rootP, or null if not bound.
   * @param pContainedClass the fixed value of pattern parameter containedClass, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public ClassesInPackageHierarchyMatch newMatch(final EPackage pRootP, final EClass pContainedClass) {
    return ClassesInPackageHierarchyMatch.newMatch(pRootP, pContainedClass);
  }
  
  /**
   * Retrieve the set of values that occur in matches for rootP.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<EPackage> rawAccumulateAllValuesOfrootP(final Object[] parameters) {
    Set<EPackage> results = new HashSet<EPackage>();
    rawAccumulateAllValues(POSITION_ROOTP, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for rootP.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfrootP() {
    return rawAccumulateAllValuesOfrootP(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for rootP.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfrootP(final ClassesInPackageHierarchyMatch partialMatch) {
    return rawAccumulateAllValuesOfrootP(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for rootP.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EPackage> getAllValuesOfrootP(final EClass pContainedClass) {
    return rawAccumulateAllValuesOfrootP(new Object[]{
    null, 
    pContainedClass
    });
  }
  
  /**
   * Retrieve the set of values that occur in matches for containedClass.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<EClass> rawAccumulateAllValuesOfcontainedClass(final Object[] parameters) {
    Set<EClass> results = new HashSet<EClass>();
    rawAccumulateAllValues(POSITION_CONTAINEDCLASS, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for containedClass.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EClass> getAllValuesOfcontainedClass() {
    return rawAccumulateAllValuesOfcontainedClass(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for containedClass.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EClass> getAllValuesOfcontainedClass(final ClassesInPackageHierarchyMatch partialMatch) {
    return rawAccumulateAllValuesOfcontainedClass(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for containedClass.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EClass> getAllValuesOfcontainedClass(final EPackage pRootP) {
    return rawAccumulateAllValuesOfcontainedClass(new Object[]{
    pRootP, 
    null
    });
  }
  
  @Override
  protected ClassesInPackageHierarchyMatch tupleToMatch(final Tuple t) {
    try {
    	return ClassesInPackageHierarchyMatch.newMatch((EPackage) t.get(POSITION_ROOTP), (EClass) t.get(POSITION_CONTAINEDCLASS));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected ClassesInPackageHierarchyMatch arrayToMatch(final Object[] match) {
    try {
    	return ClassesInPackageHierarchyMatch.newMatch((EPackage) match[POSITION_ROOTP], (EClass) match[POSITION_CONTAINEDCLASS]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected ClassesInPackageHierarchyMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return ClassesInPackageHierarchyMatch.newMutableMatch((EPackage) match[POSITION_ROOTP], (EClass) match[POSITION_CONTAINEDCLASS]);
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
  public static IQuerySpecification<ClassesInPackageHierarchyMatcher> querySpecification() throws IncQueryException {
    return ClassesInPackageHierarchyQuerySpecification.instance();
  }
}
