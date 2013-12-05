package org.eclipse.incquery.runtime.runonce.tests;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.examples.eiqlibrary.Book;
import org.eclipse.incquery.examples.eiqlibrary.Writer;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.runtime.runonce.tests.bookAuthors pattern, 
 * to be used in conjunction with {@link BookAuthorsMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see BookAuthorsMatcher
 * @see BookAuthorsProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class BookAuthorsMatch extends BasePatternMatch {
  private Book fBook;
  
  private Writer fAuthor;
  
  private static List<String> parameterNames = makeImmutableList("book", "author");
  
  private BookAuthorsMatch(final Book pBook, final Writer pAuthor) {
    this.fBook = pBook;
    this.fAuthor = pAuthor;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("book".equals(parameterName)) return this.fBook;
    if ("author".equals(parameterName)) return this.fAuthor;
    return null;
    
  }
  
  public Book getBook() {
    return this.fBook;
    
  }
  
  public Writer getAuthor() {
    return this.fAuthor;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("book".equals(parameterName) ) {
    	this.fBook = (org.eclipse.incquery.examples.eiqlibrary.Book) newValue;
    	return true;
    }
    if ("author".equals(parameterName) ) {
    	this.fAuthor = (org.eclipse.incquery.examples.eiqlibrary.Writer) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setBook(final Book pBook) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fBook = pBook;
    
  }
  
  public void setAuthor(final Writer pAuthor) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fAuthor = pAuthor;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.runtime.runonce.tests.bookAuthors";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return BookAuthorsMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fBook, fAuthor};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"book\"=" + prettyPrintValue(fBook) + ", ");
    result.append("\"author\"=" + prettyPrintValue(fAuthor));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fBook == null) ? 0 : fBook.hashCode()); 
    result = prime * result + ((fAuthor == null) ? 0 : fAuthor.hashCode()); 
    return result; 
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof BookAuthorsMatch)) { // this should be infrequent				
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!pattern().equals(otherSig.pattern()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    BookAuthorsMatch other = (BookAuthorsMatch) obj;
    if (fBook == null) {if (other.fBook != null) return false;}
    else if (!fBook.equals(other.fBook)) return false;
    if (fAuthor == null) {if (other.fAuthor != null) return false;}
    else if (!fAuthor.equals(other.fAuthor)) return false;
    return true;
  }
  
  @Override
  public Pattern pattern() {
    try {
    	return BookAuthorsMatcher.querySpecification().getPattern();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends BookAuthorsMatch {
    Mutable(final Book pBook, final Writer pAuthor) {
      super(pBook, pAuthor);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends BookAuthorsMatch {
    Immutable(final Book pBook, final Writer pAuthor) {
      super(pBook, pAuthor);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
