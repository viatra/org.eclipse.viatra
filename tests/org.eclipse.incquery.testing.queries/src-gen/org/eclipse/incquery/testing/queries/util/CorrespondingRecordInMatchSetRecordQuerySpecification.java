package org.eclipse.incquery.testing.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.context.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecordMatcher;
import org.eclipse.incquery.testing.queries.util.CorrespondingRecordsQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate CorrespondingRecordInMatchSetRecordMatcher in a type-safe way.
 * 
 * @see CorrespondingRecordInMatchSetRecordMatcher
 * @see CorrespondingRecordInMatchSetRecordMatch
 * 
 */
@SuppressWarnings("all")
public final class CorrespondingRecordInMatchSetRecordQuerySpecification extends BaseGeneratedQuerySpecification<CorrespondingRecordInMatchSetRecordMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static CorrespondingRecordInMatchSetRecordQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected CorrespondingRecordInMatchSetRecordMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return CorrespondingRecordInMatchSetRecordMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecord";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Record","CorrespodingRecord","ExpectedSet");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Record", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord"),new PParameter("CorrespodingRecord", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord"),new PParameter("ExpectedSet", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_Record = body.getOrCreateVariableByName("Record");
      PVariable var_CorrespodingRecord = body.getOrCreateVariableByName("CorrespodingRecord");
      PVariable var_ExpectedSet = body.getOrCreateVariableByName("ExpectedSet");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Record, "Record"), 
        new ExportedParameter(body, var_CorrespodingRecord, "CorrespodingRecord"), 
        new ExportedParameter(body, var_ExpectedSet, "ExpectedSet")
      ));
      
      new TypeUnary(body, var_Record, getClassifierLiteral("http://www.eclipse.org/incquery/snapshot", "MatchRecord"), "http://www.eclipse.org/incquery/snapshot/MatchRecord");
      
      
      new Inequality(body, var_Record, var_CorrespodingRecord);
      new TypeBinary(body, context, var_ExpectedSet, var_CorrespodingRecord, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSetRecord", "matches"), "http://www.eclipse.org/incquery/snapshot/MatchSetRecord.matches");
      new PositivePatternCall(body, new FlatTuple(var_Record, var_CorrespodingRecord), CorrespondingRecordsQuerySpecification.instance());
      bodies.add(body);
    }setStatus(PQuery.PQueryStatus.OK);
    return bodies;
  }
  
  private CorrespondingRecordInMatchSetRecordQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQuery.PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<CorrespondingRecordInMatchSetRecordQuerySpecification> {
    @Override
    public CorrespondingRecordInMatchSetRecordQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static CorrespondingRecordInMatchSetRecordQuerySpecification INSTANCE = make();
    
    public static CorrespondingRecordInMatchSetRecordQuerySpecification make() {
      try {
      	return new CorrespondingRecordInMatchSetRecordQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
