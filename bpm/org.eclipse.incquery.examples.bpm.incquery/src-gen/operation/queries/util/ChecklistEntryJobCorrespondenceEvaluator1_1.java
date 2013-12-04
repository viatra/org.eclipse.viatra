package operation.queries.util;

import com.google.common.collect.ImmutableList;
import java.util.Map;
import org.eclipse.incquery.runtime.extensibility.IMatchChecker;
import org.eclipse.incquery.runtime.rete.construction.psystem.IValueProvider;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * A xbase xexpression evaluator tailored for the operation.queries.ChecklistEntryJobCorrespondence pattern.
 */
@SuppressWarnings("all")
public class ChecklistEntryJobCorrespondenceEvaluator1_1 implements IMatchChecker {
  private ImmutableList<String> parameterNames = ImmutableList.of(
    "JobName"	, 
    "JobPath"	, 
    "SysName"	
    );
    ;
  
  /**
   * The raw java code generated from the xbase xexpression by xtext.
   */
  private Boolean evaluateGeneratedExpression(final String JobName, final String JobPath, final String SysName) {
    String _concat = ((String) SysName).concat("/");
    String _concat_1 = _concat.concat(((String) JobName));
    boolean _equals = ((String) JobPath).equals(_concat_1);
    return Boolean.valueOf(_equals);
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  @Deprecated
  public Boolean evaluateXExpression(final Tuple tuple, final Map<String,Integer> tupleNameMap) {
    int JobNamePosition = tupleNameMap.get("JobName");
    java.lang.String JobName = (java.lang.String) tuple.get(JobNamePosition);
    int JobPathPosition = tupleNameMap.get("JobPath");
    java.lang.String JobPath = (java.lang.String) tuple.get(JobPathPosition);
    int SysNamePosition = tupleNameMap.get("SysName");
    java.lang.String SysName = (java.lang.String) tuple.get(SysNamePosition);
    return evaluateGeneratedExpression(JobName, JobPath, SysName);
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  public Boolean evaluateExpression(final IValueProvider provider) {
    java.lang.String JobName = (java.lang.String) provider.getValue("JobName");
    java.lang.String JobPath = (java.lang.String) provider.getValue("JobPath");
    java.lang.String SysName = (java.lang.String) provider.getValue("SysName");
    return evaluateGeneratedExpression(JobName, JobPath, SysName);
    
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  public Iterable<String> getInputParameterNames() {
    return parameterNames;
    
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  public String getShortDescription() {
    return "XExpression 1_1 from Pattern ChecklistEntryJobCorrespondence";
    
  }
}
