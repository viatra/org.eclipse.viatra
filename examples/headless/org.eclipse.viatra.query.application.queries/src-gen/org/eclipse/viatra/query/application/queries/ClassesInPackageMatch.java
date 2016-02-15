package org.eclipse.viatra.query.application.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.application.queries.util.ClassesInPackageQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.query.application.queries.classesInPackage pattern,
 * to be used in conjunction with {@link ClassesInPackageMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ClassesInPackageMatcher
 * @see ClassesInPackageProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ClassesInPackageMatch extends BasePatternMatch {
  private EPackage fP;
  
  private EClass fEc;
  
  private static List<String> parameterNames = makeImmutableList("p", "ec");
  
  private ClassesInPackageMatch(final EPackage pP, final EClass pEc) {
    this.fP = pP;
    this.fEc = pEc;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("p".equals(parameterName)) return this.fP;
    if ("ec".equals(parameterName)) return this.fEc;
    return null;
  }
  
  public EPackage getP() {
    return this.fP;
  }
  
  public EClass getEc() {
    return this.fEc;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("p".equals(parameterName) ) {
    	this.fP = (EPackage) newValue;
    	return true;
    }
    if ("ec".equals(parameterName) ) {
    	this.fEc = (EClass) newValue;
    	return true;
    }
    return false;
  }
  
  public void setP(final EPackage pP) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fP = pP;
  }
  
  public void setEc(final EClass pEc) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fEc = pEc;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.query.application.queries.classesInPackage";
  }
  
  @Override
  public List<String> parameterNames() {
    return ClassesInPackageMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fP, fEc};
  }
  
  @Override
  public ClassesInPackageMatch toImmutable() {
    return isMutable() ? newMatch(fP, fEc) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"p\"=" + prettyPrintValue(fP) + ", ");
    
    result.append("\"ec\"=" + prettyPrintValue(fEc)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fP == null) ? 0 : fP.hashCode());
    result = prime * result + ((fEc == null) ? 0 : fEc.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ClassesInPackageMatch)) { // this should be infrequent
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
    ClassesInPackageMatch other = (ClassesInPackageMatch) obj;
    if (fP == null) {if (other.fP != null) return false;}
    else if (!fP.equals(other.fP)) return false;
    if (fEc == null) {if (other.fEc != null) return false;}
    else if (!fEc.equals(other.fEc)) return false;
    return true;
  }
  
  @Override
  public ClassesInPackageQuerySpecification specification() {
    try {
    	return ClassesInPackageQuerySpecification.instance();
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
  public static ClassesInPackageMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ClassesInPackageMatch newMutableMatch(final EPackage pP, final EClass pEc) {
    return new Mutable(pP, pEc);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pP the fixed value of pattern parameter p, or null if not bound.
   * @param pEc the fixed value of pattern parameter ec, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ClassesInPackageMatch newMatch(final EPackage pP, final EClass pEc) {
    return new Immutable(pP, pEc);
  }
  
  private static final class Mutable extends ClassesInPackageMatch {
    Mutable(final EPackage pP, final EClass pEc) {
      super(pP, pEc);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ClassesInPackageMatch {
    Immutable(final EPackage pP, final EClass pEc) {
      super(pP, pEc);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
