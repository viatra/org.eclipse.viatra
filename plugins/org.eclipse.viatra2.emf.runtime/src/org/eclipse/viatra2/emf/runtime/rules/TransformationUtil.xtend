package org.eclipse.viatra2.emf.runtime.rules

import org.eclipse.incquery.runtime.evm.api.RuleEngine
import org.eclipse.incquery.runtime.evm.api.RuleSpecification
import org.eclipse.incquery.runtime.evm.api.event.EventFilter
import org.eclipse.incquery.runtime.evm.api.Context
import org.eclipse.incquery.runtime.evm.api.Activation
import org.eclipse.incquery.runtime.api.IPatternMatch
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum

class TransformationUtil {
  
  def <Match extends IPatternMatch> batchExecution(
    RuleEngine ruleEngine,
    Context context,
    RuleSpecification<Match> ruleSpecification,
    EventFilter<? super Match> filter    
  ) {
    
    ruleEngine.addRule(ruleSpecification, false, filter)
    
    println('''== Executing activations of «ruleSpecification» with filter «filter» ==''')
    var Activation<Match> act
    while((act = ruleEngine.firstActivation(ruleSpecification, filter)) != null){
      act.fireActivation(context)
    }
    println('''== Execution finished of «ruleSpecification» with filter «filter» ==''')
    ruleEngine.removeRule(ruleSpecification, filter)
    
  }
  
  def <Match extends IPatternMatch> batchExecution(
    RuleEngine ruleEngine,
    Context context,
    RuleSpecification<Match> ruleSpecification
  ) {
    val filter = ruleSpecification.createEmptyFilter
    ruleEngine.batchExecution(context, ruleSpecification, filter)
  }
  
  private def <Match extends IPatternMatch> firstActivation(
    RuleEngine engine,
    RuleSpecification<Match> ruleSpecification,
    EventFilter<? super Match> filter
  ) {
    engine.getActivations(ruleSpecification, filter, IncQueryActivationStateEnum::APPEARED).head
  }
  
  private def <Match extends IPatternMatch> fireActivation(
    Activation<Match> act,
    Context context
  ) {
    if(act != null && act.enabled){
      act.fire(context)
    }
  }
}