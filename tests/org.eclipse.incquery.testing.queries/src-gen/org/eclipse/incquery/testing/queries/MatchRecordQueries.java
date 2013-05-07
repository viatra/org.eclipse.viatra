package org.eclipse.incquery.testing.queries;

import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.testing.queries.RecordRoleValueMatcher;
import org.eclipse.incquery.testing.queries.SubstitutionValueMatcher;
import org.eclipse.incquery.testing.queries.UnexpectedMatchRecordMatcher;

public final class MatchRecordQueries extends BaseGeneratedPatternGroup {
  public MatchRecordQueries() throws IncQueryException {
    querySpecifications.add(UnexpectedMatchRecordMatcher.querySpecification());
    querySpecifications.add(SubstitutionValueMatcher.querySpecification());
    querySpecifications.add(RecordRoleValueMatcher.querySpecification());
    
  }
}
