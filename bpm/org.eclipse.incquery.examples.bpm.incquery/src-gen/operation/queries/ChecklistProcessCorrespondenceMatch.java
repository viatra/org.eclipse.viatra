package operation.queries;

import java.util.Arrays;
import java.util.List;
import operation.Checklist;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Pattern-specific match representation of the operation.queries.ChecklistProcessCorrespondence pattern, 
 * to be used in conjunction with {@link ChecklistProcessCorrespondenceMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see ChecklistProcessCorrespondenceMatcher
 * @see ChecklistProcessCorrespondenceProcessor
 * 
 */
public abstract class ChecklistProcessCorrespondenceMatch extends BasePatternMatch {
  private Checklist fChecklist;
  
  private process.Process fProcess;
  
  private static List<String> parameterNames = makeImmutableList("Checklist", "Process");
  
  private ChecklistProcessCorrespondenceMatch(final Checklist pChecklist, final process.Process pProcess) {
    this.fChecklist = pChecklist;
    this.fProcess = pProcess;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Checklist".equals(parameterName)) return this.fChecklist;
    if ("Process".equals(parameterName)) return this.fProcess;
    return null;
    
  }
  
  public Checklist getChecklist() {
    return this.fChecklist;
    
  }
  
  public process.Process getProcess() {
    return this.fProcess;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Checklist".equals(parameterName) ) {
    	this.fChecklist = (operation.Checklist) newValue;
    	return true;
    }
    if ("Process".equals(parameterName) ) {
    	this.fProcess = (process.Process) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setChecklist(final Checklist pChecklist) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fChecklist = pChecklist;
    
  }
  
  public void setProcess(final process.Process pProcess) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fProcess = pProcess;
    
  }
  
  @Override
  public String patternName() {
    return "operation.queries.ChecklistProcessCorrespondence";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return ChecklistProcessCorrespondenceMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fChecklist, fProcess};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Checklist\"=" + prettyPrintValue(fChecklist) + ", ");
    result.append("\"Process\"=" + prettyPrintValue(fProcess));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fChecklist == null) ? 0 : fChecklist.hashCode()); 
    result = prime * result + ((fProcess == null) ? 0 : fProcess.hashCode()); 
    return result; 
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof ChecklistProcessCorrespondenceMatch)) { // this should be infrequent				
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!pattern().equals(otherSig.pattern()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    ChecklistProcessCorrespondenceMatch other = (ChecklistProcessCorrespondenceMatch) obj;
    if (fChecklist == null) {if (other.fChecklist != null) return false;}
    else if (!fChecklist.equals(other.fChecklist)) return false;
    if (fProcess == null) {if (other.fProcess != null) return false;}
    else if (!fProcess.equals(other.fProcess)) return false;
    return true;
  }
  
  @Override
  public Pattern pattern() {
    try {
    	return ChecklistProcessCorrespondenceMatcher.querySpecification().getPattern();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  static final class Mutable extends ChecklistProcessCorrespondenceMatch {
    Mutable(final Checklist pChecklist, final process.Process pProcess) {
      super(pChecklist, pProcess);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  static final class Immutable extends ChecklistProcessCorrespondenceMatch {
    Immutable(final Checklist pChecklist, final process.Process pProcess) {
      super(pChecklist, pProcess);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
