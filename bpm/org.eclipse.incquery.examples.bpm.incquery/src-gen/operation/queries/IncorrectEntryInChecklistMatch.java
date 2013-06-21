package operation.queries;

import java.util.Arrays;
import java.util.List;
import operation.ChecklistEntry;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import process.Task;

/**
 * Pattern-specific match representation of the operation.queries.IncorrectEntryInChecklist pattern, 
 * to be used in conjunction with {@link IncorrectEntryInChecklistMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see IncorrectEntryInChecklistMatcher
 * @see IncorrectEntryInChecklistProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class IncorrectEntryInChecklistMatch extends BasePatternMatch {
  private ChecklistEntry fChecklistEntry;
  
  private Task fTask;
  
  private process.Process fProcess;
  
  private static List<String> parameterNames = makeImmutableList("ChecklistEntry", "Task", "Process");
  
  private IncorrectEntryInChecklistMatch(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess) {
    this.fChecklistEntry = pChecklistEntry;
    this.fTask = pTask;
    this.fProcess = pProcess;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("ChecklistEntry".equals(parameterName)) return this.fChecklistEntry;
    if ("Task".equals(parameterName)) return this.fTask;
    if ("Process".equals(parameterName)) return this.fProcess;
    return null;
    
  }
  
  public ChecklistEntry getChecklistEntry() {
    return this.fChecklistEntry;
    
  }
  
  public Task getTask() {
    return this.fTask;
    
  }
  
  public process.Process getProcess() {
    return this.fProcess;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("ChecklistEntry".equals(parameterName) ) {
    	this.fChecklistEntry = (operation.ChecklistEntry) newValue;
    	return true;
    }
    if ("Task".equals(parameterName) ) {
    	this.fTask = (process.Task) newValue;
    	return true;
    }
    if ("Process".equals(parameterName) ) {
    	this.fProcess = (process.Process) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setChecklistEntry(final ChecklistEntry pChecklistEntry) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fChecklistEntry = pChecklistEntry;
    
  }
  
  public void setTask(final Task pTask) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fTask = pTask;
    
  }
  
  public void setProcess(final process.Process pProcess) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fProcess = pProcess;
    
  }
  
  @Override
  public String patternName() {
    return "operation.queries.IncorrectEntryInChecklist";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return IncorrectEntryInChecklistMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fChecklistEntry, fTask, fProcess};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"ChecklistEntry\"=" + prettyPrintValue(fChecklistEntry) + ", ");
    result.append("\"Task\"=" + prettyPrintValue(fTask) + ", ");
    result.append("\"Process\"=" + prettyPrintValue(fProcess));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fChecklistEntry == null) ? 0 : fChecklistEntry.hashCode()); 
    result = prime * result + ((fTask == null) ? 0 : fTask.hashCode()); 
    result = prime * result + ((fProcess == null) ? 0 : fProcess.hashCode()); 
    return result; 
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof IncorrectEntryInChecklistMatch)) { // this should be infrequent				
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!pattern().equals(otherSig.pattern()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    IncorrectEntryInChecklistMatch other = (IncorrectEntryInChecklistMatch) obj;
    if (fChecklistEntry == null) {if (other.fChecklistEntry != null) return false;}
    else if (!fChecklistEntry.equals(other.fChecklistEntry)) return false;
    if (fTask == null) {if (other.fTask != null) return false;}
    else if (!fTask.equals(other.fTask)) return false;
    if (fProcess == null) {if (other.fProcess != null) return false;}
    else if (!fProcess.equals(other.fProcess)) return false;
    return true;
  }
  
  @Override
  public Pattern pattern() {
    try {
    	return IncorrectEntryInChecklistMatcher.querySpecification().getPattern();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends IncorrectEntryInChecklistMatch {
    Mutable(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess) {
      super(pChecklistEntry, pTask, pProcess);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends IncorrectEntryInChecklistMatch {
    Immutable(final ChecklistEntry pChecklistEntry, final Task pTask, final process.Process pProcess) {
      super(pChecklistEntry, pTask, pProcess);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
