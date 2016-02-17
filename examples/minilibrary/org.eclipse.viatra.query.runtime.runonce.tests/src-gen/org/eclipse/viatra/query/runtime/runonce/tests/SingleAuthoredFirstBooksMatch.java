package org.eclipse.viatra.query.runtime.runonce.tests;

import java.util.Arrays;
import java.util.List;
import org.eclipse.viatra.examples.library.Book;
import org.eclipse.viatra.examples.library.Library;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.query.runtime.runonce.tests.util.SingleAuthoredFirstBooksQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.query.runtime.runonce.tests.singleAuthoredFirstBooks pattern,
 * to be used in conjunction with {@link SingleAuthoredFirstBooksMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see SingleAuthoredFirstBooksMatcher
 * @see SingleAuthoredFirstBooksProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class SingleAuthoredFirstBooksMatch extends BasePatternMatch {
  private Library fLibrary;
  
  private Book fFirstBook;
  
  private static List<String> parameterNames = makeImmutableList("library", "firstBook");
  
  private SingleAuthoredFirstBooksMatch(final Library pLibrary, final Book pFirstBook) {
    this.fLibrary = pLibrary;
    this.fFirstBook = pFirstBook;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("library".equals(parameterName)) return this.fLibrary;
    if ("firstBook".equals(parameterName)) return this.fFirstBook;
    return null;
  }
  
  public Library getLibrary() {
    return this.fLibrary;
  }
  
  public Book getFirstBook() {
    return this.fFirstBook;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("library".equals(parameterName) ) {
    	this.fLibrary = (Library) newValue;
    	return true;
    }
    if ("firstBook".equals(parameterName) ) {
    	this.fFirstBook = (Book) newValue;
    	return true;
    }
    return false;
  }
  
  public void setLibrary(final Library pLibrary) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fLibrary = pLibrary;
  }
  
  public void setFirstBook(final Book pFirstBook) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fFirstBook = pFirstBook;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.query.runtime.runonce.tests.singleAuthoredFirstBooks";
  }
  
  @Override
  public List<String> parameterNames() {
    return SingleAuthoredFirstBooksMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fLibrary, fFirstBook};
  }
  
  @Override
  public SingleAuthoredFirstBooksMatch toImmutable() {
    return isMutable() ? newMatch(fLibrary, fFirstBook) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"library\"=" + prettyPrintValue(fLibrary) + ", ");
    
    result.append("\"firstBook\"=" + prettyPrintValue(fFirstBook)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fLibrary == null) ? 0 : fLibrary.hashCode());
    result = prime * result + ((fFirstBook == null) ? 0 : fFirstBook.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof SingleAuthoredFirstBooksMatch)) { // this should be infrequent
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
    SingleAuthoredFirstBooksMatch other = (SingleAuthoredFirstBooksMatch) obj;
    if (fLibrary == null) {if (other.fLibrary != null) return false;}
    else if (!fLibrary.equals(other.fLibrary)) return false;
    if (fFirstBook == null) {if (other.fFirstBook != null) return false;}
    else if (!fFirstBook.equals(other.fFirstBook)) return false;
    return true;
  }
  
  @Override
  public SingleAuthoredFirstBooksQuerySpecification specification() {
    try {
    	return SingleAuthoredFirstBooksQuerySpecification.instance();
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
  public static SingleAuthoredFirstBooksMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static SingleAuthoredFirstBooksMatch newMutableMatch(final Library pLibrary, final Book pFirstBook) {
    return new Mutable(pLibrary, pFirstBook);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pFirstBook the fixed value of pattern parameter firstBook, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static SingleAuthoredFirstBooksMatch newMatch(final Library pLibrary, final Book pFirstBook) {
    return new Immutable(pLibrary, pFirstBook);
  }
  
  private static final class Mutable extends SingleAuthoredFirstBooksMatch {
    Mutable(final Library pLibrary, final Book pFirstBook) {
      super(pLibrary, pFirstBook);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends SingleAuthoredFirstBooksMatch {
    Immutable(final Library pLibrary, final Book pFirstBook) {
      super(pLibrary, pFirstBook);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
