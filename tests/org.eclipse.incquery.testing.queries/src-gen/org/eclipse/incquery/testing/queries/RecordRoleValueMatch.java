package org.eclipse.incquery.testing.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.incquery.snapshot.EIQSnapshot.RecordRole;
import org.eclipse.incquery.testing.queries.util.RecordRoleValueQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.testing.queries.RecordRoleValue pattern,
 * to be used in conjunction with {@link RecordRoleValueMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see RecordRoleValueMatcher
 * @see RecordRoleValueProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class RecordRoleValueMatch extends BasePatternMatch {
  private MatchRecord fRecord;
  
  private RecordRole fRole;
  
  private static List<String> parameterNames = makeImmutableList("Record", "Role");
  
  private RecordRoleValueMatch(final MatchRecord pRecord, final RecordRole pRole) {
    this.fRecord = pRecord;
    this.fRole = pRole;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Record".equals(parameterName)) return this.fRecord;
    if ("Role".equals(parameterName)) return this.fRole;
    return null;
    
  }
  
  public MatchRecord getRecord() {
    return this.fRecord;
    
  }
  
  public RecordRole getRole() {
    return this.fRole;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Record".equals(parameterName) ) {
    	this.fRecord = (org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    if ("Role".equals(parameterName) ) {
    	this.fRole = (org.eclipse.incquery.snapshot.EIQSnapshot.RecordRole) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setRecord(final MatchRecord pRecord) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fRecord = pRecord;
    
  }
  
  public void setRole(final RecordRole pRole) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fRole = pRole;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.testing.queries.RecordRoleValue";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return RecordRoleValueMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fRecord, fRole};
    
  }
  
  @Override
  public RecordRoleValueMatch toImmutable() {
    return isMutable() ? newMatch(fRecord, fRole) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Record\"=" + prettyPrintValue(fRecord) + ", ");
    result.append("\"Role\"=" + prettyPrintValue(fRole));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fRecord == null) ? 0 : fRecord.hashCode());
    result = prime * result + ((fRole == null) ? 0 : fRole.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof RecordRoleValueMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    RecordRoleValueMatch other = (RecordRoleValueMatch) obj;
    if (fRecord == null) {if (other.fRecord != null) return false;}
    else if (!fRecord.equals(other.fRecord)) return false;
    if (fRole == null) {if (other.fRole != null) return false;}
    else if (!fRole.equals(other.fRole)) return false;
    return true;
  }
  
  @Override
  public RecordRoleValueQuerySpecification specification() {
    try {
    	return RecordRoleValueQuerySpecification.instance();
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
  public static RecordRoleValueMatch newEmptyMatch() {
    return new Mutable(null, null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static RecordRoleValueMatch newMutableMatch(final MatchRecord pRecord, final RecordRole pRole) {
    return new Mutable(pRecord, pRole);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pRecord the fixed value of pattern parameter Record, or null if not bound.
   * @param pRole the fixed value of pattern parameter Role, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static RecordRoleValueMatch newMatch(final MatchRecord pRecord, final RecordRole pRole) {
    return new Immutable(pRecord, pRole);
    
  }
  
  @SuppressWarnings("all")
  private static final class Mutable extends RecordRoleValueMatch {
    Mutable(final MatchRecord pRecord, final RecordRole pRole) {
      super(pRecord, pRole);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  private static final class Immutable extends RecordRoleValueMatch {
    Immutable(final MatchRecord pRecord, final RecordRole pRole) {
      super(pRecord, pRole);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
