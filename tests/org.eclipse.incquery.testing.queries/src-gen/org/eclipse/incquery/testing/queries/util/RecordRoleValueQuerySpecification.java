package org.eclipse.incquery.testing.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.ConstantValue;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.testing.queries.RecordRoleValueMatcher;

/**
 * A pattern-specific query specification that can instantiate RecordRoleValueMatcher in a type-safe way.
 * 
 * @see RecordRoleValueMatcher
 * @see RecordRoleValueMatch
 * 
 */
@SuppressWarnings("all")
public final class RecordRoleValueQuerySpecification extends BaseGeneratedQuerySpecification<RecordRoleValueMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static RecordRoleValueQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected RecordRoleValueMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return RecordRoleValueMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.testing.queries.RecordRoleValue";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Record","Role");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Record", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord"),new PParameter("Role", "org.eclipse.incquery.snapshot.EIQSnapshot.RecordRole"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_Record = body.getOrCreateVariableByName("Record");
      PVariable var_Role = body.getOrCreateVariableByName("Role");
      PVariable var__MS = body.getOrCreateVariableByName("_MS");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Record, "Record"), 
        new ExportedParameter(body, var_Role, "Role")
      ));
      
      
      new TypeBinary(body, CONTEXT, var__MS, var_Record, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSetRecord", "filter"), "http://www.eclipse.org/incquery/snapshot/MatchSetRecord.filter");
      new ConstantValue(body, var_Role, org.eclipse.incquery.snapshot.EIQSnapshot.RecordRole.get("Filter"));
      bodies.add(body);
    }
    {
      PBody body = new PBody(this);
      PVariable var_Record = body.getOrCreateVariableByName("Record");
      PVariable var_Role = body.getOrCreateVariableByName("Role");
      PVariable var__MS = body.getOrCreateVariableByName("_MS");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Record, "Record"), 
        new ExportedParameter(body, var_Role, "Role")
      ));
      
      
      new TypeBinary(body, CONTEXT, var__MS, var_Record, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MatchSetRecord", "matches"), "http://www.eclipse.org/incquery/snapshot/MatchSetRecord.matches");
      new ConstantValue(body, var_Role, org.eclipse.incquery.snapshot.EIQSnapshot.RecordRole.get("Match"));
      bodies.add(body);
    }
    {
      PAnnotation annotation = new PAnnotation("QueryExplorer");
      annotation.addAttribute("display",false);
      addAnnotation(annotation);
    }
    {
      PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      annotation.addAttribute("feature","role");
      addAnnotation(annotation);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static RecordRoleValueQuerySpecification INSTANCE = make();
    
    public static RecordRoleValueQuerySpecification make() {
      return new RecordRoleValueQuerySpecification();					
      
    }
  }
  
}
