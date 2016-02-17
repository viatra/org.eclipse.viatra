package org.eclipse.viatra.query.runtime.runonce.tests.util;

import org.eclipse.viatra.examples.library.Book;
import org.eclipse.viatra.examples.library.Writer;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.runonce.tests.LongSciFiBooksOfAuthorMatch;

/**
 * A match processor tailored for the org.eclipse.viatra.query.runtime.runonce.tests.longSciFiBooksOfAuthor pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class LongSciFiBooksOfAuthorProcessor implements IMatchProcessor<LongSciFiBooksOfAuthorMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pAuthor the value of pattern parameter author in the currently processed match
   * @param pBook the value of pattern parameter book in the currently processed match
   * 
   */
  public abstract void process(final Writer pAuthor, final Book pBook);
  
  @Override
  public void process(final LongSciFiBooksOfAuthorMatch match) {
    process(match.getAuthor(), match.getBook());
  }
}
