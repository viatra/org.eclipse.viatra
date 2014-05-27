package org.eclipse.incquery.examples.bpm.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.examples.bpm.queries.util.NextActivityQuerySpecification;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import process.Activity;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.examples.bpm.queries.nextActivity pattern,
 * to be used in conjunction with {@link NextActivityMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see NextActivityMatcher
 * @see NextActivityProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class NextActivityMatch extends BasePatternMatch {
  private Activity fAct;
  
  private Activity fNext;
  
  private static List<String> parameterNames = makeImmutableList("Act", "Next");
  
  private NextActivityMatch(final Activity pAct, final Activity pNext) {
    this.fAct = pAct;
    this.fNext = pNext;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Act".equals(parameterName)) return this.fAct;
    if ("Next".equals(parameterName)) return this.fNext;
    return null;
    
  }
  
  public Activity getAct() {
    return this.fAct;
    
  }
  
  public Activity getNext() {
    return this.fNext;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Act".equals(parameterName) ) {
    	this.fAct = (process.Activity) newValue;
    	return true;
    }
    if ("Next".equals(parameterName) ) {
    	this.fNext = (process.Activity) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setAct(final Activity pAct) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fAct = pAct;
    
  }
  
  public void setNext(final Activity pNext) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fNext = pNext;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.examples.bpm.queries.nextActivity";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return NextActivityMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fAct, fNext};
    
  }
  
  @Override
  public NextActivityMatch toImmutable() {
    return isMutable() ? newMatch(fAct, fNext) : this;
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Act\"=" + prettyPrintValue(fAct) + ", ");
    result.append("\"Next\"=" + prettyPrintValue(fNext));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fAct == null) ? 0 : fAct.hashCode());
    result = prime * result + ((fNext == null) ? 0 : fNext.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof NextActivityMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    NextActivityMatch other = (NextActivityMatch) obj;
    if (fAct == null) {if (other.fAct != null) return false;}
    else if (!fAct.equals(other.fAct)) return false;
    if (fNext == null) {if (other.fNext != null) return false;}
    else if (!fNext.equals(other.fNext)) return false;
    return true;
  }
  
  @Override
  public NextActivityQuerySpecification specification() {
    try {
    	return NextActivityQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  /**
   * Returns an empty, mutable match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @return the empty match.
   * 
   */
  public static NextActivityMatch newEmptyMatch() {
    return new Mutable(null, null);
    
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pAct the fixed value of pattern parameter Act, or null if not bound.
   * @param pNext the fixed value of pattern parameter Next, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static NextActivityMatch newMutableMatch(final Activity pAct, final Activity pNext) {
    return new Mutable(pAct, pNext);
    
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pAct the fixed value of pattern parameter Act, or null if not bound.
   * @param pNext the fixed value of pattern parameter Next, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static NextActivityMatch newMatch(final Activity pAct, final Activity pNext) {
    return new Immutable(pAct, pNext);
    
  }
  
  @SuppressWarnings("all")
  private static final class Mutable extends NextActivityMatch {
    Mutable(final Activity pAct, final Activity pNext) {
      super(pAct, pNext);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  private static final class Immutable extends NextActivityMatch {
    Immutable(final Activity pAct, final Activity pNext) {
      super(pAct, pNext);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
