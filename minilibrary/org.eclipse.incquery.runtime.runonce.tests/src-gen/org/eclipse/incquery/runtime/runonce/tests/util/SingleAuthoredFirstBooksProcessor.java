package org.eclipse.incquery.runtime.runonce.tests.util;

import org.eclipse.incquery.examples.eiqlibrary.Book;
import org.eclipse.incquery.examples.eiqlibrary.Library;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.runonce.tests.SingleAuthoredFirstBooksMatch;

/**
 * A match processor tailored for the org.eclipse.incquery.runtime.runonce.tests.singleAuthoredFirstBooks pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class SingleAuthoredFirstBooksProcessor implements IMatchProcessor<SingleAuthoredFirstBooksMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pLibrary the value of pattern parameter library in the currently processed match 
   * @param pFirstBook the value of pattern parameter firstBook in the currently processed match 
   * 
   */
  public abstract void process(final Library pLibrary, final Book pFirstBook);
  
  @Override
  public void process(final SingleAuthoredFirstBooksMatch match) {
    process(match.getLibrary(), match.getFirstBook());
    
  }
}
