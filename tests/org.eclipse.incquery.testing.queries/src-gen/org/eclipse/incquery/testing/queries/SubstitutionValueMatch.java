package org.eclipse.incquery.testing.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord;
import org.eclipse.incquery.testing.queries.util.SubstitutionValueQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.testing.queries.SubstitutionValue pattern,
 * to be used in conjunction with {@link SubstitutionValueMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see SubstitutionValueMatcher
 * @see SubstitutionValueProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class SubstitutionValueMatch extends BasePatternMatch {
  private MatchSubstitutionRecord fSubstitution;
  
  private Object fValue;
  
  private static List<String> parameterNames = makeImmutableList("Substitution", "Value");
  
  private SubstitutionValueMatch(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
    this.fSubstitution = pSubstitution;
    this.fValue = pValue;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Substitution".equals(parameterName)) return this.fSubstitution;
    if ("Value".equals(parameterName)) return this.fValue;
    return null;
    
  }
  
  public MatchSubstitutionRecord getSubstitution() {
    return this.fSubstitution;
    
  }
  
  public Object getValue() {
    return this.fValue;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Substitution".equals(parameterName) ) {
    	this.fSubstitution = (org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord) newValue;
    	return true;
    }
    if ("Value".equals(parameterName) && newValue instanceof java.lang.Object) {
    	this.fValue = (java.lang.Object) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setSubstitution(final MatchSubstitutionRecord pSubstitution) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fSubstitution = pSubstitution;
    
  }
  
  public void setValue(final Object pValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fValue = pValue;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.testing.queries.SubstitutionValue";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return SubstitutionValueMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fSubstitution, fValue};
    
  }
  
  @Override
  public SubstitutionValueMatch toImmutable() {
    return isMutable() ? newMatch(fSubstitution, fValue) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Substitution\"=" + prettyPrintValue(fSubstitution) + ", ");
    result.append("\"Value\"=" + prettyPrintValue(fValue));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fSubstitution == null) ? 0 : fSubstitution.hashCode());
    result = prime * result + ((fValue == null) ? 0 : fValue.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof SubstitutionValueMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    SubstitutionValueMatch other = (SubstitutionValueMatch) obj;
    if (fSubstitution == null) {if (other.fSubstitution != null) return false;}
    else if (!fSubstitution.equals(other.fSubstitution)) return false;
    if (fValue == null) {if (other.fValue != null) return false;}
    else if (!fValue.equals(other.fValue)) return false;
    return true;
  }
  
  @Override
  public SubstitutionValueQuerySpecification specification() {
    try {
    	return SubstitutionValueQuerySpecification.instance();
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
  public static SubstitutionValueMatch newEmptyMatch() {
    return new Mutable(null, null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static SubstitutionValueMatch newMutableMatch(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
    return new Mutable(pSubstitution, pValue);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pSubstitution the fixed value of pattern parameter Substitution, or null if not bound.
   * @param pValue the fixed value of pattern parameter Value, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static SubstitutionValueMatch newMatch(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
    return new Immutable(pSubstitution, pValue);
    
  }
  
  @SuppressWarnings("all")
  private static final class Mutable extends SubstitutionValueMatch {
    Mutable(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
      super(pSubstitution, pValue);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  private static final class Immutable extends SubstitutionValueMatch {
    Immutable(final MatchSubstitutionRecord pSubstitution, final Object pValue) {
      super(pSubstitution, pValue);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
