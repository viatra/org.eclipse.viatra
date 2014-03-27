package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import operation.queries.ChecklistProcessCorrespondenceMatcher;
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

/**
 * A pattern-specific query specification that can instantiate ChecklistProcessCorrespondenceMatcher in a type-safe way.
 * 
 * @see ChecklistProcessCorrespondenceMatcher
 * @see ChecklistProcessCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class ChecklistProcessCorrespondenceQuerySpecification extends BaseGeneratedQuerySpecification<ChecklistProcessCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ChecklistProcessCorrespondenceQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected ChecklistProcessCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistProcessCorrespondenceMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "operation.queries.ChecklistProcessCorrespondence";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Checklist","Process");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Checklist", "operation.Checklist"),new PParameter("Process", "process.Process"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_Checklist = body.getOrCreateVariableByName("Checklist");
      PVariable var_Process = body.getOrCreateVariableByName("Process");
      PVariable var_ProcessId = body.getOrCreateVariableByName("ProcessId");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Checklist, "Checklist"), 
        new ExportedParameter(body, var_Process, "Process")
      ));
      
      
      new TypeUnary(body, var_Process, getClassifierLiteral("http://process/1.0", "Process"), "http://process/1.0/Process");
      new TypeBinary(body, context, var_Process, var_ProcessId, getFeatureLiteral("http://process/1.0", "ProcessElement", "id"), "http://process/1.0/ProcessElement.id");
      new TypeBinary(body, context, var_Checklist, var_ProcessId, getFeatureLiteral("http://operation/1.0", "Checklist", "processId"), "http://operation/1.0/Checklist.processId");
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      annotation.addAttribute("feature","process");
      addAnnotation(annotation);
    }
    setStatus(PQuery.PQueryStatus.OK);
    return bodies;
  }
  
  private ChecklistProcessCorrespondenceQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQuery.PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<ChecklistProcessCorrespondenceQuerySpecification> {
    @Override
    public ChecklistProcessCorrespondenceQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static ChecklistProcessCorrespondenceQuerySpecification INSTANCE = make();
    
    public static ChecklistProcessCorrespondenceQuerySpecification make() {
      try {
      	return new ChecklistProcessCorrespondenceQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
