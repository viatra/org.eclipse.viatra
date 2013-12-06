package org.eclipse.incquery.runtime.runonce.tests;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.runonce.tests.BookAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.BooksWithMultipleAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.LongSciFiBooksOfAuthorMatcher;
import org.eclipse.incquery.runtime.runonce.tests.RequestCountOfLibraryMatcher;
import org.eclipse.incquery.runtime.runonce.tests.SingleAuthoredFirstBooksMatcher;
import org.eclipse.incquery.runtime.runonce.tests.SomeBooksWithTwoAuthorsMatcher;
import org.eclipse.incquery.runtime.runonce.tests.SumOfPagesInLibraryMatcher;

@SuppressWarnings("all")
public final class EiqlibraryMatchers {
  private IncQueryEngine engine;
  
  public EiqlibraryMatchers(final IncQueryEngine engine) {
    this.engine = engine;
    
  }
  
  public SumOfPagesInLibraryMatcher getSumOfPagesInLibraryMatcher() throws IncQueryException {
    return SumOfPagesInLibraryMatcher.on(engine);
  }
  
  public SomeBooksWithTwoAuthorsMatcher getSomeBooksWithTwoAuthorsMatcher() throws IncQueryException {
    return SomeBooksWithTwoAuthorsMatcher.on(engine);
  }
  
  public LongSciFiBooksOfAuthorMatcher getLongSciFiBooksOfAuthorMatcher() throws IncQueryException {
    return LongSciFiBooksOfAuthorMatcher.on(engine);
  }
  
  public RequestCountOfLibraryMatcher getRequestCountOfLibraryMatcher() throws IncQueryException {
    return RequestCountOfLibraryMatcher.on(engine);
  }
  
  public BooksWithMultipleAuthorsMatcher getBooksWithMultipleAuthorsMatcher() throws IncQueryException {
    return BooksWithMultipleAuthorsMatcher.on(engine);
  }
  
  public BookAuthorsMatcher getBookAuthorsMatcher() throws IncQueryException {
    return BookAuthorsMatcher.on(engine);
  }
  
  public SingleAuthoredFirstBooksMatcher getSingleAuthoredFirstBooksMatcher() throws IncQueryException {
    return SingleAuthoredFirstBooksMatcher.on(engine);
  }
}
