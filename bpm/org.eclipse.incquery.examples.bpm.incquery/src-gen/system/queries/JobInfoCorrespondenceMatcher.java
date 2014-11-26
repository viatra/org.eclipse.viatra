package system.queries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import operation.RuntimeInformation;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.tuple.Tuple;
import org.eclipse.incquery.runtime.util.IncQueryLoggingUtil;
import system.Job;
import system.queries.JobInfoCorrespondenceMatch;
import system.queries.util.JobInfoCorrespondenceQuerySpecification;

/**
 * Generated pattern matcher API of the system.queries.JobInfoCorrespondence pattern,
 * providing pattern-specific query methods.
 * 
 * <p>Use the pattern matcher on a given model via {@link #on(IncQueryEngine)},
 * e.g. in conjunction with {@link IncQueryEngine#on(Notifier)}.
 * 
 * <p>Matches of the pattern will be represented as {@link JobInfoCorrespondenceMatch}.
 * 
 * <p>Original source:
 * <code><pre>
 * Job.info relation 
 * {@literal @}QueryBasedFeature(feature = "info")
 * pattern JobInfoCorrespondence(Job : Job, Info : RuntimeInformation) = {
 * 	ChecklistEntry.info(CLE, Info);
 * 	ChecklistEntry.jobs(CLE, Job);
 * }
 * </pre></code>
 * 
 * @see JobInfoCorrespondenceMatch
 * @see JobInfoCorrespondenceProcessor
 * @see JobInfoCorrespondenceQuerySpecification
 * 
 */
@SuppressWarnings("all")
public class JobInfoCorrespondenceMatcher extends BaseMatcher<JobInfoCorrespondenceMatch> {
  /**
   * Initializes the pattern matcher within an existing EMF-IncQuery engine.
   * If the pattern matcher is already constructed in the engine, only a light-weight reference is returned.
   * The match set will be incrementally refreshed upon updates.
   * @param engine the existing EMF-IncQuery engine in which this matcher will be created.
   * @throws IncQueryException if an error occurs during pattern matcher creation
   * 
   */
  public static JobInfoCorrespondenceMatcher on(final IncQueryEngine engine) throws IncQueryException {
    // check if matcher already exists
    JobInfoCorrespondenceMatcher matcher = engine.getExistingMatcher(querySpecification());
    if (matcher == null) {
    	matcher = new JobInfoCorrespondenceMatcher(engine);
    	// do not have to "put" it into engine.matchers, reportMatcherInitialized() will take care of it
    }
    return matcher;
  }
  
  private final static int POSITION_JOB = 0;
  
  private final static int POSITION_INFO = 1;
  
  private final static Logger LOGGER = IncQueryLoggingUtil.getLogger(JobInfoCorrespondenceMatcher.class);
  
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
  public JobInfoCorrespondenceMatcher(final Notifier emfRoot) throws IncQueryException {
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
  public JobInfoCorrespondenceMatcher(final IncQueryEngine engine) throws IncQueryException {
    super(engine, querySpecification());
  }
  
  /**
   * Returns the set of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pInfo the fixed value of pattern parameter Info, or null if not bound.
   * @return matches represented as a JobInfoCorrespondenceMatch object.
   * 
   */
  public Collection<JobInfoCorrespondenceMatch> getAllMatches(final Job pJob, final RuntimeInformation pInfo) {
    return rawGetAllMatches(new Object[]{pJob, pInfo});
  }
  
  /**
   * Returns an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pInfo the fixed value of pattern parameter Info, or null if not bound.
   * @return a match represented as a JobInfoCorrespondenceMatch object, or null if no match is found.
   * 
   */
  public JobInfoCorrespondenceMatch getOneArbitraryMatch(final Job pJob, final RuntimeInformation pInfo) {
    return rawGetOneArbitraryMatch(new Object[]{pJob, pInfo});
  }
  
  /**
   * Indicates whether the given combination of specified pattern parameters constitute a valid pattern match,
   * under any possible substitution of the unspecified parameters (if any).
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pInfo the fixed value of pattern parameter Info, or null if not bound.
   * @return true if the input is a valid (partial) match of the pattern.
   * 
   */
  public boolean hasMatch(final Job pJob, final RuntimeInformation pInfo) {
    return rawHasMatch(new Object[]{pJob, pInfo});
  }
  
  /**
   * Returns the number of all matches of the pattern that conform to the given fixed values of some parameters.
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pInfo the fixed value of pattern parameter Info, or null if not bound.
   * @return the number of pattern matches found.
   * 
   */
  public int countMatches(final Job pJob, final RuntimeInformation pInfo) {
    return rawCountMatches(new Object[]{pJob, pInfo});
  }
  
  /**
   * Executes the given processor on each match of the pattern that conforms to the given fixed values of some parameters.
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pInfo the fixed value of pattern parameter Info, or null if not bound.
   * @param processor the action that will process each pattern match.
   * 
   */
  public void forEachMatch(final Job pJob, final RuntimeInformation pInfo, final IMatchProcessor<? super JobInfoCorrespondenceMatch> processor) {
    rawForEachMatch(new Object[]{pJob, pInfo}, processor);
  }
  
  /**
   * Executes the given processor on an arbitrarily chosen match of the pattern that conforms to the given fixed values of some parameters.
   * Neither determinism nor randomness of selection is guaranteed.
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pInfo the fixed value of pattern parameter Info, or null if not bound.
   * @param processor the action that will process the selected match.
   * @return true if the pattern has at least one match with the given parameter values, false if the processor was not invoked
   * 
   */
  public boolean forOneArbitraryMatch(final Job pJob, final RuntimeInformation pInfo, final IMatchProcessor<? super JobInfoCorrespondenceMatch> processor) {
    return rawForOneArbitraryMatch(new Object[]{pJob, pInfo}, processor);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pInfo the fixed value of pattern parameter Info, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public JobInfoCorrespondenceMatch newMatch(final Job pJob, final RuntimeInformation pInfo) {
    return JobInfoCorrespondenceMatch.newMatch(pJob, pInfo);
  }
  
  /**
   * Retrieve the set of values that occur in matches for Job.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<Job> rawAccumulateAllValuesOfJob(final Object[] parameters) {
    Set<Job> results = new HashSet<Job>();
    rawAccumulateAllValues(POSITION_JOB, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Job.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Job> getAllValuesOfJob() {
    return rawAccumulateAllValuesOfJob(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Job.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Job> getAllValuesOfJob(final JobInfoCorrespondenceMatch partialMatch) {
    return rawAccumulateAllValuesOfJob(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Job.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<Job> getAllValuesOfJob(final RuntimeInformation pInfo) {
    return rawAccumulateAllValuesOfJob(new Object[]{
    null, 
    pInfo
    });
  }
  
  /**
   * Retrieve the set of values that occur in matches for Info.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  protected Set<RuntimeInformation> rawAccumulateAllValuesOfInfo(final Object[] parameters) {
    Set<RuntimeInformation> results = new HashSet<RuntimeInformation>();
    rawAccumulateAllValues(POSITION_INFO, parameters, results);
    return results;
  }
  
  /**
   * Retrieve the set of values that occur in matches for Info.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<RuntimeInformation> getAllValuesOfInfo() {
    return rawAccumulateAllValuesOfInfo(emptyArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Info.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<RuntimeInformation> getAllValuesOfInfo(final JobInfoCorrespondenceMatch partialMatch) {
    return rawAccumulateAllValuesOfInfo(partialMatch.toArray());
  }
  
  /**
   * Retrieve the set of values that occur in matches for Info.
   * @return the Set of all values, null if no parameter with the given name exists, empty set if there are no matches
   * 
   */
  public Set<RuntimeInformation> getAllValuesOfInfo(final Job pJob) {
    return rawAccumulateAllValuesOfInfo(new Object[]{
    pJob, 
    null
    });
  }
  
  @Override
  protected JobInfoCorrespondenceMatch tupleToMatch(final Tuple t) {
    try {
    	return JobInfoCorrespondenceMatch.newMatch((system.Job) t.get(POSITION_JOB), (operation.RuntimeInformation) t.get(POSITION_INFO));
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in tuple not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected JobInfoCorrespondenceMatch arrayToMatch(final Object[] match) {
    try {
    	return JobInfoCorrespondenceMatch.newMatch((system.Job) match[POSITION_JOB], (operation.RuntimeInformation) match[POSITION_INFO]);
    } catch(ClassCastException e) {
    	LOGGER.error("Element(s) in array not properly typed!",e);
    	return null;
    }
  }
  
  @Override
  protected JobInfoCorrespondenceMatch arrayToMatchMutable(final Object[] match) {
    try {
    	return JobInfoCorrespondenceMatch.newMutableMatch((system.Job) match[POSITION_JOB], (operation.RuntimeInformation) match[POSITION_INFO]);
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
  public static IQuerySpecification<JobInfoCorrespondenceMatcher> querySpecification() throws IncQueryException {
    return JobInfoCorrespondenceQuerySpecification.instance();
  }
}
