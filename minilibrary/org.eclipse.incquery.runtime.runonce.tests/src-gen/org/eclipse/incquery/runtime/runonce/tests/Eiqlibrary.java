package org.eclipse.incquery.runtime.runonce.tests;

import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.runonce.tests.BookAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.BooksWithMultipleAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.LongSciFiBooksOfAuthorMatcher;
import org.eclipse.incquery.runtime.runonce.tests.RequestCountOfLibraryMatcher;
import org.eclipse.incquery.runtime.runonce.tests.SingleAuthoredFirstBooksMatcher;
import org.eclipse.incquery.runtime.runonce.tests.SomeBooksWithTwoAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.SumOfPagesInLibraryMatcher;

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
    querySpecifications.add(SumOfPagesInLibraryMatcher.querySpecification());
    querySpecifications.add(LongSciFiBooksOfAuthorMatcher.querySpecification());
    querySpecifications.add(BooksWithMultipleAuthorsMatcher.querySpecification());
    querySpecifications.add(SingleAuthoredFirstBooksMatcher.querySpecification());
    querySpecifications.add(RequestCountOfLibraryMatcher.querySpecification());
    querySpecifications.add(SomeBooksWithTwoAuthorsMatcher.querySpecification());
    querySpecifications.add(BookAuthorsMatcher.querySpecification());
    
  }
}
