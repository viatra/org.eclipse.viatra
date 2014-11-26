package operation.queries;

import java.util.Arrays;
import java.util.List;
import operation.ChecklistEntry;
import operation.queries.util.DataReadByChecklistEntryQuerySpecification;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import process.Task;
import system.Data;

/**
 * Pattern-specific match representation of the operation.queries.DataReadByChecklistEntry pattern,
 * to be used in conjunction with {@link DataReadByChecklistEntryMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see DataReadByChecklistEntryMatcher
 * @see DataReadByChecklistEntryProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class DataReadByChecklistEntryMatch extends BasePatternMatch {
  private ChecklistEntry fCLE;
  
  private Task fTask;
  
  private Data fData;
  
  private static List<String> parameterNames = makeImmutableList("CLE", "Task", "Data");
  
  private DataReadByChecklistEntryMatch(final ChecklistEntry pCLE, final Task pTask, final Data pData) {
    this.fCLE = pCLE;
    this.fTask = pTask;
    this.fData = pData;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("CLE".equals(parameterName)) return this.fCLE;
    if ("Task".equals(parameterName)) return this.fTask;
    if ("Data".equals(parameterName)) return this.fData;
    return null;
  }
  
  public ChecklistEntry getCLE() {
    return this.fCLE;
  }
  
  public Task getTask() {
    return this.fTask;
  }
  
  public Data getData() {
    return this.fData;
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
    if ("Data".equals(parameterName) ) {
    	this.fData = (system.Data) newValue;
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
  
  public void setData(final Data pData) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fData = pData;
  }
  
  @Override
  public String patternName() {
    return "operation.queries.DataReadByChecklistEntry";
  }
  
  @Override
  public List<String> parameterNames() {
    return DataReadByChecklistEntryMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fCLE, fTask, fData};
  }
  
  @Override
  public DataReadByChecklistEntryMatch toImmutable() {
    return isMutable() ? newMatch(fCLE, fTask, fData) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"CLE\"=" + prettyPrintValue(fCLE) + ", ");
    
    result.append("\"Task\"=" + prettyPrintValue(fTask) + ", ");
    
    result.append("\"Data\"=" + prettyPrintValue(fData)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fCLE == null) ? 0 : fCLE.hashCode());
    result = prime * result + ((fTask == null) ? 0 : fTask.hashCode());
    result = prime * result + ((fData == null) ? 0 : fData.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof DataReadByChecklistEntryMatch)) { // this should be infrequent
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
    DataReadByChecklistEntryMatch other = (DataReadByChecklistEntryMatch) obj;
    if (fCLE == null) {if (other.fCLE != null) return false;}
    else if (!fCLE.equals(other.fCLE)) return false;
    if (fTask == null) {if (other.fTask != null) return false;}
    else if (!fTask.equals(other.fTask)) return false;
    if (fData == null) {if (other.fData != null) return false;}
    else if (!fData.equals(other.fData)) return false;
    return true;
  }
  
  @Override
  public DataReadByChecklistEntryQuerySpecification specification() {
    try {
    	return DataReadByChecklistEntryQuerySpecification.instance();
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
  public static DataReadByChecklistEntryMatch newEmptyMatch() {
    return new Mutable(null, null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pCLE the fixed value of pattern parameter CLE, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static DataReadByChecklistEntryMatch newMutableMatch(final ChecklistEntry pCLE, final Task pTask, final Data pData) {
    return new Mutable(pCLE, pTask, pData);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pCLE the fixed value of pattern parameter CLE, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pData the fixed value of pattern parameter Data, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static DataReadByChecklistEntryMatch newMatch(final ChecklistEntry pCLE, final Task pTask, final Data pData) {
    return new Immutable(pCLE, pTask, pData);
  }
  
  private static final class Mutable extends DataReadByChecklistEntryMatch {
    Mutable(final ChecklistEntry pCLE, final Task pTask, final Data pData) {
      super(pCLE, pTask, pData);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends DataReadByChecklistEntryMatch {
    Immutable(final ChecklistEntry pCLE, final Task pTask, final Data pData) {
      super(pCLE, pTask, pData);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
