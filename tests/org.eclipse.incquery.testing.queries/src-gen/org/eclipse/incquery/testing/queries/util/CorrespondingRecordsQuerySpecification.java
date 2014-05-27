package org.eclipse.incquery.testing.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.testing.queries.util.IncorrectSubstitutionQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate CorrespondingRecordsMatcher in a type-safe way.
 * 
 * @see CorrespondingRecordsMatcher
 * @see CorrespondingRecordsMatch
 * 
 */
@SuppressWarnings("all")
final class CorrespondingRecordsQuerySpecification extends BaseGeneratedQuerySpecification<IncQueryMatcher<IPatternMatch>> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static CorrespondingRecordsQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected IncQueryMatcher<IPatternMatch> instantiate(final IncQueryEngine engine) throws IncQueryException {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.testing.queries.CorrespondingRecords";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Record","CorrespondingRecord");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Record", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord"),new PParameter("CorrespondingRecord", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord"));
  }
  
  @Override
  public IPatternMatch newEmptyMatch() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public IPatternMatch newMatch(final Object... parameters) {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_Record = body.getOrCreateVariableByName("Record");
      PVariable var_CorrespondingRecord = body.getOrCreateVariableByName("CorrespondingRecord");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Record, "Record"), 
        new ExportedParameter(body, var_CorrespondingRecord, "CorrespondingRecord")
      ));
      
      new TypeUnary(body, var_Record, getClassifierLiteral("http://www.eclipse.org/incquery/snapshot", "MatchRecord"), "http://www.eclipse.org/incquery/snapshot/MatchRecord");
      
      new TypeUnary(body, var_CorrespondingRecord, getClassifierLiteral("http://www.eclipse.org/incquery/snapshot", "MatchRecord"), "http://www.eclipse.org/incquery/snapshot/MatchRecord");
      new Inequality(body, var_Record, var_CorrespondingRecord);
      new NegativePatternCall(body, new FlatTuple(var_Record, var_CorrespondingRecord), IncorrectSubstitutionQuerySpecification.instance().instance());
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static CorrespondingRecordsQuerySpecification INSTANCE = make();
    
    public static CorrespondingRecordsQuerySpecification make() {
      return new CorrespondingRecordsQuerySpecification();					
      
    }
  }
  
}
