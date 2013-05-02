package org.eclipse.incquery.testing.queries;

import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.testing.queries.recordrolevalue.RecordRoleValueMatcher;
import org.eclipse.incquery.testing.queries.substitutionvalue.SubstitutionValueMatcher;
import org.eclipse.incquery.testing.queries.unexpectedmatchrecord.UnexpectedMatchRecordMatcher;

public final class GroupOfFileMatchRecord extends BaseGeneratedPatternGroup {
  public GroupOfFileMatchRecord() throws IncQueryException {
    querySpecifications.add(RecordRoleValueMatcher.querySpecification());
    querySpecifications.add(UnexpectedMatchRecordMatcher.querySpecification());
    querySpecifications.add(SubstitutionValueMatcher.querySpecification());
    
  }
}
