package org.eclipse.viatra.query.application.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.application.queries.util.ClassesInPackageHierarchyQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.query.application.queries.classesInPackageHierarchy pattern,
 * to be used in conjunction with {@link ClassesInPackageHierarchyMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ClassesInPackageHierarchyMatcher
 * @see ClassesInPackageHierarchyProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class ClassesInPackageHierarchyMatch extends BasePatternMatch {
  private EPackage fRootP;
  
  private EClass fContainedClass;
  
  private static List<String> parameterNames = makeImmutableList("rootP", "containedClass");
  
  private ClassesInPackageHierarchyMatch(final EPackage pRootP, final EClass pContainedClass) {
    this.fRootP = pRootP;
    this.fContainedClass = pContainedClass;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("rootP".equals(parameterName)) return this.fRootP;
    if ("containedClass".equals(parameterName)) return this.fContainedClass;
    return null;
  }
  
  public EPackage getRootP() {
    return this.fRootP;
  }
  
  public EClass getContainedClass() {
    return this.fContainedClass;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("rootP".equals(parameterName) ) {
    	this.fRootP = (EPackage) newValue;
    	return true;
    }
    if ("containedClass".equals(parameterName) ) {
    	this.fContainedClass = (EClass) newValue;
    	return true;
    }
    return false;
  }
  
  public void setRootP(final EPackage pRootP) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fRootP = pRootP;
  }
  
  public void setContainedClass(final EClass pContainedClass) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fContainedClass = pContainedClass;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.query.application.queries.classesInPackageHierarchy";
  }
  
  @Override
  public List<String> parameterNames() {
    return ClassesInPackageHierarchyMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fRootP, fContainedClass};
  }
  
  @Override
  public ClassesInPackageHierarchyMatch toImmutable() {
    return isMutable() ? newMatch(fRootP, fContainedClass) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"rootP\"=" + prettyPrintValue(fRootP) + ", ");
    
    result.append("\"containedClass\"=" + prettyPrintValue(fContainedClass)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fRootP == null) ? 0 : fRootP.hashCode());
    result = prime * result + ((fContainedClass == null) ? 0 : fContainedClass.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ClassesInPackageHierarchyMatch)) { // this should be infrequent
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
    ClassesInPackageHierarchyMatch other = (ClassesInPackageHierarchyMatch) obj;
    if (fRootP == null) {if (other.fRootP != null) return false;}
    else if (!fRootP.equals(other.fRootP)) return false;
    if (fContainedClass == null) {if (other.fContainedClass != null) return false;}
    else if (!fContainedClass.equals(other.fContainedClass)) return false;
    return true;
  }
  
  @Override
  public ClassesInPackageHierarchyQuerySpecification specification() {
    try {
    	return ClassesInPackageHierarchyQuerySpecification.instance();
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
  public static ClassesInPackageHierarchyMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pRootP the fixed value of pattern parameter rootP, or null if not bound.
   * @param pContainedClass the fixed value of pattern parameter containedClass, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static ClassesInPackageHierarchyMatch newMutableMatch(final EPackage pRootP, final EClass pContainedClass) {
    return new Mutable(pRootP, pContainedClass);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pRootP the fixed value of pattern parameter rootP, or null if not bound.
   * @param pContainedClass the fixed value of pattern parameter containedClass, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static ClassesInPackageHierarchyMatch newMatch(final EPackage pRootP, final EClass pContainedClass) {
    return new Immutable(pRootP, pContainedClass);
  }
  
  private static final class Mutable extends ClassesInPackageHierarchyMatch {
    Mutable(final EPackage pRootP, final EClass pContainedClass) {
      super(pRootP, pContainedClass);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends ClassesInPackageHierarchyMatch {
    Immutable(final EPackage pRootP, final EClass pContainedClass) {
      super(pRootP, pContainedClass);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
