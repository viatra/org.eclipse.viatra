package org.eclipse.incquery.examples.bpm.queries;

import org.eclipse.incquery.examples.bpm.queries.EntryTaskMatcher;
import org.eclipse.incquery.examples.bpm.queries.JobTasksMatcher;
import org.eclipse.incquery.examples.bpm.queries.NextActivityMatcher;
import org.eclipse.incquery.examples.bpm.queries.ProcessTasksMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;

@SuppressWarnings("all")
public final class BpmTestQueriesMatchers {
  private IncQueryEngine engine;
  
  public BpmTestQueriesMatchers(final IncQueryEngine engine) {
    this.engine = engine;
    
  }
  
  public ProcessTasksMatcher getProcessTasksMatcher() throws IncQueryException {
    return ProcessTasksMatcher.on(engine);
  }
  
  public EntryTaskMatcher getEntryTaskMatcher() throws IncQueryException {
    return EntryTaskMatcher.on(engine);
  }
  
  public JobTasksMatcher getJobTasksMatcher() throws IncQueryException {
    return JobTasksMatcher.on(engine);
  }
  
  public NextActivityMatcher getNextActivityMatcher() throws IncQueryException {
    return NextActivityMatcher.on(engine);
  }
}
