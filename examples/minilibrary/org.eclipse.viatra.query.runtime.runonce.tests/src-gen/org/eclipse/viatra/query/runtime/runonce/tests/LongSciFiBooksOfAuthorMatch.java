package org.eclipse.viatra.query.runtime.runonce.tests;

import java.util.Arrays;
import java.util.List;
import org.eclipse.viatra.examples.library.Book;
import org.eclipse.viatra.examples.library.Writer;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.impl.BasePatternMatch;
import org.eclipse.viatra.query.runtime.exception.IncQueryException;
import org.eclipse.viatra.query.runtime.runonce.tests.util.LongSciFiBooksOfAuthorQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.viatra.query.runtime.runonce.tests.longSciFiBooksOfAuthor pattern,
 * to be used in conjunction with {@link LongSciFiBooksOfAuthorMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see LongSciFiBooksOfAuthorMatcher
 * @see LongSciFiBooksOfAuthorProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class LongSciFiBooksOfAuthorMatch extends BasePatternMatch {
  private Writer fAuthor;
  
  private Book fBook;
  
  private static List<String> parameterNames = makeImmutableList("author", "book");
  
  private LongSciFiBooksOfAuthorMatch(final Writer pAuthor, final Book pBook) {
    this.fAuthor = pAuthor;
    this.fBook = pBook;
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("author".equals(parameterName)) return this.fAuthor;
    if ("book".equals(parameterName)) return this.fBook;
    return null;
  }
  
  public Writer getAuthor() {
    return this.fAuthor;
  }
  
  public Book getBook() {
    return this.fBook;
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("author".equals(parameterName) ) {
    	this.fAuthor = (Writer) newValue;
    	return true;
    }
    if ("book".equals(parameterName) ) {
    	this.fBook = (Book) newValue;
    	return true;
    }
    return false;
  }
  
  public void setAuthor(final Writer pAuthor) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fAuthor = pAuthor;
  }
  
  public void setBook(final Book pBook) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fBook = pBook;
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.viatra.query.runtime.runonce.tests.longSciFiBooksOfAuthor";
  }
  
  @Override
  public List<String> parameterNames() {
    return LongSciFiBooksOfAuthorMatch.parameterNames;
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fAuthor, fBook};
  }
  
  @Override
  public LongSciFiBooksOfAuthorMatch toImmutable() {
    return isMutable() ? newMatch(fAuthor, fBook) : this;
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"author\"=" + prettyPrintValue(fAuthor) + ", ");
    
    result.append("\"book\"=" + prettyPrintValue(fBook)
    );
    return result.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fAuthor == null) ? 0 : fAuthor.hashCode());
    result = prime * result + ((fBook == null) ? 0 : fBook.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof LongSciFiBooksOfAuthorMatch)) { // this should be infrequent
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
    LongSciFiBooksOfAuthorMatch other = (LongSciFiBooksOfAuthorMatch) obj;
    if (fAuthor == null) {if (other.fAuthor != null) return false;}
    else if (!fAuthor.equals(other.fAuthor)) return false;
    if (fBook == null) {if (other.fBook != null) return false;}
    else if (!fBook.equals(other.fBook)) return false;
    return true;
  }
  
  @Override
  public LongSciFiBooksOfAuthorQuerySpecification specification() {
    try {
    	return LongSciFiBooksOfAuthorQuerySpecification.instance();
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
  public static LongSciFiBooksOfAuthorMatch newEmptyMatch() {
    return new Mutable(null, null);
  }
  
  /**
   * Returns a mutable (partial) match.
   * Fields of the mutable match can be filled to create a partial match, usable as matcher input.
   * 
   * @param pAuthor the fixed value of pattern parameter author, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return the new, mutable (partial) match object.
   * 
   */
  public static LongSciFiBooksOfAuthorMatch newMutableMatch(final Writer pAuthor, final Book pBook) {
    return new Mutable(pAuthor, pBook);
  }
  
  /**
   * Returns a new (partial) match.
   * This can be used e.g. to call the matcher with a partial match.
   * <p>The returned match will be immutable. Use {@link #newEmptyMatch()} to obtain a mutable match object.
   * @param pAuthor the fixed value of pattern parameter author, or null if not bound.
   * @param pBook the fixed value of pattern parameter book, or null if not bound.
   * @return the (partial) match object.
   * 
   */
  public static LongSciFiBooksOfAuthorMatch newMatch(final Writer pAuthor, final Book pBook) {
    return new Immutable(pAuthor, pBook);
  }
  
  private static final class Mutable extends LongSciFiBooksOfAuthorMatch {
    Mutable(final Writer pAuthor, final Book pBook) {
      super(pAuthor, pBook);
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  private static final class Immutable extends LongSciFiBooksOfAuthorMatch {
    Immutable(final Writer pAuthor, final Book pBook) {
      super(pAuthor, pBook);
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
}
