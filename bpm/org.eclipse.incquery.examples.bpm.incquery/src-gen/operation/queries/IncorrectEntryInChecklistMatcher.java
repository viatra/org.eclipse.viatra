package operation.queries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import operation.ChecklistEntry;
import operation.queries.IncorrectEntryInChecklistMatch;
import operation.queries.util.IncorrectEntryInChecklistQuerySpecification;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import process.Task;

/**
 * Generated pattern matcher API of the operation.queries.IncorrectEntryInChecklist pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link IncorrectEntryInChecklistMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * // validation query (checklist entries are connected to tasks in the process of the checklist)
 * {@literal @}Constraint(location = ChecklistEntry,
 * 	message = "Entry $ChecklistEntry.name$ corresponds to Task $Task.name$ outside of process $Process.name$ defined for the checklist!",
 * 	severity = "error")
 * pattern IncorrectEntryInChecklist(ChecklistEntry,Task,Process) = {
 * 	Checklist.entries(Checklist,ChecklistEntry);
 * 	find ChecklistProcessCorrespondence(Checklist,Process);
 * 	find ChecklistEntryTaskCorrespondence(ChecklistEntry,Task);
 * 	neg find TaskInProcess(Task,Process);
 * }
 * </pre></code>
 * 
 * @see IncorrectEntryInChecklistMatch
 * @see IncorrectEntryInChecklistProcessor
 * @see IncorrectEntryInChecklistQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class IncorrectEntryInChecklistMatcher extends BaseMatcher<IncorrectEntryInChecklistMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static IncorrectEntryInChecklistMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    IncorrectEntryInChecklistMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new IncorrectEntryInChecklistMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_CHECKLISTENTRY = 0;
  
  private final static int POSITION_TASK = 1;
  
  private final static int POSITION_PROCESS = 2;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(IncorrectEntryInChecklistMatcher.class);
  
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
  public IncorrectEntryInChecklistMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public IncorrectEntryInChecklistMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pChecklistEntry the fixed value of pattern parameter ChecklistEntry, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return matches represented as a IncorrectEntryInChecklistMatch object.
   * 
   */
  public Collection<IncorrectEntryInChecklistMatch> getAllMatches(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess) {
    return rawGetAllMatches(new Object[]{pChecklistEntry, pTask, pProcess});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pChecklistEntry the fixed value of pattern parameter ChecklistEntry, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return a match represented as a IncorrectEntryInChecklistMatch object, or null if no match is found.
   * 
   */
  public IncorrectEntryInChecklistMatch getOneArbitraryMatch(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess) {
    return rawGetOneArbitraryMatch(new Object[]{pChecklistEntry, pTask, pProcess});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pChecklistEntry the fixed value of pattern parameter ChecklistEntry, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess) {
    return rawHasMatch(new Object[]{pChecklistEntry, pTask, pProcess});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pChecklistEntry the fixed value of pattern parameter ChecklistEntry, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess) {
    return rawCountMatches(new Object[]{pChecklistEntry, pTask, pProcess});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pChecklistEntry the fixed value of pattern parameter ChecklistEntry, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess, final IMatchProcessor<? super IncorrectEntryInChecklistMatch> processor) {
    rawForEachMatch(new Object[]{pChecklistEntry, pTask, pProcess}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pChecklistEntry the fixed value of pattern parameter ChecklistEntry, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess, final IMatchProcessor<? super IncorrectEntryInChecklistMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pChecklistEntry, pTask, pProcess}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pChecklistEntry the fixed value of pattern parameter ChecklistEntry, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public IncorrectEntryInChecklistMatch newMatch(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess) {
    return IncorrectEntryInChecklistMatch.newMatch(pChecklistEntry, pTask, pProcess);
  }
  
  /**
   * Retrieve the set of values that occur in matches for ChecklistEntry.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<ChecklistEntry> rawAccumulateAllValuesOfChecklistEntry(final Object[] parameters) {
    Set<ChecklistEntry> results = new HashSet<ChecklistEntry>();
    rawAccumulateAllValues(POSITION_CHECKLISTENTRY, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for ChecklistEntry.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<ChecklistEntry> getAllValuesOfChecklistEntry() {
    return rawAccumulateAllValuesOfChecklistEntry(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ChecklistEntry.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<ChecklistEntry> getAllValuesOfChecklistEntry(final IncorrectEntryInChecklistMatch partialMatch) {
    return rawAccumulateAllValuesOfChecklistEntry(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for ChecklistEntry.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<ChecklistEntry> getAllValuesOfChecklistEntry(final Task pTask, final process.Process pProcess) {
    return rawAccumulateAllValuesOfChecklistEntry(new Object[]{
    null, 
    pTask, 
    pProcess
    });
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
  public Set<Task> getAllValuesOfTask(final IncorrectEntryInChecklistMatch partialMatch) {
    return rawAccumulateAllValuesOfTask(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Task.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Task> getAllValuesOfTask(final ChecklistEntry pChecklistEntry, final process.Process pProcess) {
    return rawAccumulateAllValuesOfTask(new Object[]{
    pChecklistEntry, 
    null, 
    pProcess
    });
  }
  
  /**
   * Retrieve the set of values that occur in matches for Process.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<process.Process> rawAccumulateAllValuesOfProcess(final Object[] parameters) {
    Set<process.Process> results = new HashSet<process.Process>();
    rawAccumulateAllValues(POSITION_PROCESS, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Process.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<process.Process> getAllValuesOfProcess() {
    return rawAccumulateAllValuesOfProcess(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Process.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<process.Process> getAllValuesOfProcess(final IncorrectEntryInChecklistMatch partialMatch) {
    return rawAccumulateAllValuesOfProcess(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Process.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<process.Process> getAllValuesOfProcess(final ChecklistEntry pChecklistEntry, final Task pTask) {
    return rawAccumulateAllValuesOfProcess(new Object[]{
    pChecklistEntry, 
    pTask, 
    null
    });
  }
  
  @Override
  protected IncorrectEntryInChecklistMatch tupleToMatch(final Tuple t) {
    try {
    	return IncorrectEntryInChecklistMatch.newMatch((operation.ChecklistEntry) t.get(POSITION_CHECKLISTENTRY), (process.Task) t.get(POSITION_TASK), (process.Process) t.get(POSITION_PROCESS));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected IncorrectEntryInChecklistMatch arrayToMatch(final Object[] match) {
    try {
    	return IncorrectEntryInChecklistMatch.newMatch((operation.ChecklistEntry) match[POSITION_CHECKLISTENTRY], (process.Task) match[POSITION_TASK], (process.Process) match[POSITION_PROCESS]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected IncorrectEntryInChecklistMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return IncorrectEntryInChecklistMatch.newMutableMatch((operation.ChecklistEntry) match[POSITION_CHECKLISTENTRY], (process.Task) match[POSITION_TASK], (process.Process) match[POSITION_PROCESS]);
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
  public static IQuerySpecification<IncorrectEntryInChecklistMatcher> querySpecification() throws IncQueryException {
    return IncorrectEntryInChecklistQuerySpecification.instance();
  }
}
