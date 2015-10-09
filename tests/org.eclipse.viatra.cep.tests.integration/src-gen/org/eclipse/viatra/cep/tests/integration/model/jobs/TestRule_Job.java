package org.eclipse.viatra.cep.tests.integration.model.jobs;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.api.rules.CepJob;
import org.eclipse.viatra.cep.tests.integration.contexts.TestResultHelper;

@SuppressWarnings("all")
public class TestRule_Job extends CepJob<IObservableComplexEventPattern> {
  public TestRule_Job(final ActivationState activationState) {
    super(activationState);
  }
  
  @Override
  public void execute(final Activation<? extends IObservableComplexEventPattern> ruleInstance, final Context context) {
    TestResultHelper _instance = TestResultHelper.instance();
    IObservableComplexEventPattern _atom = ruleInstance.getAtom();
    String _observedEventPatternId = _atom.getObservedEventPatternId();
    _instance.incrementById(_observedEventPatternId);
  }
  
  @Override
  public void handleError(final Activation<? extends IObservableComplexEventPattern> ruleInstance, final Exception exception, final Context context) {
    //not gonna happen
  }
}
