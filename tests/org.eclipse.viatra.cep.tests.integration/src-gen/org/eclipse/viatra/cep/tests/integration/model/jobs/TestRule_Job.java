package org.eclipse.viatra.cep.tests.integration.model.jobs;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.event.ActivationState;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;
import org.eclipse.viatra.cep.core.metamodels.events.EventPattern;
import org.eclipse.viatra.cep.tests.integration.contexts.TestResultHelper;

@SuppressWarnings("all")
public class TestRule_Job extends Job<IObservableComplexEventPattern> {
  public TestRule_Job(final ActivationState activationState) {
    super(activationState);
  }
  
  @Override
  public void execute(final Activation<? extends IObservableComplexEventPattern> activation, final Context context) {
    TestResultHelper _instance = TestResultHelper.instance();
    IObservableComplexEventPattern _atom = activation.getAtom();
    EventPattern _observableEventPattern = _atom.getObservableEventPattern();
    String _id = _observableEventPattern.getId();
    _instance.incrementById(_id);
  }
  
  @Override
  public void handleError(final Activation<? extends IObservableComplexEventPattern> activation, final Exception exception, final Context context) {
    //not gonna happen
  }
}
