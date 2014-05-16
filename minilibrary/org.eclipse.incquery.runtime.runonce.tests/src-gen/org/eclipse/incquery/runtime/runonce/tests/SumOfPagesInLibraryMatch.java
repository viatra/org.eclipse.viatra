package org.eclipse.incquery.runtime.runonce.tests;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.examples.eiqlibrary.Library;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.runonce.tests.util.SumOfPagesInLibraryQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.runtime.runonce.tests.sumOfPagesInLibrary pattern,
 * to be used in conjunction with {@link SumOfPagesInLibraryMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see SumOfPagesInLibraryMatcher
 * @see SumOfPagesInLibraryProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class SumOfPagesInLibraryMatch extends BasePatternMatch {
  private Library fLibrary;
  
  private Integer fSumOfPages;
  
  private static List<String> parameterNames = makeImmutableList("library", "sumOfPages");
  
  private SumOfPagesInLibraryMatch(final Library pLibrary, final Integer pSumOfPages) {
    this.fLibrary = pLibrary;
    this.fSumOfPages = pSumOfPages;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("library".equals(parameterName)) return this.fLibrary;
    if ("sumOfPages".equals(parameterName)) return this.fSumOfPages;
    return null;
    
  }
  
  public Library getLibrary() {
    return this.fLibrary;
    
  }
  
  public Integer getSumOfPages() {
    return this.fSumOfPages;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("library".equals(parameterName) ) {
    	this.fLibrary = (org.eclipse.incquery.examples.eiqlibrary.Library) newValue;
    	return true;
    }
    if ("sumOfPages".equals(parameterName) ) {
    	this.fSumOfPages = (java.lang.Integer) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setLibrary(final Library pLibrary) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fLibrary = pLibrary;
    
  }
  
  public void setSumOfPages(final Integer pSumOfPages) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fSumOfPages = pSumOfPages;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.sumOfPagesInLibrary";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return SumOfPagesInLibraryMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fLibrary, fSumOfPages};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"library\"=" + prettyPrintValue(fLibrary) + ", ");
    result.append("\"sumOfPages\"=" + prettyPrintValue(fSumOfPages));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fLibrary == null) ? 0 : fLibrary.hashCode());
    result = prime * result + ((fSumOfPages == null) ? 0 : fSumOfPages.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof SumOfPagesInLibraryMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    SumOfPagesInLibraryMatch other = (SumOfPagesInLibraryMatch) obj;
    if (fLibrary == null) {if (other.fLibrary != null) return false;}
    else if (!fLibrary.equals(other.fLibrary)) return false;
    if (fSumOfPages == null) {if (other.fSumOfPages != null) return false;}
    else if (!fSumOfPages.equals(other.fSumOfPages)) return false;
    return true;
  }
  
  @Override
  public SumOfPagesInLibraryQuerySpecification specification() {
    try {
    	return SumOfPagesInLibraryQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends SumOfPagesInLibraryMatch {
    Mutable(final Library pLibrary, final Integer pSumOfPages) {
      super(pLibrary, pSumOfPages);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends SumOfPagesInLibraryMatch {
    Immutable(final Library pLibrary, final Integer pSumOfPages) {
      super(pLibrary, pSumOfPages);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
