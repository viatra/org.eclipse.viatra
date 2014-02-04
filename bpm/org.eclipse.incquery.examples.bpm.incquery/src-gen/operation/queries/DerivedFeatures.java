package operation.queries;

import operation.queries.ChecklistEntryJobCorrespondenceMatcher;
import operation.queries.ChecklistEntryTaskCorrespondenceMatcher;
import operation.queries.ChecklistProcessCorrespondenceMatcher;
import operation.queries.DataReadByChecklistEntryMatcher;
import operation.queries.IncorrectEntryInChecklistMatcher;
import operation.queries.TaskChecklistEntryJobCorrespondenceMatcher;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * A pattern group formed of all patterns defined in derivedFeatures.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file derivedFeatures.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package operation.queries, the group contains the definition of the following patterns: <ul>
 * <li>ChecklistEntryJobCorrespondence</li>
 * <li>ChecklistEntryTaskCorrespondence</li>
 * <li>ChecklistProcessCorrespondence</li>
 * <li>ChecklistEntryTaskInCheckListProcess</li>
 * <li>TaskInProcess</li>
 * <li>IncorrectEntryInChecklist</li>
 * <li>TaskChecklistEntryJobCorrespondence</li>
 * <li>DataReadByChecklistEntry</li>
 * </ul>
 * 
 * @see IPatternGroup
 * 
 */
@SuppressWarnings("all")
public final class DerivedFeatures extends BaseGeneratedPatternGroup {
  /**
   * Access the pattern group.
   * 
   * @return the singleton instance of the group
   * @throws IncQueryException if there was an error loading the generated code of pattern specifications
   * 
   */
  public static DerivedFeatures instance() throws IncQueryException {
    if (INSTANCE == null) {
    	INSTANCE = new DerivedFeatures();
    }
    return INSTANCE;
    
  }
  
  private static DerivedFeatures INSTANCE;
  
  private DerivedFeatures() throws IncQueryException {
    querySpecifications.add(DataReadByChecklistEntryMatcher.querySpecification());
    querySpecifications.add(ChecklistEntryJobCorrespondenceMatcher.querySpecification());
    querySpecifications.add(TaskChecklistEntryJobCorrespondenceMatcher.querySpecification());
    querySpecifications.add(ChecklistEntryTaskCorrespondenceMatcher.querySpecification());
    querySpecifications.add(IncorrectEntryInChecklistMatcher.querySpecification());
    querySpecifications.add(ChecklistProcessCorrespondenceMatcher.querySpecification());
    
  }
}
