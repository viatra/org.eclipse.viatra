package operation.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import operation.queries.ChecklistEntryJobCorrespondenceMatch;
import operation.queries.ChecklistEntryJobCorrespondenceMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedEMFQuerySpecification;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.matchers.psystem.IExpressionEvaluator;
import org.eclipse.incquery.runtime.matchers.psystem.IValueProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExpressionEvaluation;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.runtime.matchers.psystem.queries.PParameter;

/**
 * A pattern-specific query specification that can instantiate ChecklistEntryJobCorrespondenceMatcher in a type-safe way.
 * 
 * @see ChecklistEntryJobCorrespondenceMatcher
 * @see ChecklistEntryJobCorrespondenceMatch
 * 
 */
@SuppressWarnings("all")
public final class ChecklistEntryJobCorrespondenceQuerySpecification extends BaseGeneratedEMFQuerySpecification<ChecklistEntryJobCorrespondenceMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static ChecklistEntryJobCorrespondenceQuerySpecification instance() throws IncQueryException {
    return LazyHolder.INSTANCE;
  }
  
  @Override
  protected ChecklistEntryJobCorrespondenceMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistEntryJobCorrespondenceMatcher.on(engine);
  }
  
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
    return Arrays.asList(new PParameter("CLE", "org.eclipse.emf.ecore.EObject"),new PParameter("Job", "org.eclipse.emf.ecore.EObject"));
  }
  
  @Override
  public ChecklistEntryJobCorrespondenceMatch newEmptyMatch() {
    return ChecklistEntryJobCorrespondenceMatch.newEmptyMatch();
  }
  
  @Override
  public ChecklistEntryJobCorrespondenceMatch newMatch(final Object... parameters) {
    return ChecklistEntryJobCorrespondenceMatch.newMatch((org.eclipse.emf.ecore.EObject) parameters[0], (org.eclipse.emf.ecore.EObject) parameters[1]);
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    Set<PBody>  bodies = Sets.newLinkedHashSet();
    
    {
    	PBody body = new PBody(this);
    	PVariable var_CLE = body.getOrCreateVariableByName("CLE");
    	PVariable var_Job = body.getOrCreateVariableByName("Job");
    	PVariable var_JobName = body.getOrCreateVariableByName("JobName");
    	PVariable var_System = body.getOrCreateVariableByName("System");
    	PVariable var_SysName = body.getOrCreateVariableByName("SysName");
    	PVariable var_JobPath = body.getOrCreateVariableByName("JobPath");
    	body.setExportedParameters(Arrays.<ExportedParameter>asList(
    		new ExportedParameter(body, var_CLE, "CLE"),
    		
    		new ExportedParameter(body, var_Job, "Job")
    	));
    new TypeBinary(body, CONTEXT, var_Job, var_JobName, getFeatureLiteral("http://system/1.0", "ResourceElement", "name"), "http://system/1.0/ResourceElement.name");
    new TypeBinary(body, CONTEXT, var_System, var_SysName, getFeatureLiteral("http://system/1.0", "ResourceElement", "name"), "http://system/1.0/ResourceElement.name");
    new TypeBinary(body, CONTEXT, var_Job, var_System, getFeatureLiteral("http://system/1.0", "Job", "runsOn"), "http://system/1.0/Job.runsOn");
    new TypeBinary(body, CONTEXT, var_CLE, var_JobPath, getFeatureLiteral("http://operation/1.0", "ChecklistEntry", "jobPaths"), "http://operation/1.0/ChecklistEntry.jobPaths");
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
    return bodies;
  }
  
  private static class LazyHolder {
    private final static ChecklistEntryJobCorrespondenceQuerySpecification INSTANCE = make();
    
    public static ChecklistEntryJobCorrespondenceQuerySpecification make() {
      return new ChecklistEntryJobCorrespondenceQuerySpecification();					
    }
  }
  
  private boolean evaluateExpression_1_1(final String JobName, final String JobPath, final String SysName) {
    String _concat = ((String) SysName).concat("/");
    String _concat_1 = _concat.concat(((String) JobName));
    boolean _equals = ((String) JobPath).equals(_concat_1);
    return _equals;
  }
}
