package org.eclipse.viatra.query.runtime.runonce.tests.util;

import org.eclipse.viatra.examples.library.Book;
import org.eclipse.viatra.examples.library.Library;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.runonce.tests.SomeBooksWithTwoAuthorsMatch;

/**
 * A match processor tailored for the org.eclipse.viatra.query.runtime.runonce.tests.someBooksWithTwoAuthors pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class SomeBooksWithTwoAuthorsProcessor implements IMatchProcessor<SomeBooksWithTwoAuthorsMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pLibrary the value of pattern parameter library in the currently processed match
   * @param pBook the value of pattern parameter book in the currently processed match
   * 
   */
  public abstract void process(final Library pLibrary, final Book pBook);
  
  @Override
  public void process(final SomeBooksWithTwoAuthorsMatch match) {
    process(match.getLibrary(), match.getBook());
  }
}
