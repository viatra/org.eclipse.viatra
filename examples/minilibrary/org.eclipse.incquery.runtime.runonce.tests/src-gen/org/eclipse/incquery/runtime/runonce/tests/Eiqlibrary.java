package org.eclipse.incquery.runtime.runonce.tests;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.runonce.tests.BookAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.BooksWithMultipleAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.LongSciFiBooksOfAuthorMatcher;
import org.eclipse.incquery.runtime.runonce.tests.RequestCountOfLibraryMatcher;
import org.eclipse.incquery.runtime.runonce.tests.SingleAuthoredFirstBooksMatcher;
import org.eclipse.incquery.runtime.runonce.tests.SomeBooksWithTwoAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.SumOfPagesInLibraryMatcher;
import org.eclipse.incquery.runtime.runonce.tests.util.BookAuthorsQuerySpecification;
import org.eclipse.incquery.runtime.runonce.tests.util.BooksWithMultipleAuthorsQuerySpecification;
import org.eclipse.incquery.runtime.runonce.tests.util.LongSciFiBooksOfAuthorQuerySpecification;
import org.eclipse.incquery.runtime.runonce.tests.util.RequestCountOfLibraryQuerySpecification;
import org.eclipse.incquery.runtime.runonce.tests.util.SingleAuthoredFirstBooksQuerySpecification;
import org.eclipse.incquery.runtime.runonce.tests.util.SomeBooksWithTwoAuthorsQuerySpecification;
import org.eclipse.incquery.runtime.runonce.tests.util.SumOfPagesInLibraryQuerySpecification;

/**
 * A pattern group formed of all patterns defined in eiqlibrary.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file eiqlibrary.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package org.eclipse.incquery.runtime.runonce.tests, the group contains the definition of the following patterns: <ul>
 * <li>bookAuthors</li>
 * <li>booksWithMultipleAuthors</li>
 * <li>sumOfPagesInLibrary</li>
 * <li>singleAuthoredFirstBooks</li>
 * <li>longSciFiBooksOfAuthor</li>
 * <li>requestCountOfLibrary</li>
 * <li>someBooksWithTwoAuthors</li>
 * </ul>
 * 
 * @see IPatternGroup
 * 
 */
@SuppressWarnings("all")
public final class Eiqlibrary extends BaseGeneratedPatternGroup {
  /**
   * Access the pattern group.
   * 
   * @return the singleton instance of the group
   * @throws IncQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static Eiqlibrary instance() throws IncQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new Eiqlibrary();
    }
    return INSTANCE;
    
  }
  
  private static Eiqlibrary INSTANCE;
  
  private Eiqlibrary() throws IncQueryException {
    querySpecifications.add(BookAuthorsQuerySpecification.instance());
    querySpecifications.add(BooksWithMultipleAuthorsQuerySpecification.instance());
    querySpecifications.add(SumOfPagesInLibraryQuerySpecification.instance());
    querySpecifications.add(SingleAuthoredFirstBooksQuerySpecification.instance());
    querySpecifications.add(LongSciFiBooksOfAuthorQuerySpecification.instance());
    querySpecifications.add(RequestCountOfLibraryQuerySpecification.instance());
    querySpecifications.add(SomeBooksWithTwoAuthorsQuerySpecification.instance());
    
  }
  
  public BookAuthorsQuerySpecification getBookAuthors() throws IncQueryException {
    return BookAuthorsQuerySpecification.instance();
  }
  
  public BookAuthorsMatcher getBookAuthors(final IncQueryEngine engine) throws IncQueryException {
    return BookAuthorsMatcher.on(engine);
  }
  
  public BooksWithMultipleAuthorsQuerySpecification getBooksWithMultipleAuthors() throws IncQueryException {
    return BooksWithMultipleAuthorsQuerySpecification.instance();
  }
  
  public BooksWithMultipleAuthorsMatcher getBooksWithMultipleAuthors(final IncQueryEngine engine) throws IncQueryException {
    return BooksWithMultipleAuthorsMatcher.on(engine);
  }
  
  public SumOfPagesInLibraryQuerySpecification getSumOfPagesInLibrary() throws IncQueryException {
    return SumOfPagesInLibraryQuerySpecification.instance();
  }
  
  public SumOfPagesInLibraryMatcher getSumOfPagesInLibrary(final IncQueryEngine engine) throws IncQueryException {
    return SumOfPagesInLibraryMatcher.on(engine);
  }
  
  public SingleAuthoredFirstBooksQuerySpecification getSingleAuthoredFirstBooks() throws IncQueryException {
    return SingleAuthoredFirstBooksQuerySpecification.instance();
  }
  
  public SingleAuthoredFirstBooksMatcher getSingleAuthoredFirstBooks(final IncQueryEngine engine) throws IncQueryException {
    return SingleAuthoredFirstBooksMatcher.on(engine);
  }
  
  public LongSciFiBooksOfAuthorQuerySpecification getLongSciFiBooksOfAuthor() throws IncQueryException {
    return LongSciFiBooksOfAuthorQuerySpecification.instance();
  }
  
  public LongSciFiBooksOfAuthorMatcher getLongSciFiBooksOfAuthor(final IncQueryEngine engine) throws IncQueryException {
    return LongSciFiBooksOfAuthorMatcher.on(engine);
  }
  
  public RequestCountOfLibraryQuerySpecification getRequestCountOfLibrary() throws IncQueryException {
    return RequestCountOfLibraryQuerySpecification.instance();
  }
  
  public RequestCountOfLibraryMatcher getRequestCountOfLibrary(final IncQueryEngine engine) throws IncQueryException {
    return RequestCountOfLibraryMatcher.on(engine);
  }
  
  public SomeBooksWithTwoAuthorsQuerySpecification getSomeBooksWithTwoAuthors() throws IncQueryException {
    return SomeBooksWithTwoAuthorsQuerySpecification.instance();
  }
  
  public SomeBooksWithTwoAuthorsMatcher getSomeBooksWithTwoAuthors(final IncQueryEngine engine) throws IncQueryException {
    return SomeBooksWithTwoAuthorsMatcher.on(engine);
  }
}
