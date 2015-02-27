package org.eclipse.viatra.cep.tests.integration.model;

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.tests.integration.model.events.A1_Event;
import org.eclipse.viatra.cep.tests.integration.model.events.A2_Event;
import org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.A1_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.atomic.A2_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.And_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.Follows_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.MultiplicityAtLeast_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.Multiplicity_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.patterns.complex.Or_Pattern;
import org.eclipse.viatra.cep.tests.integration.model.rules.FollowsRule;
import org.eclipse.viatra.cep.tests.integration.model.rules.OrRule;

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
   * Factory method for complex event pattern {@link Multiplicity_Pattern}.
   */
  public Multiplicity_Pattern createMultiplicity_Pattern() {
    return new Multiplicity_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link MultiplicityAtLeast_Pattern}.
   */
  public MultiplicityAtLeast_Pattern createMultiplicityAtLeast_Pattern() {
    return new MultiplicityAtLeast_Pattern();
  }
  
  /**
   * Factory method for rule {@link FollowsRule}.
   */
  public FollowsRule createFollowsRule() {
    return new FollowsRule();
  }
  
  /**
   * Factory method for rule {@link OrRule}.
   */
  public OrRule createOrRule() {
    return new OrRule();
  }
  
  /**
   * Factory method for instantiating every defined rule.
   */
  public List<ICepRule> allRules() {
    List<ICepRule> rules = Lists.newArrayList();
    rules.add(new FollowsRule());
    rules.add(new OrRule());
    return rules;
  }
}
