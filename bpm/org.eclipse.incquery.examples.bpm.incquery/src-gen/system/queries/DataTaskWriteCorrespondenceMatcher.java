package system.queries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.rete.misc.DeltaMonitor;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;
import process.Task;
import system.Data;
import system.queries.DataTaskWriteCorrespondenceMatch;
import system.queries.util.DataTaskWriteCorrespondenceQuerySpecification;

/**
 * Generated pattern matcher API of the system.queries.DataTaskWriteCorrespondence pattern, 
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)}, 
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link DataTaskWriteCorrespondenceMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * {@literal @}QueryBasedFeature(feature = "writingTask")
 * pattern DataTaskWriteCorrespondence(Data : Data ,Task : Task) = {
 * 	Data.writingTaskIds(Data,TaskId);
 * 	Task.id(Task,TaskId);
 * }
 * </pre></code>
 * 
 * @see DataTaskWriteCorrespondenceMatch
 * @see DataTaskWriteCorrespondenceProcessor
 * @see DataTaskWriteCorrespondenceQuerySpecification
 * 
 */
public class DataTaskWriteCorrespondenceMatcher extends BaseMatcher<DataTaskWriteCorrespondenceMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine. 
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static DataTaskWriteCorrespondenceMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    DataTaskWriteCorrespondenceMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new DataTaskWriteCorrespondenceMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    } 	
    return matcher;
  }
  
  private final static int POSITION_DATA = 0;
  
  private final static int POSITION_TASK = 1;
  
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
  public DataTaskWriteCorrespondenceMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public DataTaskWriteCorrespondenceMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @return matches represented as a DataTaskWriteCorrespondenceMatch object.
   * 
   */
  public Collection<DataTaskWriteCorrespondenceMatch> getAllMatches(final Data pData, final Task pTask) {
    return rawGetAllMatches(new Object[]{pData, pTask});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @return a match represented as a DataTaskWriteCorrespondenceMatch object, or null if no match is found.
   * 
   */
  public DataTaskWriteCorrespondenceMatch getOneArbitraryMatch(final Data pData, final Task pTask) {
    return rawGetOneArbitraryMatch(new Object[]{pData, pTask});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Data pData, final Task pTask) {
    return rawHasMatch(new Object[]{pData, pTask});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Data pData, final Task pTask) {
    return rawCountMatches(new Object[]{pData, pTask});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Data pData, final Task pTask, final IMatchProcessor<? super DataTaskWriteCorrespondenceMatch> processor) {
    rawForEachMatch(new Object[]{pData, pTask}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.  
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param processor the action that will process the selected match. 
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Data pData, final Task pTask, final IMatchProcessor<? super DataTaskWriteCorrespondenceMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pData, pTask}, processor);
  }
  
  /**
   * Registers a new filtered delta monitor on this pattern matcher.
   * The DeltaMonitor can be used to track changes (delta) in the set of filtered pattern matches from now on, considering those matches only that conform to the given fixed values of some parameters. 
   * It can also be reset to track changes from a later point in time, 
   * and changes can even be acknowledged on an individual basis. 
   * See {@link DeltaMonitor} for details.
   * @param fillAtStart if true, all current matches are reported as new match events; if false, the delta monitor starts empty.
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @return the delta monitor.
   * @deprecated use the IncQuery Databinding API (IncQueryObservables) instead.
   * 
   */
  @Deprecated
  public DeltaMonitor<DataTaskWriteCorrespondenceMatch> newFilteredDeltaMonitor(final boolean fillAtStart, final Data pData, final Task pTask) {
    return rawNewFilteredDeltaMonitor(fillAtStart, new Object[]{pData, pTask});
  }
  
  /**
   * Returns a new (partial) Match object for the matcher. 
   * This can be used e.g. to call the matcher with a partial match. 
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public DataTaskWriteCorrespondenceMatch newMatch(final Data pData, final Task pTask) {
    return new DataTaskWriteCorrespondenceMatch.Immutable(pData, pTask);
    
  }
  
  /**
   * Retrieve the set of values that occur in matches for Data.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Data> rawAccumulateAllValuesOfData(final Object[] parameters) {
    Set<Data> results = new HashSet<Data>();
    rawAccumulateAllValues(POSITION_DATA, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Data.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Data> getAllValuesOfData() {
    return rawAccumulateAllValuesOfData(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Data.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Data> getAllValuesOfData(final DataTaskWriteCorrespondenceMatch partialMatch) {
    return rawAccumulateAllValuesOfData(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Data.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Data> getAllValuesOfData(final Task pTask) {
    return rawAccumulateAllValuesOfData(new Object[]{null, pTask});
  }
  
  /**
   * Retrieve the set of values that occur in matches for Task.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Task> rawAccumulateAllValuesOfTask(final Object[] parameters) {
    Set<Task> results = new HashSet<Task>();
    rawAccumulateAllValues(POSITION_TASK, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Task.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfTask() {
    return rawAccumulateAllValuesOfTask(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Task.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfTask(final DataTaskWriteCorrespondenceMatch partialMatch) {
    return rawAccumulateAllValuesOfTask(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Task.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfTask(final Data pData) {
    return rawAccumulateAllValuesOfTask(new Object[]{pData, null});
  }
  
  @Override
  protected DataTaskWriteCorrespondenceMatch tupleToMatch(final Tuple t) {
    try {
    	return new DataTaskWriteCorrespondenceMatch.Immutable((system.Data) t.get(POSITION_DATA), (process.Task) t.get(POSITION_TASK));	
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in tuple not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  protected DataTaskWriteCorrespondenceMatch arrayToMatch(final Object[] match) {
    try {
    	return new DataTaskWriteCorrespondenceMatch.Immutable((system.Data) match[POSITION_DATA], (process.Task) match[POSITION_TASK]);
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  @Override
  protected DataTaskWriteCorrespondenceMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return new DataTaskWriteCorrespondenceMatch.Mutable((system.Data) match[POSITION_DATA], (process.Task) match[POSITION_TASK]);
    } catch(ClassCastException e) {engine.getLogger().error("Element(s) in array not properly typed!",e);	//throw new IncQueryRuntimeException(e.getMessage());
    	return null;
    }
    
  }
  
  /**
   * @return the singleton instance of the query specification of this pattern
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IQuerySpecification<DataTaskWriteCorrespondenceMatcher> querySpecification() throws IncQueryException {
    return DataTaskWriteCorrespondenceQuerySpecification.instance();
  }
}
