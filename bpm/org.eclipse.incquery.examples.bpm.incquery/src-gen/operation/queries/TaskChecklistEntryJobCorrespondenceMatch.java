package operation.queries;

import java.util.Arrays;
import java.util.List;
import operation.ChecklistEntry;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import process.Task;
import system.Job;

/**
 * Pattern-specific match representation of the operation.queries.TaskChecklistEntryJobCorrespondence pattern, 
 * to be used in conjunction with {@link TaskChecklistEntryJobCorrespondenceMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see TaskChecklistEntryJobCorrespondenceMatcher
 * @see TaskChecklistEntryJobCorrespondenceProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class TaskChecklistEntryJobCorrespondenceMatch extends BasePatternMatch {
  private Task fTask;
  
  private ChecklistEntry fCLE;
  
  private Job fJob;
  
  private static List<String> parameterNames = makeImmutableList("Task", "CLE", "Job");
  
  private TaskChecklistEntryJobCorrespondenceMatch(final Task pTask, final ChecklistEntry pCLE, final Job pJob) {
    this.fTask = pTask;
    this.fCLE = pCLE;
    this.fJob = pJob;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Task".equals(parameterName)) return this.fTask;
    if ("CLE".equals(parameterName)) return this.fCLE;
    if ("Job".equals(parameterName)) return this.fJob;
    return null;
    
  }
  
  public Task getTask() {
    return this.fTask;
    
  }
  
  public ChecklistEntry getCLE() {
    return this.fCLE;
    
  }
  
  public Job getJob() {
    return this.fJob;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Task".equals(parameterName) ) {
    	this.fTask = (process.Task) newValue;
    	return true;
    }
    if ("CLE".equals(parameterName) ) {
    	this.fCLE = (operation.ChecklistEntry) newValue;
    	return true;
    }
    if ("Job".equals(parameterName) ) {
    	this.fJob = (system.Job) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setTask(final Task pTask) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fTask = pTask;
    
  }
  
  public void setCLE(final ChecklistEntry pCLE) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fCLE = pCLE;
    
  }
  
  public void setJob(final Job pJob) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fJob = pJob;
    
  }
  
  @Override
  public String patternName() {
    return "operation.queries.TaskChecklistEntryJobCorrespondence";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return TaskChecklistEntryJobCorrespondenceMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fTask, fCLE, fJob};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Task\"=" + prettyPrintValue(fTask) + ", ");
    result.append("\"CLE\"=" + prettyPrintValue(fCLE) + ", ");
    result.append("\"Job\"=" + prettyPrintValue(fJob));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fTask == null) ? 0 : fTask.hashCode()); 
    result = prime * result + ((fCLE == null) ? 0 : fCLE.hashCode()); 
    result = prime * result + ((fJob == null) ? 0 : fJob.hashCode()); 
    return result; 
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof TaskChecklistEntryJobCorrespondenceMatch)) { // this should be infrequent				
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!pattern().equals(otherSig.pattern()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    TaskChecklistEntryJobCorrespondenceMatch other = (TaskChecklistEntryJobCorrespondenceMatch) obj;
    if (fTask == null) {if (other.fTask != null) return false;}
    else if (!fTask.equals(other.fTask)) return false;
    if (fCLE == null) {if (other.fCLE != null) return false;}
    else if (!fCLE.equals(other.fCLE)) return false;
    if (fJob == null) {if (other.fJob != null) return false;}
    else if (!fJob.equals(other.fJob)) return false;
    return true;
  }
  
  @Override
  public Pattern pattern() {
    try {
    	return TaskChecklistEntryJobCorrespondenceMatcher.querySpecification().getPattern();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends TaskChecklistEntryJobCorrespondenceMatch {
    Mutable(final Task pTask, final ChecklistEntry pCLE, final Job pJob) {
      super(pTask, pCLE, pJob);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends TaskChecklistEntryJobCorrespondenceMatch {
    Immutable(final Task pTask, final ChecklistEntry pCLE, final Job pJob) {
      super(pTask, pCLE, pJob);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
