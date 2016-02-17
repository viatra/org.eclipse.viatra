package org.eclipse.viatra.query.runtime.runonce.tests.util;

import org.eclipse.viatra.examples.library.Book;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.runonce.tests.BooksWithMultipleAuthorsMatch;

/**
 * A match processor tailored for the org.eclipse.viatra.query.runtime.runonce.tests.booksWithMultipleAuthors pattern.
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
