package org.eclipse.viatra.query.runtime.runonce.tests.util;

import org.eclipse.viatra.examples.library.Library;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.runonce.tests.SumOfPagesInLibraryMatch;

/**
 * A match processor tailored for the org.eclipse.viatra.query.runtime.runonce.tests.sumOfPagesInLibrary pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class SumOfPagesInLibraryProcessor implements IMatchProcessor<SumOfPagesInLibraryMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pLibrary the value of pattern parameter library in the currently processed match
   * @param pSumOfPages the value of pattern parameter sumOfPages in the currently processed match
   * 
   */
  public abstract void process(final Library pLibrary, final Integer pSumOfPages);
  
  @Override
  public void process(final SumOfPagesInLibraryMatch match) {
    process(match.getLibrary(), match.getSumOfPages());
  }
}
