package org.eclipse.incquery.runtime.runonce.tests.util;

import org.eclipse.incquery.examples.eiqlibrary.Book;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.runonce.tests.BooksWithMultipleAuthorsMatch;

/**
 * A match processor tailored for the org.eclipse.incquery.runtime.runonce.tests.booksWithMultipleAuthors pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class BooksWithMultipleAuthorsProcessor implements IMatchProcessor<BooksWithMultipleAuthorsMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pBook the value of pattern parameter book in the currently processed match 
   * 
   */
  public abstract void process(final Book pBook);
  
  @Override
  public void process(final BooksWithMultipleAuthorsMatch match) {
    process(match.getBook());
    
  }
}
