package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import operation.queries.ChecklistProcessCorrespondenceMatch;
import operation.queries.ChecklistProcessCorrespondenceMatcher;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * A pattern-specific query specification that can instantiate ChecklistProcessCorrespondenceMatcher in a type-safe way.
 * 
 * @see ChecklistProcessCorrespondenceMatcher
 * @see ChecklistProcessCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class ChecklistProcessCorrespondenceQuerySpecification extends BaseGeneratedEMFQuerySpecification<ChecklistProcessCorrespondenceMatcher> {
  private ChecklistProcessCorrespondenceQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ChecklistProcessCorrespondenceQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ChecklistProcessCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistProcessCorrespondenceMatcher.on(engine);
  }
  
  @Override
  public ChecklistProcessCorrespondenceMatch newEmptyMatch() {
    return ChecklistProcessCorrespondenceMatch.newEmptyMatch();
  }
  
  @Override
  public ChecklistProcessCorrespondenceMatch newMatch(final Object... parameters) {
    return ChecklistProcessCorrespondenceMatch.newMatch((operation.Checklist) parameters[0], (process.Process) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static ChecklistProcessCorrespondenceQuerySpecification INSTANCE = make();
    
    public static ChecklistProcessCorrespondenceQuerySpecification make() {
      return new ChecklistProcessCorrespondenceQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static ChecklistProcessCorrespondenceQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
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
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_Checklist = body.getOrCreateVariableByName("Checklist");
      		PVariable var_Process = body.getOrCreateVariableByName("Process");
      		PVariable var_ProcessId = body.getOrCreateVariableByName("ProcessId");
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		PVariable var__virtual_1_ = body.getOrCreateVariableByName(".virtual{1}");
      		body.setExportedParameters(Arrays.<ExportedParameter>asList(
      			new ExportedParameter(body, var_Checklist, "Checklist"),
      			
      			new ExportedParameter(body, var_Process, "Process")
      		));
      		new TypeConstraint(body, new FlatTuple(var_Checklist), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://operation/1.0", "Checklist")));
      		new TypeConstraint(body, new FlatTuple(var_Process), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://process/1.0", "Process")));
      		new TypeConstraint(body, new FlatTuple(var_Process), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://process/1.0", "Process")));
      		new TypeConstraint(body, new FlatTuple(var_Process, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://process/1.0", "ProcessElement", "id")));
      		new Equality(body, var__virtual_0_, var_ProcessId);
      		new TypeConstraint(body, new FlatTuple(var_Checklist), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://operation/1.0", "Checklist")));
      		new TypeConstraint(body, new FlatTuple(var_Checklist, var__virtual_1_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://operation/1.0", "Checklist", "processId")));
      		new Equality(body, var__virtual_1_, var_ProcessId);
      		bodies.add(body);
      	}
      	{
      		PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      		annotation.addAttribute("feature", "process");
      		addAnnotation(annotation);
      	}
      	// to silence compiler error
      	if (false) throw new IncQueryException("Never", "happens");
      } catch (IncQueryException ex) {
      	throw processDependencyException(ex);
      }
      return bodies;
    }
  }
}
