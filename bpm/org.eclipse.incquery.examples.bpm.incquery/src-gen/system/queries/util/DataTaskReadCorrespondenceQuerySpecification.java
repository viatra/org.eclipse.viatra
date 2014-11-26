package system.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import system.queries.DataTaskReadCorrespondenceMatch;
import system.queries.DataTaskReadCorrespondenceMatcher;

/**
 * A pattern-specific query specification that can instantiate DataTaskReadCorrespondenceMatcher in a type-safe way.
 * 
 * @see DataTaskReadCorrespondenceMatcher
 * @see DataTaskReadCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class DataTaskReadCorrespondenceQuerySpecification extends BaseGeneratedEMFQuerySpecification<DataTaskReadCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static DataTaskReadCorrespondenceQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
  }
  
  @Override
  protected DataTaskReadCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return DataTaskReadCorrespondenceMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "system.queries.DataTaskReadCorrespondence";
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Data","Task");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Data", "system.Data"),new PParameter("Task", "process.Task"));
  }
  
  @Override
  public DataTaskReadCorrespondenceMatch newEmptyMatch() {
    return DataTaskReadCorrespondenceMatch.newEmptyMatch();
  }
  
  @Override
  public DataTaskReadCorrespondenceMatch newMatch(final Object... parameters) {
    return DataTaskReadCorrespondenceMatch.newMatch((system.Data) parameters[0], (process.Task) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody>  bodies = Sets.newLinkedHashSet();
    
    {
    	PBody body = new PBody(this);
    	PVariable var_Data = body.getOrCreateVariableByName("Data");
    	PVariable var_Task = body.getOrCreateVariableByName("Task");
    	PVariable var_TaskId = body.getOrCreateVariableByName("TaskId");
    	body.setExportedParameters(Arrays.<ExportedParameter>asList(
    		new ExportedParameter(body, var_Data, "Data"),
    		
    		new ExportedParameter(body, var_Task, "Task")
    	));
    new TypeUnary(body, var_Task, getClassifierLiteral("http://process/1.0", "Task"), "http://process/1.0/Task");
    new TypeBinary(body, CONTEXT, var_Data, var_TaskId, getFeatureLiteral("http://system/1.0", "Data", "readingTaskIds"), "http://system/1.0/Data.readingTaskIds");
    new TypeBinary(body, CONTEXT, var_Task, var_TaskId, getFeatureLiteral("http://process/1.0", "ProcessElement", "id"), "http://process/1.0/ProcessElement.id");
    	bodies.add(body);
    }
    {
    	PAnnotation annotation = new PAnnotation("QueryBasedFeature");
    	annotation.addAttribute("feature", "readingTask");
    	addAnnotation(annotation);
    }
    return bodies;
  }
  
  private static class LazyHolder {
    private final static DataTaskReadCorrespondenceQuerySpecification INSTANCE = make();
    
    public static DataTaskReadCorrespondenceQuerySpecification make() {
      return new DataTaskReadCorrespondenceQuerySpecification();					
    }
  }
}
