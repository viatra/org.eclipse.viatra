package operation.queries;

import java.util.Arrays;
import java.util.List;
import operation.ChecklistEntry;
import operation.queries.util.ChecklistEntryJobCorrespondenceQuerySpecification;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import system.Job;

/**
 * Pattern-specific match representation of the operation.queries.ChecklistEntryJobCorrespondence pattern,
 * to be used in conjunction with {@link ChecklistEntryJobCorrespondenceMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ChecklistEntryJobCorrespondenceMatcher
 * @see ChecklistEntryJobCorrespondenceProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ChecklistEntryJobCorrespondenceMatch extends BasePatternMatch {
  private ChecklistEntry fCLE;
  
  private Job fJob;
  
  private static List<String> parameterNames = makeImmutableList("CLE", "Job");
  
  private ChecklistEntryJobCorrespondenceMatch(final ChecklistEntry pCLE, final Job pJob) {
    this.fCLE = pCLE;
    this.fJob = pJob;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("CLE".equals(parameterName)) return this.fCLE;
    if ("Job".equals(parameterName)) return this.fJob;
    return null;
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
    return "operation.queries.ChecklistEntryJobCorrespondence";
  }
  
  @Override
  public List<String> parameterNames() {
    return ChecklistEntryJobCorrespondenceMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fCLE, fJob};
  }
  
  @Override
  public ChecklistEntryJobCorrespondenceMatch toImmutable() {
    return isMutable() ? newMatch(fCLE, fJob) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"CLE\"=" + prettyPrintValue(fCLE) + ", ");
    
    result.append("\"Job\"=" + prettyPrintValue(fJob)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fCLE == null) ? 0 : fCLE.hashCode());
    result = prime * result + ((fJob == null) ? 0 : fJob.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ChecklistEntryJobCorrespondenceMatch)) { // this should be infrequent
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
    ChecklistEntryJobCorrespondenceMatch other = (ChecklistEntryJobCorrespondenceMatch) obj;
    if (fCLE == null) {if (other.fCLE != null) return false;}
    else if (!fCLE.equals(other.fCLE)) return false;
    if (fJob == null) {if (other.fJob != null) return false;}
    else if (!fJob.equals(other.fJob)) return false;
    return true;
  }
  
  @Override
  public ChecklistEntryJobCorrespondenceQuerySpecification specification() {
    try {
    	return ChecklistEntryJobCorrespondenceQuerySpecification.instance();
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
  public static ChecklistEntryJobCorrespondenceMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pCLE the fixed value of pattern parameter CLE, or null if not bound.
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ChecklistEntryJobCorrespondenceMatch newMutableMatch(final ChecklistEntry pCLE, final Job pJob) {
    return new Mutable(pCLE, pJob);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pCLE the fixed value of pattern parameter CLE, or null if not bound.
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ChecklistEntryJobCorrespondenceMatch newMatch(final ChecklistEntry pCLE, final Job pJob) {
    return new Immutable(pCLE, pJob);
  }
  
  private static final class Mutable extends ChecklistEntryJobCorrespondenceMatch {
    Mutable(final ChecklistEntry pCLE, final Job pJob) {
      super(pCLE, pJob);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ChecklistEntryJobCorrespondenceMatch {
    Immutable(final ChecklistEntry pCLE, final Job pJob) {
      super(pCLE, pJob);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
