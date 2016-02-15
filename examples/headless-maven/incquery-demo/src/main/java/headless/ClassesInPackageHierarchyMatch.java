package headless;

import headless.util.ClassesInPackageHierarchyQuerySpecification;
import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Pattern-specific match representation of the headless.classesInPackageHierarchy pattern,
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
    	this.fRootP = (org.eclipse.emf.ecore.EPackage) newValue;
    	return true;
    }
    if ("containedClass".equals(parameterName) ) {
    	this.fContainedClass = (org.eclipse.emf.ecore.EClass) newValue;
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
    return "headless.classesInPackageHierarchy";
    
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
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"rootP\"=" + prettyPrintValue(fRootP) + ", ");
    result.append("\"containedClass\"=" + prettyPrintValue(fContainedClass));
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
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
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
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends ClassesInPackageHierarchyMatch {
    Mutable(final EPackage pRootP, final EClass pContainedClass) {
      super(pRootP, pContainedClass);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends ClassesInPackageHierarchyMatch {
    Immutable(final EPackage pRootP, final EClass pContainedClass) {
      super(pRootP, pContainedClass);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
