package org.eclipse.incquery.testing.queries.util;

import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord;
import org.eclipse.incquery.testing.queries.SubstitutionValueMatch;

/**
 * A match processor tailored for the org.eclipse.incquery.testing.queries.SubstitutionValue pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class SubstitutionValueProcessor implements IMatchProcessor<SubstitutionValueMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pSubstitution the value of pattern parameter Substitution in the currently processed match 
   * @param pValue the value of pattern parameter Value in the currently processed match 
   * 
   */
  public abstract void process(final MatchSubstitutionRecord pSubstitution, final Object pValue);
  
  @Override
  public void process(final SubstitutionValueMatch match) {
    process(match.getSubstitution(), match.getValue());
    
  }
}
