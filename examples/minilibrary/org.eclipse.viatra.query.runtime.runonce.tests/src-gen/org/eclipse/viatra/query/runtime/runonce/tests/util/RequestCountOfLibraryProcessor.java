package org.eclipse.viatra.query.runtime.runonce.tests.util;

import org.eclipse.viatra.examples.library.Library;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.runonce.tests.RequestCountOfLibraryMatch;

/**
 * A match processor tailored for the org.eclipse.viatra.query.runtime.runonce.tests.requestCountOfLibrary pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class RequestCountOfLibraryProcessor implements IMatchProcessor<RequestCountOfLibraryMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pLibrary the value of pattern parameter library in the currently processed match
   * @param pReqCount the value of pattern parameter reqCount in the currently processed match
   * 
   */
  public abstract void process(final Library pLibrary, final Integer pReqCount);
  
  @Override
  public void process(final RequestCountOfLibraryMatch match) {
    process(match.getLibrary(), match.getReqCount());
  }
}
