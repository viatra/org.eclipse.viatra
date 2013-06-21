package operation.queries.util;

import java.util.Map;
import org.eclipse.incquery.runtime.extensibility.IMatchChecker;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * A xbase xexpression evaluator tailored for the operation.queries.ChecklistEntryJobCorrespondence pattern.
 */
@SuppressWarnings("all")
public class ChecklistEntryJobCorrespondenceEvaluator1_1 implements IMatchChecker {
  /**
   * The raw java code generated from the xbase xexpression by xtext.
   */
  private Boolean evaluateXExpressionGenerated(final String JobName, final String JobPath, final String SysName) {
    String _concat = ((String) SysName).concat("/");
    String _concat_1 = _concat.concat(((String) JobName));
    boolean _equals = ((String) JobPath).equals(_concat_1);
    return Boolean.valueOf(_equals);
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  public Boolean evaluateXExpression(final Tuple tuple, final Map<String,Integer> tupleNameMap) {
    int JobNamePosition = tupleNameMap.get("JobName");
    java.lang.String JobName = (java.lang.String) tuple.get(JobNamePosition);
    int JobPathPosition = tupleNameMap.get("JobPath");
    java.lang.String JobPath = (java.lang.String) tuple.get(JobPathPosition);
    int SysNamePosition = tupleNameMap.get("SysName");
    java.lang.String SysName = (java.lang.String) tuple.get(SysNamePosition);
    return evaluateXExpressionGenerated(JobName, JobPath, SysName);
  }
}
