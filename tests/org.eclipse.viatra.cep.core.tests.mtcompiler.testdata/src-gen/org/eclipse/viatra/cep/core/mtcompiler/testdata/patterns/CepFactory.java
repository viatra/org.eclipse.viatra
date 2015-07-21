package org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns;

import com.google.common.collect.Lists;
import java.util.List;
import org.eclipse.viatra.cep.core.api.rules.ICepRule;
import org.eclipse.viatra.cep.core.metamodels.events.EventSource;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.A_1_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.A_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.B_1_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.B_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.C_1_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.C_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.Fall_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.Far_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.Left_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.Near_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.Right_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.events.Rise_Event;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.A_1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.A_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.B_1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.B_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.C_1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.C_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.Fall_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.Far_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.Left_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.Near_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.Right_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.atomic.Rise_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.And2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.And_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.AtLeast1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.AtLeast2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Duplicate2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Duplicate_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Follows_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Inf1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Inf2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.MultiplicityOnAtomic_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.MultiplicityOnComplex1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.MultiplicityOnComplex2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.MultiplicityOnComplex3_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.MultiplicityOnComplex4_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NestedFollows_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NestedOrWithFollows1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NestedOrWithFollows2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NestedOr_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotAndParams_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotAndTimewin_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotAnd_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotAtomic_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotFollows2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotFollowsParams_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotFollowsTimewin_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotFollows_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotOrParams_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotOrTimewin_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.NotOr_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Or_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.ParamsAnd_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.ParamsFollows_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.ParamsOr_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.Teq1_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinAnd_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinFollows2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinFollows3_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinFollows_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinOr2_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.patterns.complex.TimewinOr_Pattern;
import org.eclipse.viatra.cep.core.mtcompiler.testdata.patterns.rules.R;

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
   * Factory method for event class {@link A_Event}.
   */
  public A_Event createA_Event(final EventSource eventSource) {
    return new A_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link A_Event}.
   */
  public A_Event createA_Event() {
    return new A_Event(null);
  }
  
  /**
   * Factory method for event class {@link B_Event}.
   */
  public B_Event createB_Event(final EventSource eventSource) {
    return new B_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link B_Event}.
   */
  public B_Event createB_Event() {
    return new B_Event(null);
  }
  
  /**
   * Factory method for event class {@link C_Event}.
   */
  public C_Event createC_Event(final EventSource eventSource) {
    return new C_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link C_Event}.
   */
  public C_Event createC_Event() {
    return new C_Event(null);
  }
  
  /**
   * Factory method for event class {@link A_1_Event}.
   */
  public A_1_Event createA_1_Event(final EventSource eventSource) {
    return new A_1_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link A_1_Event}.
   */
  public A_1_Event createA_1_Event() {
    return new A_1_Event(null);
  }
  
  /**
   * Factory method for event class {@link B_1_Event}.
   */
  public B_1_Event createB_1_Event(final EventSource eventSource) {
    return new B_1_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link B_1_Event}.
   */
  public B_1_Event createB_1_Event() {
    return new B_1_Event(null);
  }
  
  /**
   * Factory method for event class {@link C_1_Event}.
   */
  public C_1_Event createC_1_Event(final EventSource eventSource) {
    return new C_1_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link C_1_Event}.
   */
  public C_1_Event createC_1_Event() {
    return new C_1_Event(null);
  }
  
  /**
   * Factory method for event class {@link Left_Event}.
   */
  public Left_Event createLeft_Event(final EventSource eventSource) {
    return new Left_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link Left_Event}.
   */
  public Left_Event createLeft_Event() {
    return new Left_Event(null);
  }
  
  /**
   * Factory method for event class {@link Right_Event}.
   */
  public Right_Event createRight_Event(final EventSource eventSource) {
    return new Right_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link Right_Event}.
   */
  public Right_Event createRight_Event() {
    return new Right_Event(null);
  }
  
  /**
   * Factory method for event class {@link Rise_Event}.
   */
  public Rise_Event createRise_Event(final EventSource eventSource) {
    return new Rise_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link Rise_Event}.
   */
  public Rise_Event createRise_Event() {
    return new Rise_Event(null);
  }
  
  /**
   * Factory method for event class {@link Fall_Event}.
   */
  public Fall_Event createFall_Event(final EventSource eventSource) {
    return new Fall_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link Fall_Event}.
   */
  public Fall_Event createFall_Event() {
    return new Fall_Event(null);
  }
  
  /**
   * Factory method for event class {@link Near_Event}.
   */
  public Near_Event createNear_Event(final EventSource eventSource) {
    return new Near_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link Near_Event}.
   */
  public Near_Event createNear_Event() {
    return new Near_Event(null);
  }
  
  /**
   * Factory method for event class {@link Far_Event}.
   */
  public Far_Event createFar_Event(final EventSource eventSource) {
    return new Far_Event(eventSource);
  }
  
  /**
   * Factory method for event class {@link Far_Event}.
   */
  public Far_Event createFar_Event() {
    return new Far_Event(null);
  }
  
  /**
   * Factory method for atomic event pattern {@link A_Pattern}.
   */
  public A_Pattern createA_Pattern() {
    return new A_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link B_Pattern}.
   */
  public B_Pattern createB_Pattern() {
    return new B_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link C_Pattern}.
   */
  public C_Pattern createC_Pattern() {
    return new C_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link A_1_Pattern}.
   */
  public A_1_Pattern createA_1_Pattern() {
    return new A_1_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link B_1_Pattern}.
   */
  public B_1_Pattern createB_1_Pattern() {
    return new B_1_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link C_1_Pattern}.
   */
  public C_1_Pattern createC_1_Pattern() {
    return new C_1_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link Left_Pattern}.
   */
  public Left_Pattern createLeft_Pattern() {
    return new Left_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link Right_Pattern}.
   */
  public Right_Pattern createRight_Pattern() {
    return new Right_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link Rise_Pattern}.
   */
  public Rise_Pattern createRise_Pattern() {
    return new Rise_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link Fall_Pattern}.
   */
  public Fall_Pattern createFall_Pattern() {
    return new Fall_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link Near_Pattern}.
   */
  public Near_Pattern createNear_Pattern() {
    return new Near_Pattern();
  }
  
  /**
   * Factory method for atomic event pattern {@link Far_Pattern}.
   */
  public Far_Pattern createFar_Pattern() {
    return new Far_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Follows_Pattern}.
   */
  public Follows_Pattern createFollows_Pattern() {
    return new Follows_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NestedFollows_Pattern}.
   */
  public NestedFollows_Pattern createNestedFollows_Pattern() {
    return new NestedFollows_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Or_Pattern}.
   */
  public Or_Pattern createOr_Pattern() {
    return new Or_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NestedOr_Pattern}.
   */
  public NestedOr_Pattern createNestedOr_Pattern() {
    return new NestedOr_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NestedOrWithFollows1_Pattern}.
   */
  public NestedOrWithFollows1_Pattern createNestedOrWithFollows1_Pattern() {
    return new NestedOrWithFollows1_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NestedOrWithFollows2_Pattern}.
   */
  public NestedOrWithFollows2_Pattern createNestedOrWithFollows2_Pattern() {
    return new NestedOrWithFollows2_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link And_Pattern}.
   */
  public And_Pattern createAnd_Pattern() {
    return new And_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link And2_Pattern}.
   */
  public And2_Pattern createAnd2_Pattern() {
    return new And2_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Duplicate_Pattern}.
   */
  public Duplicate_Pattern createDuplicate_Pattern() {
    return new Duplicate_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Duplicate2_Pattern}.
   */
  public Duplicate2_Pattern createDuplicate2_Pattern() {
    return new Duplicate2_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link MultiplicityOnAtomic_Pattern}.
   */
  public MultiplicityOnAtomic_Pattern createMultiplicityOnAtomic_Pattern() {
    return new MultiplicityOnAtomic_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link MultiplicityOnComplex1_Pattern}.
   */
  public MultiplicityOnComplex1_Pattern createMultiplicityOnComplex1_Pattern() {
    return new MultiplicityOnComplex1_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link MultiplicityOnComplex2_Pattern}.
   */
  public MultiplicityOnComplex2_Pattern createMultiplicityOnComplex2_Pattern() {
    return new MultiplicityOnComplex2_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link MultiplicityOnComplex3_Pattern}.
   */
  public MultiplicityOnComplex3_Pattern createMultiplicityOnComplex3_Pattern() {
    return new MultiplicityOnComplex3_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link MultiplicityOnComplex4_Pattern}.
   */
  public MultiplicityOnComplex4_Pattern createMultiplicityOnComplex4_Pattern() {
    return new MultiplicityOnComplex4_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Inf1_Pattern}.
   */
  public Inf1_Pattern createInf1_Pattern() {
    return new Inf1_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Inf2_Pattern}.
   */
  public Inf2_Pattern createInf2_Pattern() {
    return new Inf2_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link AtLeast1_Pattern}.
   */
  public AtLeast1_Pattern createAtLeast1_Pattern() {
    return new AtLeast1_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link AtLeast2_Pattern}.
   */
  public AtLeast2_Pattern createAtLeast2_Pattern() {
    return new AtLeast2_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link TimewinFollows_Pattern}.
   */
  public TimewinFollows_Pattern createTimewinFollows_Pattern() {
    return new TimewinFollows_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link TimewinFollows2_Pattern}.
   */
  public TimewinFollows2_Pattern createTimewinFollows2_Pattern() {
    return new TimewinFollows2_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link TimewinFollows3_Pattern}.
   */
  public TimewinFollows3_Pattern createTimewinFollows3_Pattern() {
    return new TimewinFollows3_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link TimewinOr_Pattern}.
   */
  public TimewinOr_Pattern createTimewinOr_Pattern() {
    return new TimewinOr_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link TimewinAnd_Pattern}.
   */
  public TimewinAnd_Pattern createTimewinAnd_Pattern() {
    return new TimewinAnd_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link TimewinOr2_Pattern}.
   */
  public TimewinOr2_Pattern createTimewinOr2_Pattern() {
    return new TimewinOr2_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotAtomic_Pattern}.
   */
  public NotAtomic_Pattern createNotAtomic_Pattern() {
    return new NotAtomic_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotFollows_Pattern}.
   */
  public NotFollows_Pattern createNotFollows_Pattern() {
    return new NotFollows_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotFollows2_Pattern}.
   */
  public NotFollows2_Pattern createNotFollows2_Pattern() {
    return new NotFollows2_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotOr_Pattern}.
   */
  public NotOr_Pattern createNotOr_Pattern() {
    return new NotOr_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotAnd_Pattern}.
   */
  public NotAnd_Pattern createNotAnd_Pattern() {
    return new NotAnd_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotFollowsParams_Pattern}.
   */
  public NotFollowsParams_Pattern createNotFollowsParams_Pattern() {
    return new NotFollowsParams_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotOrParams_Pattern}.
   */
  public NotOrParams_Pattern createNotOrParams_Pattern() {
    return new NotOrParams_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotAndParams_Pattern}.
   */
  public NotAndParams_Pattern createNotAndParams_Pattern() {
    return new NotAndParams_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotFollowsTimewin_Pattern}.
   */
  public NotFollowsTimewin_Pattern createNotFollowsTimewin_Pattern() {
    return new NotFollowsTimewin_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotOrTimewin_Pattern}.
   */
  public NotOrTimewin_Pattern createNotOrTimewin_Pattern() {
    return new NotOrTimewin_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link NotAndTimewin_Pattern}.
   */
  public NotAndTimewin_Pattern createNotAndTimewin_Pattern() {
    return new NotAndTimewin_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link ParamsFollows_Pattern}.
   */
  public ParamsFollows_Pattern createParamsFollows_Pattern() {
    return new ParamsFollows_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link ParamsOr_Pattern}.
   */
  public ParamsOr_Pattern createParamsOr_Pattern() {
    return new ParamsOr_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link ParamsAnd_Pattern}.
   */
  public ParamsAnd_Pattern createParamsAnd_Pattern() {
    return new ParamsAnd_Pattern();
  }
  
  /**
   * Factory method for complex event pattern {@link Teq1_Pattern}.
   */
  public Teq1_Pattern createTeq1_Pattern() {
    return new Teq1_Pattern();
  }
  
  /**
   * Factory method for rule {@link R}.
   */
  public R createR() {
    return new R();
  }
  
  /**
   * Factory method for instantiating every defined rule.
   */
  public List<ICepRule> allRules() {
    List<ICepRule> rules = Lists.newArrayList();
    rules.add(new R());
    return rules;
  }
}
