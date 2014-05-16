package org.eclipse.incquery.runtime.runonce.tests;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.examples.eiqlibrary.Library;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.runonce.tests.util.RequestCountOfLibraryQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.runtime.runonce.tests.requestCountOfLibrary pattern,
 * to be used in conjunction with {@link RequestCountOfLibraryMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see RequestCountOfLibraryMatcher
 * @see RequestCountOfLibraryProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class RequestCountOfLibraryMatch extends BasePatternMatch {
  private Library fLibrary;
  
  private Integer fReqCount;
  
  private static List<String> parameterNames = makeImmutableList("library", "reqCount");
  
  private RequestCountOfLibraryMatch(final Library pLibrary, final Integer pReqCount) {
    this.fLibrary = pLibrary;
    this.fReqCount = pReqCount;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("library".equals(parameterName)) return this.fLibrary;
    if ("reqCount".equals(parameterName)) return this.fReqCount;
    return null;
    
  }
  
  public Library getLibrary() {
    return this.fLibrary;
    
  }
  
  public Integer getReqCount() {
    return this.fReqCount;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("library".equals(parameterName) ) {
    	this.fLibrary = (org.eclipse.incquery.examples.eiqlibrary.Library) newValue;
    	return true;
    }
    if ("reqCount".equals(parameterName) ) {
    	this.fReqCount = (java.lang.Integer) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setLibrary(final Library pLibrary) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fLibrary = pLibrary;
    
  }
  
  public void setReqCount(final Integer pReqCount) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fReqCount = pReqCount;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.requestCountOfLibrary";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return RequestCountOfLibraryMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fLibrary, fReqCount};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"library\"=" + prettyPrintValue(fLibrary) + ", ");
    result.append("\"reqCount\"=" + prettyPrintValue(fReqCount));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fLibrary == null) ? 0 : fLibrary.hashCode());
    result = prime * result + ((fReqCount == null) ? 0 : fReqCount.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof RequestCountOfLibraryMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    RequestCountOfLibraryMatch other = (RequestCountOfLibraryMatch) obj;
    if (fLibrary == null) {if (other.fLibrary != null) return false;}
    else if (!fLibrary.equals(other.fLibrary)) return false;
    if (fReqCount == null) {if (other.fReqCount != null) return false;}
    else if (!fReqCount.equals(other.fReqCount)) return false;
    return true;
  }
  
  @Override
  public RequestCountOfLibraryQuerySpecification specification() {
    try {
    	return RequestCountOfLibraryQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends RequestCountOfLibraryMatch {
    Mutable(final Library pLibrary, final Integer pReqCount) {
      super(pLibrary, pReqCount);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends RequestCountOfLibraryMatch {
    Immutable(final Library pLibrary, final Integer pReqCount) {
      super(pLibrary, pReqCount);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
