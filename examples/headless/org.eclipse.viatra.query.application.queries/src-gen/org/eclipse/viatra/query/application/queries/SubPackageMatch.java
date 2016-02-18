package org.eclipse.viatra.query.application.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.application.queries.util.SubPackageQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.query.application.queries.subPackage pattern,
 * to be used in conjunction with {@link SubPackageMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see SubPackageMatcher
 * @see SubPackageProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class SubPackageMatch extends BasePatternMatch {
  private EPackage fP;
  
  private EPackage fSp;
  
  private static List<String> parameterNames = makeImmutableList("p", "sp");
  
  private SubPackageMatch(final EPackage pP, final EPackage pSp) {
    this.fP = pP;
    this.fSp = pSp;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("p".equals(parameterName)) return this.fP;
    if ("sp".equals(parameterName)) return this.fSp;
    return null;
  }
  
  public EPackage getP() {
    return this.fP;
  }
  
  public EPackage getSp() {
    return this.fSp;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("p".equals(parameterName) ) {
    	this.fP = (EPackage) newValue;
    	return true;
    }
    if ("sp".equals(parameterName) ) {
    	this.fSp = (EPackage) newValue;
    	return true;
    }
    return false;
  }
  
  public void setP(final EPackage pP) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fP = pP;
  }
  
  public void setSp(final EPackage pSp) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fSp = pSp;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.query.application.queries.subPackage";
  }
  
  @Override
  public List<String> parameterNames() {
    return SubPackageMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fP, fSp};
  }
  
  @Override
  public SubPackageMatch toImmutable() {
    return isMutable() ? newMatch(fP, fSp) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"p\"=" + prettyPrintValue(fP) + ", ");
    
    result.append("\"sp\"=" + prettyPrintValue(fSp)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fP == null) ? 0 : fP.hashCode());
    result = prime * result + ((fSp == null) ? 0 : fSp.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof SubPackageMatch)) { // this should be infrequent
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
    SubPackageMatch other = (SubPackageMatch) obj;
    if (fP == null) {if (other.fP != null) return false;}
    else if (!fP.equals(other.fP)) return false;
    if (fSp == null) {if (other.fSp != null) return false;}
    else if (!fSp.equals(other.fSp)) return false;
    return true;
  }
  
  @Override
  public SubPackageQuerySpecification specification() {
    try {
    	return SubPackageQuerySpecification.instance();
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
  public static SubPackageMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pSp the fixed value of pattern parameter sp, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static SubPackageMatch newMutableMatch(final EPackage pP, final EPackage pSp) {
    return new Mutable(pP, pSp);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pSp the fixed value of pattern parameter sp, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static SubPackageMatch newMatch(final EPackage pP, final EPackage pSp) {
    return new Immutable(pP, pSp);
  }
  
  private static final class Mutable extends SubPackageMatch {
    Mutable(final EPackage pP, final EPackage pSp) {
      super(pP, pSp);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends SubPackageMatch {
    Immutable(final EPackage pP, final EPackage pSp) {
      super(pP, pSp);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
