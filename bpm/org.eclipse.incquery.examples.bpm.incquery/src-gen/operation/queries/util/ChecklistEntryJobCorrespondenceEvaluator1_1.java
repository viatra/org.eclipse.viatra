package operation.queries.util;

import java.util.Map;
import org.eclipse.incquery.runtime.extensibility.IMatchChecker;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * A xbase xexpression evaluator tailored for the operation.queries.ChecklistEntryJobCorrespondence pattern.
 */
public class ChecklistEntryJobCorrespondenceEvaluator1_1 implements IMatchChecker {
  /**
   * The raw java code generated from the xbase xexpression by xtext.
   */
  private Boolean evaluateXExpressionGenerated(final String SysName, final String JobName, final String JobPath) {
    String _JobPath = JobPath;
    String _SysName = SysName;
    String _concat = ((String) _SysName).concat("/");
    String _JobName = JobName;
    String _concat_1 = _concat.concat(((String) _JobName));
    boolean _equals = ((String) _JobPath).equals(_concat_1);
    return Boolean.valueOf(_equals);
  }
  
  /**
   * A wrapper method for calling the generated java method with the correct attributes.
   */
  @Override
  public Boolean evaluateXExpression(final Tuple tuple, final Map<String,Integer> tupleNameMap) {
    int SysNamePosition = tupleNameMap.get("SysName");
    java.lang.String SysName = (java.lang.String) tuple.get(SysNamePosition);
    int JobNamePosition = tupleNameMap.get("JobName");
    java.lang.String JobName = (java.lang.String) tuple.get(JobNamePosition);
    int JobPathPosition = tupleNameMap.get("JobPath");
    java.lang.String JobPath = (java.lang.String) tuple.get(JobPathPosition);
    return evaluateXExpressionGenerated(SysName, JobName, JobPath);
  }
}
