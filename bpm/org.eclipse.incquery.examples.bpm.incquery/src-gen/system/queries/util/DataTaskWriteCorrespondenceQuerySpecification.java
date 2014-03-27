package system.queries.util;

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
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeUnary;
import system.queries.DataTaskWriteCorrespondenceMatcher;

/**
 * A pattern-specific query specification that can instantiate DataTaskWriteCorrespondenceMatcher in a type-safe way.
 * 
 * @see DataTaskWriteCorrespondenceMatcher
 * @see DataTaskWriteCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class DataTaskWriteCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<DataTaskWriteCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static DataTaskWriteCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected DataTaskWriteCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return DataTaskWriteCorrespondenceMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "system.queries.DataTaskWriteCorrespondence";
    
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
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
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
      new TypeBinary(body, context, var_Data, var_TaskId, getFeatureLiteral("http://system/1.0", "Data", "writingTaskIds"), "http://system/1.0/Data.writingTaskIds");
      new TypeBinary(body, context, var_Task, var_TaskId, getFeatureLiteral("http://process/1.0", "ProcessElement", "id"), "http://process/1.0/ProcessElement.id");
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      annotation.addAttribute("feature","writingTask");
      addAnnotation(annotation);
    }
    setStatus(PQuery.PQueryStatus.OK);
    return bodies;
  }
  
  private DataTaskWriteCorrespondenceQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQuery.PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<DataTaskWriteCorrespondenceQuerySpecification> {
    @Override
    public DataTaskWriteCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static DataTaskWriteCorrespondenceQuerySpecification INSTANCE = make();
    
    public static DataTaskWriteCorrespondenceQuerySpecification make() {
      try {
      	return new DataTaskWriteCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
