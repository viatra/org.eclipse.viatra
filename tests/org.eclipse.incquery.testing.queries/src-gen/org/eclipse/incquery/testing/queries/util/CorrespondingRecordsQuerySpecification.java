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
import org.eclipse.incquery.runtime.matchers.psystem.PQuery.PQueryStatus;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.testing.queries.CorrespondingRecordsMatcher;
import org.eclipse.incquery.testing.queries.util.IncorrectSubstitutionQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate CorrespondingRecordsMatcher in a type-safe way.
 * 
 * @see CorrespondingRecordsMatcher
 * @see CorrespondingRecordsMatch
 * 
 */
@SuppressWarnings("all")
public final class CorrespondingRecordsQuerySpecification extends BaseGeneratedQuerySpecification<CorrespondingRecordsMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static CorrespondingRecordsQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected CorrespondingRecordsMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return CorrespondingRecordsMatcher.on(engine);
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
  public Set<PBody> doGetContainedBodies() {
    return bodies;
  }
  
  private CorrespondingRecordsQuerySpecification() throws IncQueryException {
    super();
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    {
      PBody body = new PBody(this);
      PVariable var_Record = body.getOrCreateVariableByName("Record");
      PVariable var_CorrespondingRecord = body.getOrCreateVariableByName("CorrespondingRecord");
      new ExportedParameter(body, var_Record, "Record");
      new TypeUnary(body, var_Record, getClassifierLiteral("http://www.eclipse.org/incquery/snapshot", "MatchRecord"), "http://www.eclipse.org/incquery/snapshot/MatchRecord");
      new ExportedParameter(body, var_CorrespondingRecord, "CorrespondingRecord");
      new TypeUnary(body, var_CorrespondingRecord, getClassifierLiteral("http://www.eclipse.org/incquery/snapshot", "MatchRecord"), "http://www.eclipse.org/incquery/snapshot/MatchRecord");
      new Inequality(body, var_Record, var_CorrespondingRecord);
      new NegativePatternCall(body, new FlatTuple(var_Record, var_CorrespondingRecord), IncorrectSubstitutionQuerySpecification.instance());
      body.setSymbolicParameters(Arrays.asList(var_Record, var_CorrespondingRecord));
      bodies.add(body);
    }
    setStatus(PQueryStatus.OK);
  }
  
  private Set<PBody> bodies = Sets.newHashSet();;
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<CorrespondingRecordsQuerySpecification> {
    @Override
    public CorrespondingRecordsQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static CorrespondingRecordsQuerySpecification INSTANCE = make();
    
    public static CorrespondingRecordsQuerySpecification make() {
      try {
      	return new CorrespondingRecordsQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
