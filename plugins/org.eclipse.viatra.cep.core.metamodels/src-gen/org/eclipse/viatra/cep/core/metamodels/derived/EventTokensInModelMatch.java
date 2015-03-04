package org.eclipse.viatra.cep.core.metamodels.derived;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.cep.core.metamodels.automaton.EventToken;
import org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel;
import org.eclipse.viatra.cep.core.metamodels.derived.util.EventTokensInModelQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.cep.core.metamodels.derived.eventTokensInModel pattern,
 * to be used in conjunction with {@link EventTokensInModelMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see EventTokensInModelMatcher
 * @see EventTokensInModelProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class EventTokensInModelMatch extends BasePatternMatch {
  private InternalModel fThis;
  
  private EventToken fEventToken;
  
  private static List<String> parameterNames = makeImmutableList("this", "eventToken");
  
  private EventTokensInModelMatch(final InternalModel pThis, final EventToken pEventToken) {
    this.fThis = pThis;
    this.fEventToken = pEventToken;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("this".equals(parameterName)) return this.fThis;
    if ("eventToken".equals(parameterName)) return this.fEventToken;
    return null;
  }
  
  public InternalModel getThis() {
    return this.fThis;
  }
  
  public EventToken getEventToken() {
    return this.fEventToken;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("this".equals(parameterName) ) {
    	this.fThis = (org.eclipse.viatra.cep.core.metamodels.automaton.InternalModel) newValue;
    	return true;
    }
    if ("eventToken".equals(parameterName) ) {
    	this.fEventToken = (org.eclipse.viatra.cep.core.metamodels.automaton.EventToken) newValue;
    	return true;
    }
    return false;
  }
  
  public void setThis(final InternalModel pThis) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fThis = pThis;
  }
  
  public void setEventToken(final EventToken pEventToken) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fEventToken = pEventToken;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.cep.core.metamodels.derived.eventTokensInModel";
  }
  
  @Override
  public List<String> parameterNames() {
    return EventTokensInModelMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fThis, fEventToken};
  }
  
  @Override
  public EventTokensInModelMatch toImmutable() {
    return isMutable() ? newMatch(fThis, fEventToken) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"this\"=" + prettyPrintValue(fThis) + ", ");
    
    result.append("\"eventToken\"=" + prettyPrintValue(fEventToken)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fThis == null) ? 0 : fThis.hashCode());
    result = prime * result + ((fEventToken == null) ? 0 : fEventToken.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof EventTokensInModelMatch)) { // this should be infrequent
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
    EventTokensInModelMatch other = (EventTokensInModelMatch) obj;
    if (fThis == null) {if (other.fThis != null) return false;}
    else if (!fThis.equals(other.fThis)) return false;
    if (fEventToken == null) {if (other.fEventToken != null) return false;}
    else if (!fEventToken.equals(other.fEventToken)) return false;
    return true;
  }
  
  @Override
  public EventTokensInModelQuerySpecification specification() {
    try {
    	return EventTokensInModelQuerySpecification.instance();
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
  public static EventTokensInModelMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pEventToken the fixed value of pattern parameter eventToken, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static EventTokensInModelMatch newMutableMatch(final InternalModel pThis, final EventToken pEventToken) {
    return new Mutable(pThis, pEventToken);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pEventToken the fixed value of pattern parameter eventToken, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static EventTokensInModelMatch newMatch(final InternalModel pThis, final EventToken pEventToken) {
    return new Immutable(pThis, pEventToken);
  }
  
  private static final class Mutable extends EventTokensInModelMatch {
    Mutable(final InternalModel pThis, final EventToken pEventToken) {
      super(pThis, pEventToken);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends EventTokensInModelMatch {
    Immutable(final InternalModel pThis, final EventToken pEventToken) {
      super(pThis, pEventToken);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
