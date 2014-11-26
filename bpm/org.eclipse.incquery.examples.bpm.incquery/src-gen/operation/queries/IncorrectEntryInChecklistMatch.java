package operation.queries;

import java.util.Arrays;
import java.util.List;
import operation.queries.util.IncorrectEntryInChecklistQuerySpecification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;

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
  private EObject fChecklistEntry;
  
  private EObject fTask;
  
  private EObject fProcess;
  
  private static List<String> parameterNames = makeImmutableList("ChecklistEntry", "Task", "Process");
  
  private IncorrectEntryInChecklistMatch(final EObject pChecklistEntry, final EObject pTask, final EObject pProcess) {
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
  
  public EObject getChecklistEntry() {
    return this.fChecklistEntry;
  }
  
  public EObject getTask() {
    return this.fTask;
  }
  
  public EObject getProcess() {
    return this.fProcess;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("ChecklistEntry".equals(parameterName) ) {
    	this.fChecklistEntry = (org.eclipse.emf.ecore.EObject) newValue;
    	return true;
    }
    if ("Task".equals(parameterName) ) {
    	this.fTask = (org.eclipse.emf.ecore.EObject) newValue;
    	return true;
    }
    if ("Process".equals(parameterName) ) {
    	this.fProcess = (org.eclipse.emf.ecore.EObject) newValue;
    	return true;
    }
    return false;
  }
  
  public void setChecklistEntry(final EObject pChecklistEntry) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fChecklistEntry = pChecklistEntry;
  }
  
  public void setTask(final EObject pTask) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fTask = pTask;
  }
  
  public void setProcess(final EObject pProcess) {
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
  public IncorrectEntryInChecklistMatch toImmutable() {
    return isMutable() ? newMatch(fChecklistEntry, fTask, fProcess) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"ChecklistEntry\"=" + prettyPrintValue(fChecklistEntry) + ", ");
    
    result.append("\"Task\"=" + prettyPrintValue(fTask) + ", ");
    
    result.append("\"Process\"=" + prettyPrintValue(fProcess)
    );
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
  public IncorrectEntryInChecklistQuerySpecification specification() {
    try {
    	return IncorrectEntryInChecklistQuerySpecification.instance();
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
  public static IncorrectEntryInChecklistMatch newEmptyMatch() {
    return new Mutable(null, null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pChecklistEntry the fixed value of pattern parameter ChecklistEntry, or null if not bound.
   * @param pTask the fixed value of pattern parameter Task, or null if not bound.
   * @param pProcess the fixed value of pattern parameter Process, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static IncorrectEntryInChecklistMatch newMutableMatch(final EObject pChecklistEntry, final EObject pTask, final EObject pProcess) {
    return new Mutable(pChecklistEntry, pTask, pProcess);
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
  public static IncorrectEntryInChecklistMatch newMatch(final EObject pChecklistEntry, final EObject pTask, final EObject pProcess) {
    return new Immutable(pChecklistEntry, pTask, pProcess);
  }
  
  private static final class Mutable extends IncorrectEntryInChecklistMatch {
    Mutable(final EObject pChecklistEntry, final EObject pTask, final EObject pProcess) {
      super(pChecklistEntry, pTask, pProcess);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends IncorrectEntryInChecklistMatch {
    Immutable(final EObject pChecklistEntry, final EObject pTask, final EObject pProcess) {
      super(pChecklistEntry, pTask, pProcess);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
