package org.eclipse.viatra.query.application.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.viatra.query.application.queries.util.EClassNamesQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.query.application.queries.eClassNames pattern,
 * to be used in conjunction with {@link EClassNamesMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see EClassNamesMatcher
 * @see EClassNamesProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class EClassNamesMatch extends BasePatternMatch {
  private EClass fC;
  
  private String fN;
  
  private static List<String> parameterNames = makeImmutableList("c", "n");
  
  private EClassNamesMatch(final EClass pC, final String pN) {
    this.fC = pC;
    this.fN = pN;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("c".equals(parameterName)) return this.fC;
    if ("n".equals(parameterName)) return this.fN;
    return null;
  }
  
  public EClass getC() {
    return this.fC;
  }
  
  public String getN() {
    return this.fN;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("c".equals(parameterName) ) {
    	this.fC = (EClass) newValue;
    	return true;
    }
    if ("n".equals(parameterName) ) {
    	this.fN = (String) newValue;
    	return true;
    }
    return false;
  }
  
  public void setC(final EClass pC) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fC = pC;
  }
  
  public void setN(final String pN) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fN = pN;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.query.application.queries.eClassNames";
  }
  
  @Override
  public List<String> parameterNames() {
    return EClassNamesMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fC, fN};
  }
  
  @Override
  public EClassNamesMatch toImmutable() {
    return isMutable() ? newMatch(fC, fN) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"c\"=" + prettyPrintValue(fC) + ", ");
    
    result.append("\"n\"=" + prettyPrintValue(fN)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fC == null) ? 0 : fC.hashCode());
    result = prime * result + ((fN == null) ? 0 : fN.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof EClassNamesMatch)) { // this should be infrequent
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
    EClassNamesMatch other = (EClassNamesMatch) obj;
    if (fC == null) {if (other.fC != null) return false;}
    else if (!fC.equals(other.fC)) return false;
    if (fN == null) {if (other.fN != null) return false;}
    else if (!fN.equals(other.fN)) return false;
    return true;
  }
  
  @Override
  public EClassNamesQuerySpecification specification() {
    try {
    	return EClassNamesQuerySpecification.instance();
    } catch (ViatraQueryException ex) {
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
  public static EClassNamesMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static EClassNamesMatch newMutableMatch(final EClass pC, final String pN) {
    return new Mutable(pC, pN);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pC the fixed value of pattern parameter c, or null if not bound.
   * @param pN the fixed value of pattern parameter n, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static EClassNamesMatch newMatch(final EClass pC, final String pN) {
    return new Immutable(pC, pN);
  }
  
  private static final class Mutable extends EClassNamesMatch {
    Mutable(final EClass pC, final String pN) {
      super(pC, pN);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends EClassNamesMatch {
    Immutable(final EClass pC, final String pN) {
      super(pC, pN);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
