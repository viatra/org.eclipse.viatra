package system.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import process.Task;

/**
 * Pattern-specific match representation of the system.queries.UndefinedServiceTasks pattern, 
 * to be used in conjunction with {@link UndefinedServiceTasksMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see UndefinedServiceTasksMatcher
 * @see UndefinedServiceTasksProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class UndefinedServiceTasksMatch extends BasePatternMatch {
  private Task fTask;
  
  private static List<String> parameterNames = makeImmutableList("Task");
  
  private UndefinedServiceTasksMatch(final Task pTask) {
    this.fTask = pTask;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Task".equals(parameterName)) return this.fTask;
    return null;
    
  }
  
  public Task getTask() {
    return this.fTask;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Task".equals(parameterName) ) {
    	this.fTask = (process.Task) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setTask(final Task pTask) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fTask = pTask;
    
  }
  
  @Override
  public String patternName() {
    return "system.queries.UndefinedServiceTasks";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return UndefinedServiceTasksMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fTask};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Task\"=" + prettyPrintValue(fTask));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fTask == null) ? 0 : fTask.hashCode()); 
    return result; 
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof UndefinedServiceTasksMatch)) { // this should be infrequent				
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!pattern().equals(otherSig.pattern()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    UndefinedServiceTasksMatch other = (UndefinedServiceTasksMatch) obj;
    if (fTask == null) {if (other.fTask != null) return false;}
    else if (!fTask.equals(other.fTask)) return false;
    return true;
  }
  
  @Override
  public Pattern pattern() {
    try {
    	return UndefinedServiceTasksMatcher.querySpecification().getPattern();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends UndefinedServiceTasksMatch {
    Mutable(final Task pTask) {
      super(pTask);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends UndefinedServiceTasksMatch {
    Immutable(final Task pTask) {
      super(pTask);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
