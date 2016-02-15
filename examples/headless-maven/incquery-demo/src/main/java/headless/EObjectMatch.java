package headless;

import headless.util.EObjectQuerySpecification;
import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Pattern-specific match representation of the headless.eObject pattern,
 * to be used in conjunction with {@link EObjectMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see EObjectMatcher
 * @see EObjectProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class EObjectMatch extends BasePatternMatch {
  private EObject fO;
  
  private static List<String> parameterNames = makeImmutableList("o");
  
  private EObjectMatch(final EObject pO) {
    this.fO = pO;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("o".equals(parameterName)) return this.fO;
    return null;
    
  }
  
  public EObject getO() {
    return this.fO;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("o".equals(parameterName) ) {
    	this.fO = (org.eclipse.emf.ecore.EObject) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setO(final EObject pO) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fO = pO;
    
  }
  
  @Override
  public String patternName() {
    return "headless.eObject";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return EObjectMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fO};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"o\"=" + prettyPrintValue(fO));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fO == null) ? 0 : fO.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof EObjectMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    EObjectMatch other = (EObjectMatch) obj;
    if (fO == null) {if (other.fO != null) return false;}
    else if (!fO.equals(other.fO)) return false;
    return true;
  }
  
  @Override
  public EObjectQuerySpecification specification() {
    try {
    	return EObjectQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends EObjectMatch {
    Mutable(final EObject pO) {
      super(pO);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends EObjectMatch {
    Immutable(final EObject pO) {
      super(pO);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
