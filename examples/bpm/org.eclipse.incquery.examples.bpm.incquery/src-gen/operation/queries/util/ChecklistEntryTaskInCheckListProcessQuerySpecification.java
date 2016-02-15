package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import operation.queries.util.ChecklistEntryTaskCorrespondenceQuerySpecification;
import operation.queries.util.ChecklistProcessCorrespondenceQuerySpecification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.PositivePatternCall;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * A pattern-specific query specification that can instantiate ChecklistEntryTaskInCheckListProcessMatcher in a type-safe way.
 * 
 * @see ChecklistEntryTaskInCheckListProcessMatcher
 * @see ChecklistEntryTaskInCheckListProcessMatch
 * 
 */
@SuppressWarnings("all")
final class ChecklistEntryTaskInCheckListProcessQuerySpecification extends BaseGeneratedEMFQuerySpecification<IncQueryMatcher<IPatternMatch>> {
  private ChecklistEntryTaskInCheckListProcessQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ChecklistEntryTaskInCheckListProcessQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected IncQueryMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public IPatternMatch newEmptyMatch() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public IPatternMatch newMatch(final Object... parameters) {
    throw new UnsupportedOperationException();
  }
  
  private static class LazyHolder {
    private final static ChecklistEntryTaskInCheckListProcessQuerySpecification INSTANCE = make();
    
    public static ChecklistEntryTaskInCheckListProcessQuerySpecification make() {
      return new ChecklistEntryTaskInCheckListProcessQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static ChecklistEntryTaskInCheckListProcessQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "operation.queries.ChecklistEntryTaskInCheckListProcess";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("CLE","Task");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("CLE", "operation.ChecklistEntry"),new PParameter("Task", "process.Task"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_CLE = body.getOrCreateVariableByName("CLE");
      		PVariable var_Task = body.getOrCreateVariableByName("Task");
      		PVariable var_Checklist = body.getOrCreateVariableByName("Checklist");
      		PVariable var_Process = body.getOrCreateVariableByName("Process");
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		PVariable var__virtual_1_ = body.getOrCreateVariableByName(".virtual{1}");
      		body.setExportedParameters(Arrays.<ExportedParameter>asList(
      			new ExportedParameter(body, var_CLE, "CLE"),
      			
      			new ExportedParameter(body, var_Task, "Task")
      		));
      		new PositivePatternCall(body, new FlatTuple(var_CLE, var_Task), ChecklistEntryTaskCorrespondenceQuerySpecification.instance().getInternalQueryRepresentation());
      		new PositivePatternCall(body, new FlatTuple(var_Checklist, var_Process), ChecklistProcessCorrespondenceQuerySpecification.instance().getInternalQueryRepresentation());
      		new TypeConstraint(body, new FlatTuple(var_Process), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://process/1.0", "Process")));
      		new TypeConstraint(body, new FlatTuple(var_Process, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://process/1.0", "Process", "contents")));
      		new Equality(body, var__virtual_0_, var_Task);
      		new TypeConstraint(body, new FlatTuple(var_Checklist), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://operation/1.0", "Checklist")));
      		new TypeConstraint(body, new FlatTuple(var_Checklist, var__virtual_1_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://operation/1.0", "Checklist", "entries")));
      		new Equality(body, var__virtual_1_, var_CLE);
      		bodies.add(body);
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
