package system.queries;

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
import process.Task;
import system.queries.TasksAffectedThroughDataMatch;
import system.queries.util.TasksAffectedThroughDataQuerySpecification;

/**
 * Generated pattern matcher API of the system.queries.TasksAffectedThroughData pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link TasksAffectedThroughDataMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * // third query (complex query for additional info in process model)
 * pattern TasksAffectedThroughData(SourceTask, AffectedTask) = {
 * 	Task(SourceTask);
 * 	Task(AffectedTask);
 * 	Data(Data);
 * 	find DataTaskWriteCorrespondence(Data, SourceTask);
 * 	find DataTaskReadCorrespondence(Data, AffectedTask);
 * }
 * </pre></code>
 * 
 * @see TasksAffectedThroughDataMatch
 * @see TasksAffectedThroughDataProcessor
 * @see TasksAffectedThroughDataQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class TasksAffectedThroughDataMatcher extends BaseMatcher<TasksAffectedThroughDataMatch> {
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<TasksAffectedThroughDataMatcher> querySpecification() throws IncQueryException {
    return TasksAffectedThroughDataQuerySpecification.instance();
  }
  
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static TasksAffectedThroughDataMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    TasksAffectedThroughDataMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new TasksAffectedThroughDataMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_SOURCETASK = 0;
  
  private final static int POSITION_AFFECTEDTASK = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(TasksAffectedThroughDataMatcher.class);
  
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
  public TasksAffectedThroughDataMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public TasksAffectedThroughDataMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pSourceTask the fixed value of pattern parameter SourceTask, or null if not bound.
   * @param pAffectedTask the fixed value of pattern parameter AffectedTask, or null if not bound.
   * @return matches represented as a TasksAffectedThroughDataMatch object.
   * 
   */
  public Collection<TasksAffectedThroughDataMatch> getAllMatches(final Task pSourceTask, final Task pAffectedTask) {
    return rawGetAllMatches(new Object[]{pSourceTask, pAffectedTask});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pSourceTask the fixed value of pattern parameter SourceTask, or null if not bound.
   * @param pAffectedTask the fixed value of pattern parameter AffectedTask, or null if not bound.
   * @return a match represented as a TasksAffectedThroughDataMatch object, or null if no match is found.
   * 
   */
  public TasksAffectedThroughDataMatch getOneArbitraryMatch(final Task pSourceTask, final Task pAffectedTask) {
    return rawGetOneArbitraryMatch(new Object[]{pSourceTask, pAffectedTask});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pSourceTask the fixed value of pattern parameter SourceTask, or null if not bound.
   * @param pAffectedTask the fixed value of pattern parameter AffectedTask, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Task pSourceTask, final Task pAffectedTask) {
    return rawHasMatch(new Object[]{pSourceTask, pAffectedTask});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pSourceTask the fixed value of pattern parameter SourceTask, or null if not bound.
   * @param pAffectedTask the fixed value of pattern parameter AffectedTask, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Task pSourceTask, final Task pAffectedTask) {
    return rawCountMatches(new Object[]{pSourceTask, pAffectedTask});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pSourceTask the fixed value of pattern parameter SourceTask, or null if not bound.
   * @param pAffectedTask the fixed value of pattern parameter AffectedTask, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Task pSourceTask, final Task pAffectedTask, final IMatchProcessor<? super TasksAffectedThroughDataMatch> processor) {
    rawForEachMatch(new Object[]{pSourceTask, pAffectedTask}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pSourceTask the fixed value of pattern parameter SourceTask, or null if not bound.
   * @param pAffectedTask the fixed value of pattern parameter AffectedTask, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Task pSourceTask, final Task pAffectedTask, final IMatchProcessor<? super TasksAffectedThroughDataMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pSourceTask, pAffectedTask}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters.
   * It can also be reset to track changes from a later point in time,
   * and changes can even be acknowledged on an individual basis.
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pSourceTask the fixed value of pattern parameter SourceTask, or null if not bound.
   * @param pAffectedTask the fixed value of pattern parameter AffectedTask, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<TasksAffectedThroughDataMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Task pSourceTask, final Task pAffectedTask) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pSourceTask, pAffectedTask});
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pSourceTask the fixed value of pattern parameter SourceTask, or null if not bound.
   * @param pAffectedTask the fixed value of pattern parameter AffectedTask, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public TasksAffectedThroughDataMatch newMatch(final Task pSourceTask, final Task pAffectedTask) {
    return TasksAffectedThroughDataMatch.newMatch(pSourceTask, pAffectedTask);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for SourceTask.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Task> rawAccumulateAllValuesOfSourceTask(final Object[] parameters) {
    Set<Task> results = new HashSet<Task>();
    rawAccumulateAllValues(POSITION_SOURCETASK, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for SourceTask.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfSourceTask() {
    return rawAccumulateAllValuesOfSourceTask(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for SourceTask.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfSourceTask(final TasksAffectedThroughDataMatch partialMatch) {
    return rawAccumulateAllValuesOfSourceTask(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for SourceTask.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfSourceTask(final Task pAffectedTask) {
    return rawAccumulateAllValuesOfSourceTask(new Object[]{null, pAffectedTask});
  }
  
  /**
   * Retrieve the set of values that occur in matches for AffectedTask.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Task> rawAccumulateAllValuesOfAffectedTask(final Object[] parameters) {
    Set<Task> results = new HashSet<Task>();
    rawAccumulateAllValues(POSITION_AFFECTEDTASK, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for AffectedTask.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfAffectedTask() {
    return rawAccumulateAllValuesOfAffectedTask(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for AffectedTask.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfAffectedTask(final TasksAffectedThroughDataMatch partialMatch) {
    return rawAccumulateAllValuesOfAffectedTask(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for AffectedTask.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfAffectedTask(final Task pSourceTask) {
    return rawAccumulateAllValuesOfAffectedTask(new Object[]{pSourceTask, null});
  }
  
  @Override
  protected TasksAffectedThroughDataMatch tupleToMatch(final Tuple t) {
    try {
      return TasksAffectedThroughDataMatch.newMatch((process.Task) t.get(POSITION_SOURCETASK), (process.Task) t.get(POSITION_AFFECTEDTASK));
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in tuple not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected TasksAffectedThroughDataMatch arrayToMatch(final Object[] match) {
    try {
      return TasksAffectedThroughDataMatch.newMatch((process.Task) match[POSITION_SOURCETASK], (process.Task) match[POSITION_AFFECTEDTASK]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
  
  @Override
  protected TasksAffectedThroughDataMatch arrayToMatchMutable(final Object[] match) {
    try {
      return TasksAffectedThroughDataMatch.newMutableMatch((process.Task) match[POSITION_SOURCETASK], (process.Task) match[POSITION_AFFECTEDTASK]);
    } catch(ClassCastException e) {
      LOGGER.error("Element(s) in array not properly typed!",e);
      return null;
    }
    
  }
}
