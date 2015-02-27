package org.eclipse.viatra.cep.tests.integration.model.rules;

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.viatra.cep.core.api.evm.CepActivationStates;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.tests.integration.model.jobs.OrRule_Job;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.Or_Pattern;

@SuppressWarnings("all")
public class OrRule implements ICepRule {
  private List<EventPattern> eventPatterns = Lists.newArrayList();
  
  private Job<IObservableComplexEventPattern> job = new OrRule_Job(CepActivationStates.ACTIVE);
  
  public OrRule() {
    eventPatterns.add(new Or_Pattern());
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
