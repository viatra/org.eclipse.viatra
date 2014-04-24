package org.eclipse.incquery.testing.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Inequality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.NegativePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;
import org.eclipse.incquery.testing.queries.UnexpectedMatchRecordMatcher;
import org.eclipse.incquery.testing.queries.util.CorrespondingRecordInMatchSetRecordQuerySpecification;

/**
 * A pattern-specific query specification that can instantiate UnexpectedMatchRecordMatcher in a type-safe way.
 * 
 * @see UnexpectedMatchRecordMatcher
 * @see UnexpectedMatchRecordMatch
 * 
 */
@SuppressWarnings("all")
public final class UnexpectedMatchRecordQuerySpecification extends BaseGeneratedQuerySpecification<UnexpectedMatchRecordMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static UnexpectedMatchRecordQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected UnexpectedMatchRecordMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return UnexpectedMatchRecordMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.testing.queries.UnexpectedMatchRecord";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("ActualSet","ExpectedSet","Record");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("ActualSet", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord"),new PParameter("ExpectedSet", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord"),new PParameter("Record", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_ActualSet = body.getOrCreateVariableByName("ActualSet");
      PVariable var_ExpectedSet = body.getOrCreateVariableByName("ExpectedSet");
      PVariable var_Record = body.getOrCreateVariableByName("Record");
      PVariable var_PatternName = body.getOrCreateVariableByName("PatternName");
      PVariable var__CorrespodingRecord = body.getOrCreateVariableByName("_CorrespodingRecord");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_ActualSet, "ActualSet"), 
        new ExportedParameter(body, var_ExpectedSet, "ExpectedSet"), 
        new ExportedParameter(body, var_Record, "Record")
      ));
      
      
      
      new TypeBinary(body, CONTEXT, var_ActualSet, var_Record, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSetRecord", "matches"), "http://www.eclipse.org/incquery/snapshot/MatchSetRecord.matches");
      new TypeBinary(body, CONTEXT, var_ActualSet, var_PatternName, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSetRecord", "patternQualifiedName"), "http://www.eclipse.org/incquery/snapshot/MatchSetRecord.patternQualifiedName");
      new TypeBinary(body, CONTEXT, var_ExpectedSet, var_PatternName, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSetRecord", "patternQualifiedName"), "http://www.eclipse.org/incquery/snapshot/MatchSetRecord.patternQualifiedName");
      new Inequality(body, var_ActualSet, var_ExpectedSet);
      new NegativePatternCall(body, new FlatTuple(var_Record, var__CorrespodingRecord, var_ExpectedSet), CorrespondingRecordInMatchSetRecordQuerySpecification.instance().instance());
      bodies.add(body);
    }
    {
      PAnnotation annotation = new PAnnotation("QueryExplorer");
      annotation.addAttribute("display",false);
      addAnnotation(annotation);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<UnexpectedMatchRecordQuerySpecification> {
    @Override
    public UnexpectedMatchRecordQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static UnexpectedMatchRecordQuerySpecification INSTANCE = make();
    
    public static UnexpectedMatchRecordQuerySpecification make() {
      return new UnexpectedMatchRecordQuerySpecification();					
      
    }
  }
  
}
