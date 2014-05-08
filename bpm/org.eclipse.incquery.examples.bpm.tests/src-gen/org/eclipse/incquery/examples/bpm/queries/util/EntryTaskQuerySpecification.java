package org.eclipse.incquery.examples.bpm.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.examples.bpm.queries.EntryTaskMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;

/**
 * A pattern-specific query specification that can instantiate EntryTaskMatcher in a type-safe way.
 * 
 * @see EntryTaskMatcher
 * @see EntryTaskMatch
 * 
 */
@SuppressWarnings("all")
public final class EntryTaskQuerySpecification extends BaseGeneratedQuerySpecification<EntryTaskMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static EntryTaskQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
    
  }
  
  @Override
  protected EntryTaskMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return EntryTaskMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.examples.bpm.queries.entryTask";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Entry","Task");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Entry", "operation.ChecklistEntry"),new PParameter("Task", "process.Task"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody> bodies = Sets.newLinkedHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_Entry = body.getOrCreateVariableByName("Entry");
      PVariable var_Task = body.getOrCreateVariableByName("Task");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Entry, "Entry"), 
        new ExportedParameter(body, var_Task, "Task")
      ));
      
      
      new TypeBinary(body, CONTEXT, var_Entry, var_Task, getFeatureLiteral("http://operation/1.0", "ChecklistEntry", "task"), "http://operation/1.0/ChecklistEntry.task");
      bodies.add(body);
    }
    return bodies;
  }
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static EntryTaskQuerySpecification INSTANCE = make();
    
    public static EntryTaskQuerySpecification make() {
      return new EntryTaskQuerySpecification();					
      
    }
  }
  
}
