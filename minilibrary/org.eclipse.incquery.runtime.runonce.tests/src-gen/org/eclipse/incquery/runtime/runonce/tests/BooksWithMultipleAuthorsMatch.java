package org.eclipse.incquery.runtime.runonce.tests;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.examples.eiqlibrary.Book;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.runonce.tests.util.BooksWithMultipleAuthorsQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.runtime.runonce.tests.booksWithMultipleAuthors pattern,
 * to be used in conjunction with {@link BooksWithMultipleAuthorsMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see BooksWithMultipleAuthorsMatcher
 * @see BooksWithMultipleAuthorsProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class BooksWithMultipleAuthorsMatch extends BasePatternMatch {
  private Book fBook;
  
  private static List<String> parameterNames = makeImmutableList("book");
  
  private BooksWithMultipleAuthorsMatch(final Book pBook) {
    this.fBook = pBook;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("book".equals(parameterName)) return this.fBook;
    return null;
    
  }
  
  public Book getBook() {
    return this.fBook;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("book".equals(parameterName) ) {
    	this.fBook = (org.eclipse.incquery.examples.eiqlibrary.Book) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setBook(final Book pBook) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fBook = pBook;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.booksWithMultipleAuthors";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return BooksWithMultipleAuthorsMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fBook};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"book\"=" + prettyPrintValue(fBook));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fBook == null) ? 0 : fBook.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof BooksWithMultipleAuthorsMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    BooksWithMultipleAuthorsMatch other = (BooksWithMultipleAuthorsMatch) obj;
    if (fBook == null) {if (other.fBook != null) return false;}
    else if (!fBook.equals(other.fBook)) return false;
    return true;
  }
  
  @Override
  public BooksWithMultipleAuthorsQuerySpecification specification() {
    try {
    	return BooksWithMultipleAuthorsQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends BooksWithMultipleAuthorsMatch {
    Mutable(final Book pBook) {
      super(pBook);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends BooksWithMultipleAuthorsMatch {
    Immutable(final Book pBook) {
      super(pBook);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
