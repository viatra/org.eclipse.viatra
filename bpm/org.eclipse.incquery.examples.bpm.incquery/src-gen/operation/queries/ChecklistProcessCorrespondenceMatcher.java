package operation.queries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import operation.queries.ChecklistProcessCorrespondenceMatch;
import operation.queries.util.ChecklistProcessCorrespondenceQuerySpecification;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;

/**
 * Generated pattern matcher API of the operation.queries.ChecklistProcessCorrespondence pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link ChecklistProcessCorrespondenceMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * Checklist.process relation 
 * {@literal @}QueryBasedFeature(feature = "process")
 * pattern ChecklistProcessCorrespondence(Checklist : Checklist, Process : Process) = {
 * 	Process.id(Process,ProcessId);
 * 	Checklist.processId(Checklist,ProcessId);
 * }
 * </pre></code>
 * 
 * @see ChecklistProcessCorrespondenceMatch
 * @see ChecklistProcessCorrespondenceProcessor
 * @see ChecklistProcessCorrespondenceQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class ChecklistProcessCorrespondenceMatcher extends BaseMatcher<ChecklistProcessCorrespondenceMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static ChecklistProcessCorrespondenceMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    ChecklistProcessCorrespondenceMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new ChecklistProcessCorrespondenceMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_CHECKLIST = 0;
  
  private final static int POSITION_PROCESS = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(ChecklistProcessCorrespondenceMatcher.class);
  
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
  public ChecklistProcessCorrespondenceMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public ChecklistProcessCorrespondenceMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pChecklist the fixed value of pattern parameter Checklist, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return matches represented as a ChecklistProcessCorrespondenceMatch object.
   * 
   */
  public Collection<ChecklistProcessCorrespondenceMatch> getAllMatches(final EObject pChecklist, final EObject pProcess) {
    return rawGetAllMatches(new Object[]{pChecklist, pProcess});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pChecklist the fixed value of pattern parameter Checklist, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return a match represented as a ChecklistProcessCorrespondenceMatch object, or null if no match is found.
   * 
   */
  public ChecklistProcessCorrespondenceMatch getOneArbitraryMatch(final EObject pChecklist, final EObject pProcess) {
    return rawGetOneArbitraryMatch(new Object[]{pChecklist, pProcess});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pChecklist the fixed value of pattern parameter Checklist, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final EObject pChecklist, final EObject pProcess) {
    return rawHasMatch(new Object[]{pChecklist, pProcess});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pChecklist the fixed value of pattern parameter Checklist, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final EObject pChecklist, final EObject pProcess) {
    return rawCountMatches(new Object[]{pChecklist, pProcess});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pChecklist the fixed value of pattern parameter Checklist, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final EObject pChecklist, final EObject pProcess, final IMatchProcessor<? super ChecklistProcessCorrespondenceMatch> processor) {
    rawForEachMatch(new Object[]{pChecklist, pProcess}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pChecklist the fixed value of pattern parameter Checklist, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final EObject pChecklist, final EObject pProcess, final IMatchProcessor<? super ChecklistProcessCorrespondenceMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pChecklist, pProcess}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pChecklist the fixed value of pattern parameter Checklist, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public ChecklistProcessCorrespondenceMatch newMatch(final EObject pChecklist, final EObject pProcess) {
    return ChecklistProcessCorrespondenceMatch.newMatch(pChecklist, pProcess);
  }
  
  /**
   * Retrieve the set of values that occur in matches for Checklist.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<EObject> rawAccumulateAllValuesOfChecklist(final Object[] parameters) {
    Set<EObject> results = new HashSet<EObject>();
    rawAccumulateAllValues(POSITION_CHECKLIST, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Checklist.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EObject> getAllValuesOfChecklist() {
    return rawAccumulateAllValuesOfChecklist(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Checklist.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EObject> getAllValuesOfChecklist(final ChecklistProcessCorrespondenceMatch partialMatch) {
    return rawAccumulateAllValuesOfChecklist(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Checklist.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EObject> getAllValuesOfChecklist(final EObject pProcess) {
    return rawAccumulateAllValuesOfChecklist(new Object[]{
    null, 
    pProcess
    });
  }
  
  /**
   * Retrieve the set of values that occur in matches for Process.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<EObject> rawAccumulateAllValuesOfProcess(final Object[] parameters) {
    Set<EObject> results = new HashSet<EObject>();
    rawAccumulateAllValues(POSITION_PROCESS, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Process.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EObject> getAllValuesOfProcess() {
    return rawAccumulateAllValuesOfProcess(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Process.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EObject> getAllValuesOfProcess(final ChecklistProcessCorrespondenceMatch partialMatch) {
    return rawAccumulateAllValuesOfProcess(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Process.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<EObject> getAllValuesOfProcess(final EObject pChecklist) {
    return rawAccumulateAllValuesOfProcess(new Object[]{
    pChecklist, 
    null
    });
  }
  
  @Override
  protected ChecklistProcessCorrespondenceMatch tupleToMatch(final Tuple t) {
    try {
    	return ChecklistProcessCorrespondenceMatch.newMatch((org.eclipse.emf.ecore.EObject) t.get(POSITION_CHECKLIST), (org.eclipse.emf.ecore.EObject) t.get(POSITION_PROCESS));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected ChecklistProcessCorrespondenceMatch arrayToMatch(final Object[] match) {
    try {
    	return ChecklistProcessCorrespondenceMatch.newMatch((org.eclipse.emf.ecore.EObject) match[POSITION_CHECKLIST], (org.eclipse.emf.ecore.EObject) match[POSITION_PROCESS]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected ChecklistProcessCorrespondenceMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return ChecklistProcessCorrespondenceMatch.newMutableMatch((org.eclipse.emf.ecore.EObject) match[POSITION_CHECKLIST], (org.eclipse.emf.ecore.EObject) match[POSITION_PROCESS]);
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
  public static IQuerySpecification<ChecklistProcessCorrespondenceMatcher> querySpecification() throws IncQueryException {
    return ChecklistProcessCorrespondenceQuerySpecification.instance();
  }
}
