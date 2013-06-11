package org.eclipse.incquery.examples.bpm.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
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
    	if (!pattern().equals(otherSig.pattern()))
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
  public Pattern pattern() {
    try {
    	return NextActivityMatcher.querySpecification().getPattern();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  static final class Mutable extends NextActivityMatch {
    Mutable(final Activity pAct, final Activity pNext) {
      super(pAct, pNext);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  static final class Immutable extends NextActivityMatch {
    Immutable(final Activity pAct, final Activity pNext) {
      super(pAct, pNext);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
