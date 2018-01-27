/**
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.integration.evm.jdt;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom;
import org.eclipse.viatra.integration.evm.jdt.JDTEventFilter;
import org.eclipse.viatra.integration.evm.jdt.JDTEventSourceSpecification;
import org.eclipse.viatra.integration.evm.jdt.job.JDTJobFactory;
import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle;
import org.eclipse.viatra.transformation.evm.api.Job;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public abstract class JDTRule {
  protected final JDTEventSourceSpecification eventSourceSpecification;
  
  protected final ActivationLifeCycle activationLifeCycle;
  
  @Extension
  protected final JDTJobFactory jobFactory;
  
  @Accessors
  protected final Set<Job<JDTEventAtom>> jobs = new HashSet<Job<JDTEventAtom>>();
  
  protected RuleSpecification<JDTEventAtom> ruleSpecification;
  
  protected EventFilter<JDTEventAtom> filter;
  
  public JDTRule(final JDTEventSourceSpecification eventSourceSpecification, final ActivationLifeCycle activationLifeCycle, final IJavaProject project, final JDTJobFactory jobFactory) {
    this.eventSourceSpecification = eventSourceSpecification;
    this.activationLifeCycle = activationLifeCycle;
    EventFilter<JDTEventAtom> _createEmptyFilter = eventSourceSpecification.createEmptyFilter();
    final JDTEventFilter filter = ((JDTEventFilter) _createEmptyFilter);
    filter.setProject(project);
    this.filter = filter;
    this.jobFactory = jobFactory;
    this.initialize();
  }
  
  public JDTRule(final JDTEventSourceSpecification eventSourceSpecification, final ActivationLifeCycle activationLifeCycle, final IJavaProject project) {
    this(eventSourceSpecification, activationLifeCycle, project, new JDTJobFactory());
  }
  
  public abstract void initialize();
  
  public EventFilter<JDTEventAtom> getFilter() {
    return this.filter;
  }
  
  public RuleSpecification<JDTEventAtom> getRuleSpecification() {
    if ((this.ruleSpecification == null)) {
      RuleSpecification<JDTEventAtom> _ruleSpecification = new RuleSpecification<JDTEventAtom>(this.eventSourceSpecification, this.activationLifeCycle, this.jobs);
      this.ruleSpecification = _ruleSpecification;
    }
    return this.ruleSpecification;
  }
  
  @Pure
  public Set<Job<JDTEventAtom>> getJobs() {
    return this.jobs;
  }
}
