package org.eclipse.incquery.testing.queries;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecordMatcher;
import org.eclipse.incquery.testing.queries.CorrespondingRecordsMatcher;
import org.eclipse.incquery.testing.queries.IncorrectSubstitutionMatcher;
import org.eclipse.incquery.testing.queries.RecordRoleValueMatcher;
import org.eclipse.incquery.testing.queries.SubstitutionValueMatcher;
import org.eclipse.incquery.testing.queries.UnexpectedMatchRecordMatcher;

@SuppressWarnings("all")
public final class MatchRecordQueriesMatchers {
  private IncQueryEngine engine;
  
  public MatchRecordQueriesMatchers(final IncQueryEngine engine) {
    this.engine = engine;
    
  }
  
  public RecordRoleValueMatcher getRecordRoleValueMatcher() throws IncQueryException {
    return RecordRoleValueMatcher.on(engine);
  }
  
  public CorrespondingRecordInMatchSetRecordMatcher getCorrespondingRecordInMatchSetRecordMatcher() throws IncQueryException {
    return CorrespondingRecordInMatchSetRecordMatcher.on(engine);
  }
  
  public UnexpectedMatchRecordMatcher getUnexpectedMatchRecordMatcher() throws IncQueryException {
    return UnexpectedMatchRecordMatcher.on(engine);
  }
  
  public CorrespondingRecordsMatcher getCorrespondingRecordsMatcher() throws IncQueryException {
    return CorrespondingRecordsMatcher.on(engine);
  }
  
  public IncorrectSubstitutionMatcher getIncorrectSubstitutionMatcher() throws IncQueryException {
    return IncorrectSubstitutionMatcher.on(engine);
  }
  
  public SubstitutionValueMatcher getSubstitutionValueMatcher() throws IncQueryException {
    return SubstitutionValueMatcher.on(engine);
  }
}
