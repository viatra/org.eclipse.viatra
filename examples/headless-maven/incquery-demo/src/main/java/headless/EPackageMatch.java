package headless;

import headless.util.EPackageQuerySpecification;
import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Pattern-specific match representation of the headless.ePackage pattern,
 * to be used in conjunction with {@link EPackageMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see EPackageMatcher
 * @see EPackageProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class EPackageMatch extends BasePatternMatch {
  private EPackage fP;
  
  private static List<String> parameterNames = makeImmutableList("p");
  
  private EPackageMatch(final EPackage pP) {
    this.fP = pP;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("p".equals(parameterName)) return this.fP;
    return null;
    
  }
  
  public EPackage getP() {
    return this.fP;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("p".equals(parameterName) ) {
    	this.fP = (org.eclipse.emf.ecore.EPackage) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setP(final EPackage pP) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fP = pP;
    
  }
  
  @Override
  public String patternName() {
    return "headless.ePackage";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return EPackageMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fP};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"p\"=" + prettyPrintValue(fP));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fP == null) ? 0 : fP.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof EPackageMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    EPackageMatch other = (EPackageMatch) obj;
    if (fP == null) {if (other.fP != null) return false;}
    else if (!fP.equals(other.fP)) return false;
    return true;
  }
  
  @Override
  public EPackageQuerySpecification specification() {
    try {
    	return EPackageQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends EPackageMatch {
    Mutable(final EPackage pP) {
      super(pP);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends EPackageMatch {
    Immutable(final EPackage pP) {
      super(pP);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
