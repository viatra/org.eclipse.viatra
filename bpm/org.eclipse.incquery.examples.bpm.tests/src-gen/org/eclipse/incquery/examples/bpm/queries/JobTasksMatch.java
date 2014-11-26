package org.eclipse.incquery.examples.bpm.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.examples.bpm.queries.util.JobTasksQuerySpecification;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import process.Task;
import system.Job;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.examples.bpm.queries.jobTasks pattern,
 * to be used in conjunction with {@link JobTasksMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see JobTasksMatcher
 * @see JobTasksProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class JobTasksMatch extends BasePatternMatch {
  private Job fJob;
  
  private Task fTask;
  
  private static List<String> parameterNames = makeImmutableList("Job", "Task");
  
  private JobTasksMatch(final Job pJob, final Task pTask) {
    this.fJob = pJob;
    this.fTask = pTask;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Job".equals(parameterName)) return this.fJob;
    if ("Task".equals(parameterName)) return this.fTask;
    return null;
  }
  
  public Job getJob() {
    return this.fJob;
  }
  
  public Task getTask() {
    return this.fTask;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Job".equals(parameterName) ) {
    	this.fJob = (system.Job) newValue;
    	return true;
    }
    if ("Task".equals(parameterName) ) {
    	this.fTask = (process.Task) newValue;
    	return true;
    }
    return false;
  }
  
  public void setJob(final Job pJob) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fJob = pJob;
  }
  
  public void setTask(final Task pTask) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fTask = pTask;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.examples.bpm.queries.jobTasks";
  }
  
  @Override
  public List<String> parameterNames() {
    return JobTasksMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fJob, fTask};
  }
  
  @Override
  public JobTasksMatch toImmutable() {
    return isMutable() ? newMatch(fJob, fTask) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Job\"=" + prettyPrintValue(fJob) + ", ");
    
    result.append("\"Task\"=" + prettyPrintValue(fTask)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fJob == null) ? 0 : fJob.hashCode());
    result = prime * result + ((fTask == null) ? 0 : fTask.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof JobTasksMatch)) { // this should be infrequent
    	if (obj == null) {
    		return false;
    	}
    	if (!(obj instanceof IPatternMatch)) {
    		return false;
    	}
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    JobTasksMatch other = (JobTasksMatch) obj;
    if (fJob == null) {if (other.fJob != null) return false;}
    else if (!fJob.equals(other.fJob)) return false;
    if (fTask == null) {if (other.fTask != null) return false;}
    else if (!fTask.equals(other.fTask)) return false;
    return true;
  }
  
  @Override
  public JobTasksQuerySpecification specification() {
    try {
    	return JobTasksQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException (ex);
    }
  }
  
  /**
   * Returns an empty, mutable match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @return the empty match.
   * 
   */
  public static JobTasksMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static JobTasksMatch newMutableMatch(final Job pJob, final Task pTask) {
    return new Mutable(pJob, pTask);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static JobTasksMatch newMatch(final Job pJob, final Task pTask) {
    return new Immutable(pJob, pTask);
  }
  
  private static final class Mutable extends JobTasksMatch {
    Mutable(final Job pJob, final Task pTask) {
      super(pJob, pTask);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends JobTasksMatch {
    Immutable(final Job pJob, final Task pTask) {
      super(pJob, pTask);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
