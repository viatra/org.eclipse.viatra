package operation.queries;

import operation.queries.ChecklistEntryJobCorrespondenceMatcher;
import operation.queries.ChecklistEntryTaskCorrespondenceMatcher;
import operation.queries.ChecklistProcessCorrespondenceMatcher;
import operation.queries.DataReadByChecklistEntryMatcher;
import operation.queries.IncorrectEntryInChecklistMatcher;
import operation.queries.TaskChecklistEntryJobCorrespondenceMatcher;
import operation.queries.util.ChecklistEntryJobCorrespondenceQuerySpecification;
import operation.queries.util.ChecklistEntryTaskCorrespondenceQuerySpecification;
import operation.queries.util.ChecklistProcessCorrespondenceQuerySpecification;
import operation.queries.util.DataReadByChecklistEntryQuerySpecification;
import operation.queries.util.IncorrectEntryInChecklistQuerySpecification;
import operation.queries.util.TaskChecklistEntryJobCorrespondenceQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
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
    querySpecifications.add(ChecklistEntryJobCorrespondenceQuerySpecification.instance());
    querySpecifications.add(ChecklistEntryTaskCorrespondenceQuerySpecification.instance());
    querySpecifications.add(ChecklistProcessCorrespondenceQuerySpecification.instance());
    querySpecifications.add(IncorrectEntryInChecklistQuerySpecification.instance());
    querySpecifications.add(TaskChecklistEntryJobCorrespondenceQuerySpecification.instance());
    querySpecifications.add(DataReadByChecklistEntryQuerySpecification.instance());
    
  }
  
  public ChecklistEntryJobCorrespondenceQuerySpecification getChecklistEntryJobCorrespondence() throws IncQueryException {
    return ChecklistEntryJobCorrespondenceQuerySpecification.instance();
  }
  
  public ChecklistEntryJobCorrespondenceMatcher getChecklistEntryJobCorrespondence(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistEntryJobCorrespondenceMatcher.on(engine);
  }
  
  public ChecklistEntryTaskCorrespondenceQuerySpecification getChecklistEntryTaskCorrespondence() throws IncQueryException {
    return ChecklistEntryTaskCorrespondenceQuerySpecification.instance();
  }
  
  public ChecklistEntryTaskCorrespondenceMatcher getChecklistEntryTaskCorrespondence(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistEntryTaskCorrespondenceMatcher.on(engine);
  }
  
  public ChecklistProcessCorrespondenceQuerySpecification getChecklistProcessCorrespondence() throws IncQueryException {
    return ChecklistProcessCorrespondenceQuerySpecification.instance();
  }
  
  public ChecklistProcessCorrespondenceMatcher getChecklistProcessCorrespondence(final IncQueryEngine engine) throws IncQueryException {
    return ChecklistProcessCorrespondenceMatcher.on(engine);
  }
  
  public IncorrectEntryInChecklistQuerySpecification getIncorrectEntryInChecklist() throws IncQueryException {
    return IncorrectEntryInChecklistQuerySpecification.instance();
  }
  
  public IncorrectEntryInChecklistMatcher getIncorrectEntryInChecklist(final IncQueryEngine engine) throws IncQueryException {
    return IncorrectEntryInChecklistMatcher.on(engine);
  }
  
  public TaskChecklistEntryJobCorrespondenceQuerySpecification getTaskChecklistEntryJobCorrespondence() throws IncQueryException {
    return TaskChecklistEntryJobCorrespondenceQuerySpecification.instance();
  }
  
  public TaskChecklistEntryJobCorrespondenceMatcher getTaskChecklistEntryJobCorrespondence(final IncQueryEngine engine) throws IncQueryException {
    return TaskChecklistEntryJobCorrespondenceMatcher.on(engine);
  }
  
  public DataReadByChecklistEntryQuerySpecification getDataReadByChecklistEntry() throws IncQueryException {
    return DataReadByChecklistEntryQuerySpecification.instance();
  }
  
  public DataReadByChecklistEntryMatcher getDataReadByChecklistEntry(final IncQueryEngine engine) throws IncQueryException {
    return DataReadByChecklistEntryMatcher.on(engine);
  }
}
