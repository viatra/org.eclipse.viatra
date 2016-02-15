package headless;

import headless.util.EClassNamesKeywordQuerySpecification;
import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Pattern-specific match representation of the headless.eClassNamesKeyword pattern,
 * to be used in conjunction with {@link EClassNamesKeywordMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see EClassNamesKeywordMatcher
 * @see EClassNamesKeywordProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class EClassNamesKeywordMatch extends BasePatternMatch {
  private EClass fC;
  
  private String fN;
  
  private static List<String> parameterNames = makeImmutableList("c", "n");
  
  private EClassNamesKeywordMatch(final EClass pC, final String pN) {
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
    	this.fC = (org.eclipse.emf.ecore.EClass) newValue;
    	return true;
    }
    if ("n".equals(parameterName) ) {
    	this.fN = (java.lang.String) newValue;
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
    return "headless.eClassNamesKeyword";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return EClassNamesKeywordMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fC, fN};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"c\"=" + prettyPrintValue(fC) + ", ");
    result.append("\"n\"=" + prettyPrintValue(fN));
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
    if (!(obj instanceof EClassNamesKeywordMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    EClassNamesKeywordMatch other = (EClassNamesKeywordMatch) obj;
    if (fC == null) {if (other.fC != null) return false;}
    else if (!fC.equals(other.fC)) return false;
    if (fN == null) {if (other.fN != null) return false;}
    else if (!fN.equals(other.fN)) return false;
    return true;
  }
  
  @Override
  public EClassNamesKeywordQuerySpecification specification() {
    try {
    	return EClassNamesKeywordQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends EClassNamesKeywordMatch {
    Mutable(final EClass pC, final String pN) {
      super(pC, pN);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends EClassNamesKeywordMatch {
    Immutable(final EClass pC, final String pN) {
      super(pC, pN);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
