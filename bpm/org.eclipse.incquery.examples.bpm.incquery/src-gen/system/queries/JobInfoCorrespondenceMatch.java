package system.queries;

import java.util.Arrays;
import java.util.List;
import operation.RuntimeInformation;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import system.Job;
import system.queries.util.JobInfoCorrespondenceQuerySpecification;

/**
 * Pattern-specific match representation of the system.queries.JobInfoCorrespondence pattern,
 * to be used in conjunction with {@link JobInfoCorrespondenceMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see JobInfoCorrespondenceMatcher
 * @see JobInfoCorrespondenceProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class JobInfoCorrespondenceMatch extends BasePatternMatch {
  private Job fJob;
  
  private RuntimeInformation fInfo;
  
  private static List<String> parameterNames = makeImmutableList("Job", "Info");
  
  private JobInfoCorrespondenceMatch(final Job pJob, final RuntimeInformation pInfo) {
    this.fJob = pJob;
    this.fInfo = pInfo;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Job".equals(parameterName)) return this.fJob;
    if ("Info".equals(parameterName)) return this.fInfo;
    return null;
    
  }
  
  public Job getJob() {
    return this.fJob;
    
  }
  
  public RuntimeInformation getInfo() {
    return this.fInfo;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Job".equals(parameterName) ) {
    	this.fJob = (system.Job) newValue;
    	return true;
    }
    if ("Info".equals(parameterName) ) {
    	this.fInfo = (operation.RuntimeInformation) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setJob(final Job pJob) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fJob = pJob;
    
  }
  
  public void setInfo(final RuntimeInformation pInfo) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fInfo = pInfo;
    
  }
  
  @Override
  public String patternName() {
    return "system.queries.JobInfoCorrespondence";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return JobInfoCorrespondenceMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fJob, fInfo};
    
  }
  
  @Override
  public JobInfoCorrespondenceMatch toImmutable() {
    return isMutable() ? newMatch(fJob, fInfo) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Job\"=" + prettyPrintValue(fJob) + ", ");
    result.append("\"Info\"=" + prettyPrintValue(fInfo));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fJob == null) ? 0 : fJob.hashCode());
    result = prime * result + ((fInfo == null) ? 0 : fInfo.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof JobInfoCorrespondenceMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    JobInfoCorrespondenceMatch other = (JobInfoCorrespondenceMatch) obj;
    if (fJob == null) {if (other.fJob != null) return false;}
    else if (!fJob.equals(other.fJob)) return false;
    if (fInfo == null) {if (other.fInfo != null) return false;}
    else if (!fInfo.equals(other.fInfo)) return false;
    return true;
  }
  
  @Override
  public JobInfoCorrespondenceQuerySpecification specification() {
    try {
    	return JobInfoCorrespondenceQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  /**
   * Returns an empty, mutable match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @return the empty match.
   * 
   */
  public static JobInfoCorrespondenceMatch newEmptyMatch() {
    return new Mutable(null, null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pJob the fixed value of pattern parameter Job, or null if not bound.
   * @param pInfo the fixed value of pattern parameter Info, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static JobInfoCorrespondenceMatch newMutableMatch(final Job pJob, final RuntimeInformation pInfo) {
    return new Mutable(pJob, pInfo);
    
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
  public static JobInfoCorrespondenceMatch newMatch(final Job pJob, final RuntimeInformation pInfo) {
    return new Immutable(pJob, pInfo);
    
  }
  
  @SuppressWarnings("all")
  private static final class Mutable extends JobInfoCorrespondenceMatch {
    Mutable(final Job pJob, final RuntimeInformation pInfo) {
      super(pJob, pInfo);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  private static final class Immutable extends JobInfoCorrespondenceMatch {
    Immutable(final Job pJob, final RuntimeInformation pInfo) {
      super(pJob, pInfo);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
