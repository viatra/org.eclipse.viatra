package org.eclipse.incquery.examples.bpm.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.examples.bpm.queries.util.ProcessTasksQuerySpecification;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import process.Activity;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.examples.bpm.queries.processTasks pattern,
 * to be used in conjunction with {@link ProcessTasksMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ProcessTasksMatcher
 * @see ProcessTasksProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ProcessTasksMatch extends BasePatternMatch {
  private process.Process fProc;
  
  private Activity fTask;
  
  private static List<String> parameterNames = makeImmutableList("Proc", "Task");
  
  private ProcessTasksMatch(final process.Process pProc, final Activity pTask) {
    this.fProc = pProc;
    this.fTask = pTask;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Proc".equals(parameterName)) return this.fProc;
    if ("Task".equals(parameterName)) return this.fTask;
    return null;
    
  }
  
  public process.Process getProc() {
    return this.fProc;
    
  }
  
  public Activity getTask() {
    return this.fTask;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Proc".equals(parameterName) ) {
    	this.fProc = (process.Process) newValue;
    	return true;
    }
    if ("Task".equals(parameterName) ) {
    	this.fTask = (process.Activity) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setProc(final process.Process pProc) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fProc = pProc;
    
  }
  
  public void setTask(final Activity pTask) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fTask = pTask;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.examples.bpm.queries.processTasks";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return ProcessTasksMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fProc, fTask};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Proc\"=" + prettyPrintValue(fProc) + ", ");
    result.append("\"Task\"=" + prettyPrintValue(fTask));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fProc == null) ? 0 : fProc.hashCode());
    result = prime * result + ((fTask == null) ? 0 : fTask.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ProcessTasksMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    ProcessTasksMatch other = (ProcessTasksMatch) obj;
    if (fProc == null) {if (other.fProc != null) return false;}
    else if (!fProc.equals(other.fProc)) return false;
    if (fTask == null) {if (other.fTask != null) return false;}
    else if (!fTask.equals(other.fTask)) return false;
    return true;
  }
  
  @Override
  public ProcessTasksQuerySpecification specification() {
    try {
    	return ProcessTasksQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends ProcessTasksMatch {
    Mutable(final process.Process pProc, final Activity pTask) {
      super(pProc, pTask);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends ProcessTasksMatch {
    Immutable(final process.Process pProc, final Activity pTask) {
      super(pProc, pTask);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
