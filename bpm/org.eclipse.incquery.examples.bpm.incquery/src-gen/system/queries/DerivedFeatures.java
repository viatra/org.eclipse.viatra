package system.queries;

import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedPatternGroup;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import system.queries.DataTaskReadCorrespondenceMatcher;
import system.queries.DataTaskWriteCorrespondenceMatcher;
import system.queries.JobInfoCorrespondenceMatcher;
import system.queries.JobTaskCorrespondenceMatcher;
import system.queries.TasksAffectedThroughDataMatcher;
import system.queries.TransitiveAffectedTasksThroughDataMatcher;
import system.queries.UndefinedServiceTasksMatcher;
import system.queries.util.DataTaskReadCorrespondenceQuerySpecification;
import system.queries.util.DataTaskWriteCorrespondenceQuerySpecification;
import system.queries.util.JobInfoCorrespondenceQuerySpecification;
import system.queries.util.JobTaskCorrespondenceQuerySpecification;
import system.queries.util.TasksAffectedThroughDataQuerySpecification;
import system.queries.util.TransitiveAffectedTasksThroughDataQuerySpecification;
import system.queries.util.UndefinedServiceTasksQuerySpecification;

/**
 * A pattern group formed of all patterns defined in derivedFeatures.eiq.
 * 
 * <p>Use the static instance as any {@link org.eclipse.incquery.runtime.api.IPatternGroup}, to conveniently prepare
 * an EMF-IncQuery engine for matching all patterns originally defined in file derivedFeatures.eiq,
 * in order to achieve better performance than one-by-one on-demand matcher initialization.
 * 
 * <p> From package system.queries, the group contains the definition of the following patterns: <ul>
 * <li>TaskKind</li>
 * <li>JobTaskCorrespondence</li>
 * <li>TaskHasJob</li>
 * <li>DataTaskReadCorrespondence</li>
 * <li>DataTaskWriteCorrespondence</li>
 * <li>JobInfoCorrespondence</li>
 * <li>UndefinedServiceTasks</li>
 * <li>TasksAffectedThroughData</li>
 * <li>TransitiveAffectedTasksThroughData</li>
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
    querySpecifications.add(JobTaskCorrespondenceQuerySpecification.instance());
    querySpecifications.add(DataTaskReadCorrespondenceQuerySpecification.instance());
    querySpecifications.add(DataTaskWriteCorrespondenceQuerySpecification.instance());
    querySpecifications.add(JobInfoCorrespondenceQuerySpecification.instance());
    querySpecifications.add(UndefinedServiceTasksQuerySpecification.instance());
    querySpecifications.add(TasksAffectedThroughDataQuerySpecification.instance());
    querySpecifications.add(TransitiveAffectedTasksThroughDataQuerySpecification.instance());
    
  }
  
  public JobTaskCorrespondenceQuerySpecification getJobTaskCorrespondence() throws IncQueryException {
    return JobTaskCorrespondenceQuerySpecification.instance();
  }
  
  public JobTaskCorrespondenceMatcher getJobTaskCorrespondence(final IncQueryEngine engine) throws IncQueryException {
    return JobTaskCorrespondenceMatcher.on(engine);
  }
  
  public DataTaskReadCorrespondenceQuerySpecification getDataTaskReadCorrespondence() throws IncQueryException {
    return DataTaskReadCorrespondenceQuerySpecification.instance();
  }
  
  public DataTaskReadCorrespondenceMatcher getDataTaskReadCorrespondence(final IncQueryEngine engine) throws IncQueryException {
    return DataTaskReadCorrespondenceMatcher.on(engine);
  }
  
  public DataTaskWriteCorrespondenceQuerySpecification getDataTaskWriteCorrespondence() throws IncQueryException {
    return DataTaskWriteCorrespondenceQuerySpecification.instance();
  }
  
  public DataTaskWriteCorrespondenceMatcher getDataTaskWriteCorrespondence(final IncQueryEngine engine) throws IncQueryException {
    return DataTaskWriteCorrespondenceMatcher.on(engine);
  }
  
  public JobInfoCorrespondenceQuerySpecification getJobInfoCorrespondence() throws IncQueryException {
    return JobInfoCorrespondenceQuerySpecification.instance();
  }
  
  public JobInfoCorrespondenceMatcher getJobInfoCorrespondence(final IncQueryEngine engine) throws IncQueryException {
    return JobInfoCorrespondenceMatcher.on(engine);
  }
  
  public UndefinedServiceTasksQuerySpecification getUndefinedServiceTasks() throws IncQueryException {
    return UndefinedServiceTasksQuerySpecification.instance();
  }
  
  public UndefinedServiceTasksMatcher getUndefinedServiceTasks(final IncQueryEngine engine) throws IncQueryException {
    return UndefinedServiceTasksMatcher.on(engine);
  }
  
  public TasksAffectedThroughDataQuerySpecification getTasksAffectedThroughData() throws IncQueryException {
    return TasksAffectedThroughDataQuerySpecification.instance();
  }
  
  public TasksAffectedThroughDataMatcher getTasksAffectedThroughData(final IncQueryEngine engine) throws IncQueryException {
    return TasksAffectedThroughDataMatcher.on(engine);
  }
  
  public TransitiveAffectedTasksThroughDataQuerySpecification getTransitiveAffectedTasksThroughData() throws IncQueryException {
    return TransitiveAffectedTasksThroughDataQuerySpecification.instance();
  }
  
  public TransitiveAffectedTasksThroughDataMatcher getTransitiveAffectedTasksThroughData(final IncQueryEngine engine) throws IncQueryException {
    return TransitiveAffectedTasksThroughDataMatcher.on(engine);
  }
}
