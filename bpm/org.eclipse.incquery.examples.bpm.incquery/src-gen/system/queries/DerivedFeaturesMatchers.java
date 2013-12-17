package system.queries;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import system.queries.DataTaskReadCorrespondenceMatcher;
import system.queries.DataTaskWriteCorrespondenceMatcher;
import system.queries.JobInfoCorrespondenceMatcher;
import system.queries.JobTaskCorrespondenceMatcher;
import system.queries.TasksAffectedThroughDataMatcher;
import system.queries.TransitiveAffectedTasksThroughDataMatcher;
import system.queries.UndefinedServiceTasksMatcher;

@SuppressWarnings("all")
public final class DerivedFeaturesMatchers {
  private IncQueryEngine engine;
  
  public DerivedFeaturesMatchers(final IncQueryEngine engine) {
    this.engine = engine;
    
  }
  
  public DataTaskWriteCorrespondenceMatcher getDataTaskWriteCorrespondenceMatcher() throws IncQueryException {
    return DataTaskWriteCorrespondenceMatcher.on(engine);
  }
  
  public JobTaskCorrespondenceMatcher getJobTaskCorrespondenceMatcher() throws IncQueryException {
    return JobTaskCorrespondenceMatcher.on(engine);
  }
  
  public DataTaskReadCorrespondenceMatcher getDataTaskReadCorrespondenceMatcher() throws IncQueryException {
    return DataTaskReadCorrespondenceMatcher.on(engine);
  }
  
  public TransitiveAffectedTasksThroughDataMatcher getTransitiveAffectedTasksThroughDataMatcher() throws IncQueryException {
    return TransitiveAffectedTasksThroughDataMatcher.on(engine);
  }
  
  public JobInfoCorrespondenceMatcher getJobInfoCorrespondenceMatcher() throws IncQueryException {
    return JobInfoCorrespondenceMatcher.on(engine);
  }
  
  public TasksAffectedThroughDataMatcher getTasksAffectedThroughDataMatcher() throws IncQueryException {
    return TasksAffectedThroughDataMatcher.on(engine);
  }
  
  public UndefinedServiceTasksMatcher getUndefinedServiceTasksMatcher() throws IncQueryException {
    return UndefinedServiceTasksMatcher.on(engine);
  }
}
