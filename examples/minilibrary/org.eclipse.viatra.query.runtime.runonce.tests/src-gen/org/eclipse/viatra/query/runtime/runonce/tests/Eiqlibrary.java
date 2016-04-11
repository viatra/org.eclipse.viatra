package org.eclipse.viatra.query.runtime.runonce.tests;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.runonce.tests.BookAuthorsMatcher;
import org.eclipse.viatra.query.runtime.runonce.tests.BooksWithMultipleAuthorsMatcher;
import org.eclipse.viatra.query.runtime.runonce.tests.LongSciFiBooksOfAuthorMatcher;
import org.eclipse.viatra.query.runtime.runonce.tests.RequestCountOfLibraryMatcher;
import org.eclipse.viatra.query.runtime.runonce.tests.SingleAuthoredFirstBooksMatcher;
import org.eclipse.viatra.query.runtime.runonce.tests.SomeBooksWithTwoAuthorsMatcher;
import org.eclipse.viatra.query.runtime.runonce.tests.SumOfPagesInLibraryMatcher;
import org.eclipse.viatra.query.runtime.runonce.tests.util.BookAuthorsQuerySpecification;
import org.eclipse.viatra.query.runtime.runonce.tests.util.BooksWithMultipleAuthorsQuerySpecification;
import org.eclipse.viatra.query.runtime.runonce.tests.util.LongSciFiBooksOfAuthorQuerySpecification;
import org.eclipse.viatra.query.runtime.runonce.tests.util.RequestCountOfLibraryQuerySpecification;
import org.eclipse.viatra.query.runtime.runonce.tests.util.SingleAuthoredFirstBooksQuerySpecification;
import org.eclipse.viatra.query.runtime.runonce.tests.util.SomeBooksWithTwoAuthorsQuerySpecification;
import org.eclipse.viatra.query.runtime.runonce.tests.util.SumOfPagesInLibraryQuerySpecification;

/**
 * A pattern group formed of all patterns defined in eiqlibrary.vql.
 * 
 * <p>Use the static instance as any {@link org.eclipse.viatra.query.runtime.api.IPatternGroup}, to conveniently prepare
 * a VIATRA Query engine for matching all patterns originally defined in file eiqlibrary.vql,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package org.eclipse.viatra.query.runtime.runonce.tests, the group contains the definition of the following patterns: <ul>
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
   * @throws ViatraQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static Eiqlibrary instance() throws ViatraQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new Eiqlibrary();
    }
    return INSTANCE;
  }
  
  private static Eiqlibrary INSTANCE;
  
  private Eiqlibrary() throws ViatraQueryException {
    querySpecifications.add(BookAuthorsQuerySpecification.instance());
    querySpecifications.add(BooksWithMultipleAuthorsQuerySpecification.instance());
    querySpecifications.add(SumOfPagesInLibraryQuerySpecification.instance());
    querySpecifications.add(SingleAuthoredFirstBooksQuerySpecification.instance());
    querySpecifications.add(LongSciFiBooksOfAuthorQuerySpecification.instance());
    querySpecifications.add(RequestCountOfLibraryQuerySpecification.instance());
    querySpecifications.add(SomeBooksWithTwoAuthorsQuerySpecification.instance());
  }
  
  public BookAuthorsQuerySpecification getBookAuthors() throws ViatraQueryException {
    return BookAuthorsQuerySpecification.instance();
  }
  
  public BookAuthorsMatcher getBookAuthors(final ViatraQueryEngine engine) throws ViatraQueryException {
    return BookAuthorsMatcher.on(engine);
  }
  
  public BooksWithMultipleAuthorsQuerySpecification getBooksWithMultipleAuthors() throws ViatraQueryException {
    return BooksWithMultipleAuthorsQuerySpecification.instance();
  }
  
  public BooksWithMultipleAuthorsMatcher getBooksWithMultipleAuthors(final ViatraQueryEngine engine) throws ViatraQueryException {
    return BooksWithMultipleAuthorsMatcher.on(engine);
  }
  
  public SumOfPagesInLibraryQuerySpecification getSumOfPagesInLibrary() throws ViatraQueryException {
    return SumOfPagesInLibraryQuerySpecification.instance();
  }
  
  public SumOfPagesInLibraryMatcher getSumOfPagesInLibrary(final ViatraQueryEngine engine) throws ViatraQueryException {
    return SumOfPagesInLibraryMatcher.on(engine);
  }
  
  public SingleAuthoredFirstBooksQuerySpecification getSingleAuthoredFirstBooks() throws ViatraQueryException {
    return SingleAuthoredFirstBooksQuerySpecification.instance();
  }
  
  public SingleAuthoredFirstBooksMatcher getSingleAuthoredFirstBooks(final ViatraQueryEngine engine) throws ViatraQueryException {
    return SingleAuthoredFirstBooksMatcher.on(engine);
  }
  
  public LongSciFiBooksOfAuthorQuerySpecification getLongSciFiBooksOfAuthor() throws ViatraQueryException {
    return LongSciFiBooksOfAuthorQuerySpecification.instance();
  }
  
  public LongSciFiBooksOfAuthorMatcher getLongSciFiBooksOfAuthor(final ViatraQueryEngine engine) throws ViatraQueryException {
    return LongSciFiBooksOfAuthorMatcher.on(engine);
  }
  
  public RequestCountOfLibraryQuerySpecification getRequestCountOfLibrary() throws ViatraQueryException {
    return RequestCountOfLibraryQuerySpecification.instance();
  }
  
  public RequestCountOfLibraryMatcher getRequestCountOfLibrary(final ViatraQueryEngine engine) throws ViatraQueryException {
    return RequestCountOfLibraryMatcher.on(engine);
  }
  
  public SomeBooksWithTwoAuthorsQuerySpecification getSomeBooksWithTwoAuthors() throws ViatraQueryException {
    return SomeBooksWithTwoAuthorsQuerySpecification.instance();
  }
  
  public SomeBooksWithTwoAuthorsMatcher getSomeBooksWithTwoAuthors(final ViatraQueryEngine engine) throws ViatraQueryException {
    return SomeBooksWithTwoAuthorsMatcher.on(engine);
  }
}
