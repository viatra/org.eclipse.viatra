package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import operation.queries.ChecklistEntryJobCorrespondenceMatch;
import operation.queries.ChecklistEntryJobCorrespondenceMatcher;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFPQuery;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.emf.types.EClassTransitiveInstancesKey;
import org.eclipse.incquery.runtime.emf.types.EStructuralFeatureInstancesKey;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.psystem.IValueProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.Equality;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeConstraint;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.queries.QueryInitializationException;
import org.eclipse.incquery.runtime.matchers.tuple.FlatTuple;

/**
 * A pattern-specific query specification that can instantiate ChecklistEntryJobCorrespondenceMatcher in a type-safe way.
 * 
 * @see ChecklistEntryJobCorrespondenceMatcher
 * @see ChecklistEntryJobCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class ChecklistEntryJobCorrespondenceQuerySpecification extends BaseGeneratedEMFQuerySpecification<ChecklistEntryJobCorrespondenceMatcher> {
  private ChecklistEntryJobCorrespondenceQuerySpecification() {
    super(GeneratedPQuery.INSTANCE);
  }
  
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ChecklistEntryJobCorrespondenceQuerySpecification instance() throws IncQueryException {
    try{
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	throw processInitializerError(err);
    }
  }
  
  @Override
  protected ChecklistEntryJobCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistEntryJobCorrespondenceMatcher.on(engine);
  }
  
  @Override
  public ChecklistEntryJobCorrespondenceMatch newEmptyMatch() {
    return ChecklistEntryJobCorrespondenceMatch.newEmptyMatch();
  }
  
  @Override
  public ChecklistEntryJobCorrespondenceMatch newMatch(final Object... parameters) {
    return ChecklistEntryJobCorrespondenceMatch.newMatch((operation.ChecklistEntry) parameters[0], (system.Job) parameters[1]);
  }
  
  private static class LazyHolder {
    private final static ChecklistEntryJobCorrespondenceQuerySpecification INSTANCE = make();
    
    public static ChecklistEntryJobCorrespondenceQuerySpecification make() {
      return new ChecklistEntryJobCorrespondenceQuerySpecification();					
    }
  }
  
  private static class GeneratedPQuery extends BaseGeneratedEMFPQuery {
    private final static ChecklistEntryJobCorrespondenceQuerySpecification.GeneratedPQuery INSTANCE = new GeneratedPQuery();
    
    @Override
    public String getFullyQualifiedName() {
      return "operation.queries.ChecklistEntryJobCorrespondence";
    }
    
    @Override
    public List<String> getParameterNames() {
      return Arrays.asList("CLE","Job");
    }
    
    @Override
    public List<PParameter> getParameters() {
      return Arrays.asList(new PParameter("CLE", "operation.ChecklistEntry"),new PParameter("Job", "system.Job"));
    }
    
    @Override
    public Set<PBody> doGetContainedBodies() throws QueryInitializationException {
      Set<PBody> bodies = Sets.newLinkedHashSet();
      try {
      	{
      		PBody body = new PBody(this);
      		PVariable var_CLE = body.getOrCreateVariableByName("CLE");
      		PVariable var_Job = body.getOrCreateVariableByName("Job");
      		PVariable var_JobName = body.getOrCreateVariableByName("JobName");
      		PVariable var__virtual_0_ = body.getOrCreateVariableByName(".virtual{0}");
      		PVariable var_System = body.getOrCreateVariableByName("System");
      		PVariable var_SysName = body.getOrCreateVariableByName("SysName");
      		PVariable var__virtual_1_ = body.getOrCreateVariableByName(".virtual{1}");
      		PVariable var__virtual_2_ = body.getOrCreateVariableByName(".virtual{2}");
      		PVariable var_JobPath = body.getOrCreateVariableByName("JobPath");
      		PVariable var__virtual_3_ = body.getOrCreateVariableByName(".virtual{3}");
      		body.setExportedParameters(Arrays.<ExportedParameter>asList(
      			new ExportedParameter(body, var_CLE, "CLE"),
      			
      			new ExportedParameter(body, var_Job, "Job")
      		));
      		new TypeConstraint(body, new FlatTuple(var_CLE), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://operation/1.0", "ChecklistEntry")));
      		new TypeConstraint(body, new FlatTuple(var_Job), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://system/1.0", "Job")));
      		new TypeConstraint(body, new FlatTuple(var_Job), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://system/1.0", "Job")));
      		new TypeConstraint(body, new FlatTuple(var_Job, var__virtual_0_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://system/1.0", "ResourceElement", "name")));
      		new Equality(body, var__virtual_0_, var_JobName);
      		new TypeConstraint(body, new FlatTuple(var_System), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://system/1.0", "System")));
      		new TypeConstraint(body, new FlatTuple(var_System, var__virtual_1_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://system/1.0", "ResourceElement", "name")));
      		new Equality(body, var__virtual_1_, var_SysName);
      		new TypeConstraint(body, new FlatTuple(var_Job), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://system/1.0", "Job")));
      		new TypeConstraint(body, new FlatTuple(var_Job, var__virtual_2_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://system/1.0", "Job", "runsOn")));
      		new Equality(body, var__virtual_2_, var_System);
      		new TypeConstraint(body, new FlatTuple(var_CLE), new EClassTransitiveInstancesKey((EClass)getClassifierLiteral("http://operation/1.0", "ChecklistEntry")));
      		new TypeConstraint(body, new FlatTuple(var_CLE, var__virtual_3_), new EStructuralFeatureInstancesKey(getFeatureLiteral("http://operation/1.0", "ChecklistEntry", "jobPaths")));
      		new Equality(body, var__virtual_3_, var_JobPath);
      		new ExpressionEvaluation(body, new IExpressionEvaluator() {
      			
      			@Override
      			public String getShortDescription() {
      				return "Expression evaluation from pattern ChecklistEntryJobCorrespondence";
      			}
      		
      			@Override
      			public Iterable<String> getInputParameterNames() {
      				return Arrays.asList("JobName", "JobPath", "SysName");
      			}
      		
      			@Override
      			public Object evaluateExpression(IValueProvider provider) throws Exception {
      					java.lang.String JobName = (java.lang.String) provider.getValue("JobName");
      					java.lang.String JobPath = (java.lang.String) provider.getValue("JobPath");
      					java.lang.String SysName = (java.lang.String) provider.getValue("SysName");
      					return evaluateExpression_1_1(JobName, JobPath, SysName);
      				}
      		
      		},  null); 
      		bodies.add(body);
      	}
      	{
      		PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      		annotation.addAttribute("feature", "jobs");
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
  
  private static boolean evaluateExpression_1_1(final String JobName, final String JobPath, final String SysName) {
    String _concat = ((String) SysName).concat("/");
    String _concat_1 = _concat.concat(((String) JobName));
    boolean _equals = ((String) JobPath).equals(_concat_1);
    return _equals;
  }
}
