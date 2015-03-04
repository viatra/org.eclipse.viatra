package org.eclipse.viatra.cep.core.metamodels.derived;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.TrapState;
import org.eclipse.viatra.cep.core.metamodels.derived.util.TrapStateQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.cep.core.metamodels.derived.trapState pattern,
 * to be used in conjunction with {@link TrapStateMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see TrapStateMatcher
 * @see TrapStateProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class TrapStateMatch extends BasePatternMatch {
  private Automaton fThis;
  
  private TrapState fTrapState;
  
  private static List<String> parameterNames = makeImmutableList("this", "trapState");
  
  private TrapStateMatch(final Automaton pThis, final TrapState pTrapState) {
    this.fThis = pThis;
    this.fTrapState = pTrapState;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("this".equals(parameterName)) return this.fThis;
    if ("trapState".equals(parameterName)) return this.fTrapState;
    return null;
  }
  
  public Automaton getThis() {
    return this.fThis;
  }
  
  public TrapState getTrapState() {
    return this.fTrapState;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("this".equals(parameterName) ) {
    	this.fThis = (org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) newValue;
    	return true;
    }
    if ("trapState".equals(parameterName) ) {
    	this.fTrapState = (org.eclipse.viatra.cep.core.metamodels.automaton.TrapState) newValue;
    	return true;
    }
    return false;
  }
  
  public void setThis(final Automaton pThis) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fThis = pThis;
  }
  
  public void setTrapState(final TrapState pTrapState) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fTrapState = pTrapState;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.cep.core.metamodels.derived.trapState";
  }
  
  @Override
  public List<String> parameterNames() {
    return TrapStateMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fThis, fTrapState};
  }
  
  @Override
  public TrapStateMatch toImmutable() {
    return isMutable() ? newMatch(fThis, fTrapState) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"this\"=" + prettyPrintValue(fThis) + ", ");
    
    result.append("\"trapState\"=" + prettyPrintValue(fTrapState)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fThis == null) ? 0 : fThis.hashCode());
    result = prime * result + ((fTrapState == null) ? 0 : fTrapState.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof TrapStateMatch)) { // this should be infrequent
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
    TrapStateMatch other = (TrapStateMatch) obj;
    if (fThis == null) {if (other.fThis != null) return false;}
    else if (!fThis.equals(other.fThis)) return false;
    if (fTrapState == null) {if (other.fTrapState != null) return false;}
    else if (!fTrapState.equals(other.fTrapState)) return false;
    return true;
  }
  
  @Override
  public TrapStateQuerySpecification specification() {
    try {
    	return TrapStateQuerySpecification.instance();
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
  public static TrapStateMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pTrapState the fixed value of pattern parameter trapState, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static TrapStateMatch newMutableMatch(final Automaton pThis, final TrapState pTrapState) {
    return new Mutable(pThis, pTrapState);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pTrapState the fixed value of pattern parameter trapState, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static TrapStateMatch newMatch(final Automaton pThis, final TrapState pTrapState) {
    return new Immutable(pThis, pTrapState);
  }
  
  private static final class Mutable extends TrapStateMatch {
    Mutable(final Automaton pThis, final TrapState pTrapState) {
      super(pThis, pTrapState);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends TrapStateMatch {
    Immutable(final Automaton pThis, final TrapState pTrapState) {
      super(pThis, pTrapState);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
