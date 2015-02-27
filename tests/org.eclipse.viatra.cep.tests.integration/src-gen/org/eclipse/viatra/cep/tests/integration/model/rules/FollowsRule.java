package org.eclipse.viatra.cep.tests.integration.model.rules;

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.viatra.cep.core.api.evm.CepActivationStates;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.tests.integration.model.jobs.FollowsRule_Job;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.Follows_Pattern;

@SuppressWarnings("all")
public class FollowsRule implements ICepRule {
  private List<EventPattern> eventPatterns = Lists.newArrayList();
  
  private Job<IObservableComplexEventPattern> job = new FollowsRule_Job(CepActivationStates.ACTIVE);
  
  public FollowsRule() {
    eventPatterns.add(new Follows_Pattern());
  }
  
  @Override
  public List<EventPattern> getEventPatterns() {
    return this.eventPatterns;
  }
  
  @Override
  public Job<IObservableComplexEventPattern> getJob() {
    return this.job;
  }
}
