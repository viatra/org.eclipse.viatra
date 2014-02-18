package operation.queries;

import operation.queries.ChecklistEntryJobCorrespondenceMatcher;
import operation.queries.ChecklistEntryTaskCorrespondenceMatcher;
import operation.queries.ChecklistProcessCorrespondenceMatcher;
import operation.queries.DataReadByChecklistEntryMatcher;
import operation.queries.IncorrectEntryInChecklistMatcher;
import operation.queries.TaskChecklistEntryJobCorrespondenceMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;

@SuppressWarnings("all")
public final class DerivedFeaturesMatchers {
  private IncQueryEngine engine;
  
  public DerivedFeaturesMatchers(final IncQueryEngine engine) {
    this.engine = engine;
    
  }
  
  public ChecklistEntryJobCorrespondenceMatcher getChecklistEntryJobCorrespondenceMatcher() throws IncQueryException {
    return ChecklistEntryJobCorrespondenceMatcher.on(engine);
  }
  
  public ChecklistEntryTaskCorrespondenceMatcher getChecklistEntryTaskCorrespondenceMatcher() throws IncQueryException {
    return ChecklistEntryTaskCorrespondenceMatcher.on(engine);
  }
  
  public ChecklistProcessCorrespondenceMatcher getChecklistProcessCorrespondenceMatcher() throws IncQueryException {
    return ChecklistProcessCorrespondenceMatcher.on(engine);
  }
  
  public DataReadByChecklistEntryMatcher getDataReadByChecklistEntryMatcher() throws IncQueryException {
    return DataReadByChecklistEntryMatcher.on(engine);
  }
  
  public TaskChecklistEntryJobCorrespondenceMatcher getTaskChecklistEntryJobCorrespondenceMatcher() throws IncQueryException {
    return TaskChecklistEntryJobCorrespondenceMatcher.on(engine);
  }
  
  public IncorrectEntryInChecklistMatcher getIncorrectEntryInChecklistMatcher() throws IncQueryException {
    return IncorrectEntryInChecklistMatcher.on(engine);
  }
}
