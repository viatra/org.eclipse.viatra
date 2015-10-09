package org.eclipse.viatra.cep.core.compiler.testdata.patterns.rules;

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.viatra.cep.core.api.evm.CepActivationStates;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.api.rules.CepJob;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.jobs.R_Job;
import org.eclipse.viatra.cep.core.compiler.testdata.patterns.patterns.complex.Follows_Pattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;

@SuppressWarnings("all")
public class R implements ICepRule {
  private List<EventPattern> eventPatterns = Lists.newArrayList();
  
  private CepJob<IObservableComplexEventPattern> job = new R_Job(CepActivationStates.ACTIVE);
  
  public R() {
    eventPatterns.add(new Follows_Pattern());
  }
  
  @Override
  public List<EventPattern> getEventPatterns() {
    return this.eventPatterns;
  }
  
  @Override
  public CepJob<IObservableComplexEventPattern> getJob() {
    return this.job;
  }
}
