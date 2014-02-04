package org.eclipse.incquery.examples.bpm.queries;

import java.util.Arrays;
import java.util.List;
import operation.ChecklistEntry;
import org.eclipse.incquery.examples.bpm.queries.util.EntryTaskQuerySpecification;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import process.Task;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.examples.bpm.queries.entryTask pattern,
 * to be used in conjunction with {@link EntryTaskMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see EntryTaskMatcher
 * @see EntryTaskProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class EntryTaskMatch extends BasePatternMatch {
  private ChecklistEntry fEntry;
  
  private Task fTask;
  
  private static List<String> parameterNames = makeImmutableList("Entry", "Task");
  
  private EntryTaskMatch(final ChecklistEntry pEntry, final Task pTask) {
    this.fEntry = pEntry;
    this.fTask = pTask;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Entry".equals(parameterName)) return this.fEntry;
    if ("Task".equals(parameterName)) return this.fTask;
    return null;
    
  }
  
  public ChecklistEntry getEntry() {
    return this.fEntry;
    
  }
  
  public Task getTask() {
    return this.fTask;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Entry".equals(parameterName) ) {
    	this.fEntry = (operation.ChecklistEntry) newValue;
    	return true;
    }
    if ("Task".equals(parameterName) ) {
    	this.fTask = (process.Task) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setEntry(final ChecklistEntry pEntry) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fEntry = pEntry;
    
  }
  
  public void setTask(final Task pTask) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fTask = pTask;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.examples.bpm.queries.entryTask";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return EntryTaskMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fEntry, fTask};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Entry\"=" + prettyPrintValue(fEntry) + ", ");
    result.append("\"Task\"=" + prettyPrintValue(fTask));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fEntry == null) ? 0 : fEntry.hashCode());
    result = prime * result + ((fTask == null) ? 0 : fTask.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof EntryTaskMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    EntryTaskMatch other = (EntryTaskMatch) obj;
    if (fEntry == null) {if (other.fEntry != null) return false;}
    else if (!fEntry.equals(other.fEntry)) return false;
    if (fTask == null) {if (other.fTask != null) return false;}
    else if (!fTask.equals(other.fTask)) return false;
    return true;
  }
  
  @Override
  public EntryTaskQuerySpecification specification() {
    try {
    	return EntryTaskQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends EntryTaskMatch {
    Mutable(final ChecklistEntry pEntry, final Task pTask) {
      super(pEntry, pTask);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends EntryTaskMatch {
    Immutable(final ChecklistEntry pEntry, final Task pTask) {
      super(pEntry, pTask);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
