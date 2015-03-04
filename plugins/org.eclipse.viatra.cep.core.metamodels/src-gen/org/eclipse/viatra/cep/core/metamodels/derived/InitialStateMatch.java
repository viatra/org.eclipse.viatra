package org.eclipse.viatra.cep.core.metamodels.derived;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.InitState;
import org.eclipse.viatra.cep.core.metamodels.derived.util.InitialStateQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.cep.core.metamodels.derived.initialState pattern,
 * to be used in conjunction with {@link InitialStateMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see InitialStateMatcher
 * @see InitialStateProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class InitialStateMatch extends BasePatternMatch {
  private Automaton fThis;
  
  private InitState fInitState;
  
  private static List<String> parameterNames = makeImmutableList("this", "initState");
  
  private InitialStateMatch(final Automaton pThis, final InitState pInitState) {
    this.fThis = pThis;
    this.fInitState = pInitState;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("this".equals(parameterName)) return this.fThis;
    if ("initState".equals(parameterName)) return this.fInitState;
    return null;
  }
  
  public Automaton getThis() {
    return this.fThis;
  }
  
  public InitState getInitState() {
    return this.fInitState;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("this".equals(parameterName) ) {
    	this.fThis = (org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) newValue;
    	return true;
    }
    if ("initState".equals(parameterName) ) {
    	this.fInitState = (org.eclipse.viatra.cep.core.metamodels.automaton.InitState) newValue;
    	return true;
    }
    return false;
  }
  
  public void setThis(final Automaton pThis) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fThis = pThis;
  }
  
  public void setInitState(final InitState pInitState) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fInitState = pInitState;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.cep.core.metamodels.derived.initialState";
  }
  
  @Override
  public List<String> parameterNames() {
    return InitialStateMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fThis, fInitState};
  }
  
  @Override
  public InitialStateMatch toImmutable() {
    return isMutable() ? newMatch(fThis, fInitState) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"this\"=" + prettyPrintValue(fThis) + ", ");
    
    result.append("\"initState\"=" + prettyPrintValue(fInitState)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fThis == null) ? 0 : fThis.hashCode());
    result = prime * result + ((fInitState == null) ? 0 : fInitState.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof InitialStateMatch)) { // this should be infrequent
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
    InitialStateMatch other = (InitialStateMatch) obj;
    if (fThis == null) {if (other.fThis != null) return false;}
    else if (!fThis.equals(other.fThis)) return false;
    if (fInitState == null) {if (other.fInitState != null) return false;}
    else if (!fInitState.equals(other.fInitState)) return false;
    return true;
  }
  
  @Override
  public InitialStateQuerySpecification specification() {
    try {
    	return InitialStateQuerySpecification.instance();
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
  public static InitialStateMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pInitState the fixed value of pattern parameter initState, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static InitialStateMatch newMutableMatch(final Automaton pThis, final InitState pInitState) {
    return new Mutable(pThis, pInitState);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pInitState the fixed value of pattern parameter initState, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static InitialStateMatch newMatch(final Automaton pThis, final InitState pInitState) {
    return new Immutable(pThis, pInitState);
  }
  
  private static final class Mutable extends InitialStateMatch {
    Mutable(final Automaton pThis, final InitState pInitState) {
      super(pThis, pInitState);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends InitialStateMatch {
    Immutable(final Automaton pThis, final InitState pInitState) {
      super(pThis, pInitState);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
