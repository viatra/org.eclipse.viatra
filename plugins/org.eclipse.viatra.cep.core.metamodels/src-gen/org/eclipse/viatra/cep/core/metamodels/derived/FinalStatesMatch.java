package org.eclipse.viatra.cep.core.metamodels.derived;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.cep.core.metamodels.automaton.Automaton;
import org.eclipse.viatra.cep.core.metamodels.automaton.FinalState;
import org.eclipse.viatra.cep.core.metamodels.derived.util.FinalStatesQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.cep.core.metamodels.derived.finalStates pattern,
 * to be used in conjunction with {@link FinalStatesMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see FinalStatesMatcher
 * @see FinalStatesProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class FinalStatesMatch extends BasePatternMatch {
  private Automaton fThis;
  
  private FinalState fFinalState;
  
  private static List<String> parameterNames = makeImmutableList("this", "finalState");
  
  private FinalStatesMatch(final Automaton pThis, final FinalState pFinalState) {
    this.fThis = pThis;
    this.fFinalState = pFinalState;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("this".equals(parameterName)) return this.fThis;
    if ("finalState".equals(parameterName)) return this.fFinalState;
    return null;
  }
  
  public Automaton getThis() {
    return this.fThis;
  }
  
  public FinalState getFinalState() {
    return this.fFinalState;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("this".equals(parameterName) ) {
    	this.fThis = (org.eclipse.viatra.cep.core.metamodels.automaton.Automaton) newValue;
    	return true;
    }
    if ("finalState".equals(parameterName) ) {
    	this.fFinalState = (org.eclipse.viatra.cep.core.metamodels.automaton.FinalState) newValue;
    	return true;
    }
    return false;
  }
  
  public void setThis(final Automaton pThis) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fThis = pThis;
  }
  
  public void setFinalState(final FinalState pFinalState) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fFinalState = pFinalState;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.cep.core.metamodels.derived.finalStates";
  }
  
  @Override
  public List<String> parameterNames() {
    return FinalStatesMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fThis, fFinalState};
  }
  
  @Override
  public FinalStatesMatch toImmutable() {
    return isMutable() ? newMatch(fThis, fFinalState) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"this\"=" + prettyPrintValue(fThis) + ", ");
    
    result.append("\"finalState\"=" + prettyPrintValue(fFinalState)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fThis == null) ? 0 : fThis.hashCode());
    result = prime * result + ((fFinalState == null) ? 0 : fFinalState.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof FinalStatesMatch)) { // this should be infrequent
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
    FinalStatesMatch other = (FinalStatesMatch) obj;
    if (fThis == null) {if (other.fThis != null) return false;}
    else if (!fThis.equals(other.fThis)) return false;
    if (fFinalState == null) {if (other.fFinalState != null) return false;}
    else if (!fFinalState.equals(other.fFinalState)) return false;
    return true;
  }
  
  @Override
  public FinalStatesQuerySpecification specification() {
    try {
    	return FinalStatesQuerySpecification.instance();
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
  public static FinalStatesMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pFinalState the fixed value of pattern parameter finalState, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static FinalStatesMatch newMutableMatch(final Automaton pThis, final FinalState pFinalState) {
    return new Mutable(pThis, pFinalState);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pThis the fixed value of pattern parameter this, or null if not bound.
   * @param pFinalState the fixed value of pattern parameter finalState, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static FinalStatesMatch newMatch(final Automaton pThis, final FinalState pFinalState) {
    return new Immutable(pThis, pFinalState);
  }
  
  private static final class Mutable extends FinalStatesMatch {
    Mutable(final Automaton pThis, final FinalState pFinalState) {
      super(pThis, pFinalState);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends FinalStatesMatch {
    Immutable(final Automaton pThis, final FinalState pFinalState) {
      super(pThis, pFinalState);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
