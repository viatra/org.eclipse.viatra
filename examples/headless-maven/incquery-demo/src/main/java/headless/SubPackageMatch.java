package headless;

import headless.util.SubPackageQuerySpecification;
import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Pattern-specific match representation of the headless.subPackage pattern,
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
    	this.fP = (org.eclipse.emf.ecore.EPackage) newValue;
    	return true;
    }
    if ("sp".equals(parameterName) ) {
    	this.fSp = (org.eclipse.emf.ecore.EPackage) newValue;
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
    return "headless.subPackage";
    
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
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"p\"=" + prettyPrintValue(fP) + ", ");
    result.append("\"sp\"=" + prettyPrintValue(fSp));
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
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
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
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends SubPackageMatch {
    Mutable(final EPackage pP, final EPackage pSp) {
      super(pP, pSp);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends SubPackageMatch {
    Immutable(final EPackage pP, final EPackage pSp) {
      super(pP, pSp);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
