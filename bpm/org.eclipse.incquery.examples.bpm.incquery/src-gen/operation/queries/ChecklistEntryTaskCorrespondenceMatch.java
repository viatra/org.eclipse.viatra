package operation.queries;

import java.util.Arrays;
import java.util.List;
import operation.ChecklistEntry;
import operation.queries.util.ChecklistEntryTaskCorrespondenceQuerySpecification;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import process.Task;

/**
 * Pattern-specific match representation of the operation.queries.ChecklistEntryTaskCorrespondence pattern,
 * to be used in conjunction with {@link ChecklistEntryTaskCorrespondenceMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ChecklistEntryTaskCorrespondenceMatcher
 * @see ChecklistEntryTaskCorrespondenceProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ChecklistEntryTaskCorrespondenceMatch extends BasePatternMatch {
  private ChecklistEntry fCLE;
  
  private Task fTask;
  
  private static List<String> parameterNames = makeImmutableList("CLE", "Task");
  
  private ChecklistEntryTaskCorrespondenceMatch(final ChecklistEntry pCLE, final Task pTask) {
    this.fCLE = pCLE;
    this.fTask = pTask;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("CLE".equals(parameterName)) return this.fCLE;
    if ("Task".equals(parameterName)) return this.fTask;
    return null;
    
  }
  
  public ChecklistEntry getCLE() {
    return this.fCLE;
    
  }
  
  public Task getTask() {
    return this.fTask;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("CLE".equals(parameterName) ) {
    	this.fCLE = (operation.ChecklistEntry) newValue;
    	return true;
    }
    if ("Task".equals(parameterName) ) {
    	this.fTask = (process.Task) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setCLE(final ChecklistEntry pCLE) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fCLE = pCLE;
    
  }
  
  public void setTask(final Task pTask) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fTask = pTask;
    
  }
  
  @Override
  public String patternName() {
    return "operation.queries.ChecklistEntryTaskCorrespondence";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return ChecklistEntryTaskCorrespondenceMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fCLE, fTask};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"CLE\"=" + prettyPrintValue(fCLE) + ", ");
    result.append("\"Task\"=" + prettyPrintValue(fTask));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fCLE == null) ? 0 : fCLE.hashCode());
    result = prime * result + ((fTask == null) ? 0 : fTask.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ChecklistEntryTaskCorrespondenceMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    ChecklistEntryTaskCorrespondenceMatch other = (ChecklistEntryTaskCorrespondenceMatch) obj;
    if (fCLE == null) {if (other.fCLE != null) return false;}
    else if (!fCLE.equals(other.fCLE)) return false;
    if (fTask == null) {if (other.fTask != null) return false;}
    else if (!fTask.equals(other.fTask)) return false;
    return true;
  }
  
  @Override
  public ChecklistEntryTaskCorrespondenceQuerySpecification specification() {
    try {
    	return ChecklistEntryTaskCorrespondenceQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends ChecklistEntryTaskCorrespondenceMatch {
    Mutable(final ChecklistEntry pCLE, final Task pTask) {
      super(pCLE, pTask);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends ChecklistEntryTaskCorrespondenceMatch {
    Immutable(final ChecklistEntry pCLE, final Task pTask) {
      super(pCLE, pTask);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
