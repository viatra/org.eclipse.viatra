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
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;

/**
 * A pattern-specific query specification that can instantiate IncorrectSubstitutionMatcher in a type-safe way.
 * 
 * @see IncorrectSubstitutionMatcher
 * @see IncorrectSubstitutionMatch
 * 
 */
@SuppressWarnings("all")
final class IncorrectSubstitutionQuerySpecification extends BaseGeneratedQuerySpecification<IncQueryMatcher<IPatternMatch>> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static IncorrectSubstitutionQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected IncQueryMatcher<IPatternMatch> instantiate(final IncQueryEngine engine) throws IncQueryException {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.testing.queries.IncorrectSubstitution";
    
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
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Name = body.getOrCreateVariableByName("Name");
      PVariable var_CorrespondingSubstitution = body.getOrCreateVariableByName("CorrespondingSubstitution");
      PVariable var_Value1 = body.getOrCreateVariableByName("Value1");
      PVariable var_Value2 = body.getOrCreateVariableByName("Value2");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Record, "Record"), 
        new ExportedParameter(body, var_CorrespondingRecord, "CorrespondingRecord")
      ));
      
      
      new TypeBinary(body, CONTEXT, var_Record, var_Substitution, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchRecord", "substitutions"), "http://www.eclipse.org/incquery/snapshot/MatchRecord.substitutions");
      new TypeBinary(body, CONTEXT, var_Substitution, var_Name, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSubstitutionRecord", "parameterName"), "http://www.eclipse.org/incquery/snapshot/MatchSubstitutionRecord.parameterName");
      new TypeBinary(body, CONTEXT, var_CorrespondingRecord, var_CorrespondingSubstitution, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchRecord", "substitutions"), "http://www.eclipse.org/incquery/snapshot/MatchRecord.substitutions");
      new TypeBinary(body, CONTEXT, var_CorrespondingSubstitution, var_Name, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSubstitutionRecord", "parameterName"), "http://www.eclipse.org/incquery/snapshot/MatchSubstitutionRecord.parameterName");
      new TypeBinary(body, CONTEXT, var_Substitution, var_Value1, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSubstitutionRecord", "derivedValue"), "http://www.eclipse.org/incquery/snapshot/MatchSubstitutionRecord.derivedValue");
      new TypeBinary(body, CONTEXT, var_CorrespondingSubstitution, var_Value2, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSubstitutionRecord", "derivedValue"), "http://www.eclipse.org/incquery/snapshot/MatchSubstitutionRecord.derivedValue");
      new Inequality(body, var_Value1, var_Value2);
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static IncorrectSubstitutionQuerySpecification INSTANCE = make();
    
    public static IncorrectSubstitutionQuerySpecification make() {
      return new IncorrectSubstitutionQuerySpecification();					
      
    }
  }
  
}
