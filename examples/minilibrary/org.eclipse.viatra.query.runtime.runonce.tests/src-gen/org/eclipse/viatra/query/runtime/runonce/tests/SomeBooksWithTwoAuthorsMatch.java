package org.eclipse.viatra.query.runtime.runonce.tests;

import java.util.Arrays;
import java.util.List;
import org.eclipse.viatra.examples.library.Book;
import org.eclipse.viatra.examples.library.Library;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.runonce.tests.util.SomeBooksWithTwoAuthorsQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.query.runtime.runonce.tests.someBooksWithTwoAuthors pattern,
 * to be used in conjunction with {@link SomeBooksWithTwoAuthorsMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see SomeBooksWithTwoAuthorsMatcher
 * @see SomeBooksWithTwoAuthorsProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class SomeBooksWithTwoAuthorsMatch extends BasePatternMatch {
  private Library fLibrary;
  
  private Book fBook;
  
  private static List<String> parameterNames = makeImmutableList("library", "book");
  
  private SomeBooksWithTwoAuthorsMatch(final Library pLibrary, final Book pBook) {
    this.fLibrary = pLibrary;
    this.fBook = pBook;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("library".equals(parameterName)) return this.fLibrary;
    if ("book".equals(parameterName)) return this.fBook;
    return null;
  }
  
  public Library getLibrary() {
    return this.fLibrary;
  }
  
  public Book getBook() {
    return this.fBook;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("library".equals(parameterName) ) {
    	this.fLibrary = (Library) newValue;
    	return true;
    }
    if ("book".equals(parameterName) ) {
    	this.fBook = (Book) newValue;
    	return true;
    }
    return false;
  }
  
  public void setLibrary(final Library pLibrary) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fLibrary = pLibrary;
  }
  
  public void setBook(final Book pBook) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fBook = pBook;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.query.runtime.runonce.tests.someBooksWithTwoAuthors";
  }
  
  @Override
  public List<String> parameterNames() {
    return SomeBooksWithTwoAuthorsMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fLibrary, fBook};
  }
  
  @Override
  public SomeBooksWithTwoAuthorsMatch toImmutable() {
    return isMutable() ? newMatch(fLibrary, fBook) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"library\"=" + prettyPrintValue(fLibrary) + ", ");
    
    result.append("\"book\"=" + prettyPrintValue(fBook)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fLibrary == null) ? 0 : fLibrary.hashCode());
    result = prime * result + ((fBook == null) ? 0 : fBook.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof SomeBooksWithTwoAuthorsMatch)) { // this should be infrequent
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
    SomeBooksWithTwoAuthorsMatch other = (SomeBooksWithTwoAuthorsMatch) obj;
    if (fLibrary == null) {if (other.fLibrary != null) return false;}
    else if (!fLibrary.equals(other.fLibrary)) return false;
    if (fBook == null) {if (other.fBook != null) return false;}
    else if (!fBook.equals(other.fBook)) return false;
    return true;
  }
  
  @Override
  public SomeBooksWithTwoAuthorsQuerySpecification specification() {
    try {
    	return SomeBooksWithTwoAuthorsQuerySpecification.instance();
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
  public static SomeBooksWithTwoAuthorsMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static SomeBooksWithTwoAuthorsMatch newMutableMatch(final Library pLibrary, final Book pBook) {
    return new Mutable(pLibrary, pBook);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pLibrary the fixed value of pattern parameter library, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static SomeBooksWithTwoAuthorsMatch newMatch(final Library pLibrary, final Book pBook) {
    return new Immutable(pLibrary, pBook);
  }
  
  private static final class Mutable extends SomeBooksWithTwoAuthorsMatch {
    Mutable(final Library pLibrary, final Book pBook) {
      super(pLibrary, pBook);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends SomeBooksWithTwoAuthorsMatch {
    Immutable(final Library pLibrary, final Book pBook) {
      super(pLibrary, pBook);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
