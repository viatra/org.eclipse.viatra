package org.eclipse.viatra.query.application.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.application.queries.util.EObjectQuerySpecification;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.query.application.queries.eObject pattern,
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
    	this.fO = (EObject) newValue;
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
    return "org.eclipse.viatra.query.application.queries.eObject";
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
  public EObjectMatch toImmutable() {
    return isMutable() ? newMatch(fO) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"o\"=" + prettyPrintValue(fO)
    );
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
    EObjectMatch other = (EObjectMatch) obj;
    if (fO == null) {if (other.fO != null) return false;}
    else if (!fO.equals(other.fO)) return false;
    return true;
  }
  
  @Override
  public EObjectQuerySpecification specification() {
    try {
    	return EObjectQuerySpecification.instance();
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
  public static EObjectMatch newEmptyMatch() {
    return new Mutable(null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pO the fixed value of pattern parameter o, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static EObjectMatch newMutableMatch(final EObject pO) {
    return new Mutable(pO);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pO the fixed value of pattern parameter o, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static EObjectMatch newMatch(final EObject pO) {
    return new Immutable(pO);
  }
  
  private static final class Mutable extends EObjectMatch {
    Mutable(final EObject pO) {
      super(pO);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends EObjectMatch {
    Immutable(final EObject pO) {
      super(pO);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
