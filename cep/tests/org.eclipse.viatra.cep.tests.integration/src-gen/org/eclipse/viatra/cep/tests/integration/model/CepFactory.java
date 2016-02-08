package org.eclipse.viatra.cep.tests.integration.model;

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.tests.integration.model.events.A1_Event;
import org.eclipse.viatra.cep.tests.integration.model.events.A2_Event;
import org.eclipse.viatra.cep.tests.integration.model.events.A3_Event;
import org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.A1_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.A2_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.A3_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.And_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.Follows_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.Multiplicity3_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.MultiplicityAtLeast_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.Or_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.rules.TestRule;

@SuppressWarnings("all")
public class CepFactory {
  private static CepFactory instance;
  
  public static CepFactory getInstance() {
    if(instance == null){
    	instance = new CepFactory();
    }
    return instance;
  }
  
  /**
   * Factory method for event class {@link A1_Event}.
   */
  public A1_Event createA1_Event(final EventSource eventSource) {
    return new A1_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link A1_Event}.
   */
  public A1_Event createA1_Event() {
    return new A1_Event(null);
  }
  
  /**
   * Factory method for event class {@link A2_Event}.
   */
  public A2_Event createA2_Event(final EventSource eventSource) {
    return new A2_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link A2_Event}.
   */
  public A2_Event createA2_Event() {
    return new A2_Event(null);
  }
  
  /**
   * Factory method for event class {@link A3_Event}.
   */
  public A3_Event createA3_Event(final EventSource eventSource) {
    return new A3_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link A3_Event}.
   */
  public A3_Event createA3_Event() {
    return new A3_Event(null);
  }
  
  /**
   * Factory method for atomic event pattern {@link A1_Pattern}.
   */
  public A1_Pattern createA1_Pattern() {
    return new A1_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link A2_Pattern}.
   */
  public A2_Pattern createA2_Pattern() {
    return new A2_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link A3_Pattern}.
   */
  public A3_Pattern createA3_Pattern() {
    return new A3_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Follows_Pattern}.
   */
  public Follows_Pattern createFollows_Pattern() {
    return new Follows_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Or_Pattern}.
   */
  public Or_Pattern createOr_Pattern() {
    return new Or_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link And_Pattern}.
   */
  public And_Pattern createAnd_Pattern() {
    return new And_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Multiplicity3_Pattern}.
   */
  public Multiplicity3_Pattern createMultiplicity3_Pattern() {
    return new Multiplicity3_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link MultiplicityAtLeast_Pattern}.
   */
  public MultiplicityAtLeast_Pattern createMultiplicityAtLeast_Pattern() {
    return new MultiplicityAtLeast_Pattern();
  }
  
  /**
   * Factory method for rule {@link TestRule}.
   */
  public Class<? extends ICepRule> rule_TestRule() {
    return TestRule.class;
  }
  
  /**
   * Factory method for instantiating every defined rule.
   */
  public List<Class<? extends ICepRule>> allRules() {
    List<Class<? extends ICepRule>> rules = Lists.newArrayList();
    rules.add(TestRule.class);
    return rules;
  }
}
