/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt

import java.util.HashSet
import java.util.Set
import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle
import org.eclipse.viatra.transformation.evm.api.Job
import org.eclipse.viatra.transformation.evm.api.RuleSpecification
import org.eclipse.jdt.core.IJavaProject
import org.eclipse.viatra.transformation.evm.api.event.EventFilter
import org.eclipse.viatra.integration.evm.jdt.job.JDTJobFactory
import org.eclipse.xtend.lib.annotations.Accessors

abstract class JDTRule {
    protected val JDTEventSourceSpecification eventSourceSpecification
    protected val ActivationLifeCycle activationLifeCycle
    protected extension val JDTJobFactory jobFactory
    @Accessors
    protected val Set<Job<JDTEventAtom>> jobs = new HashSet
    protected RuleSpecification<JDTEventAtom> ruleSpecification
    protected EventFilter<JDTEventAtom> filter

    new(JDTEventSourceSpecification eventSourceSpecification, ActivationLifeCycle activationLifeCycle, IJavaProject project, JDTJobFactory jobFactory) {
        this.eventSourceSpecification = eventSourceSpecification
        this.activationLifeCycle = activationLifeCycle
        val filter = eventSourceSpecification.createEmptyFilter as JDTEventFilter
        filter.project = project
        this.filter = filter
        this.jobFactory = jobFactory
        initialize
    }
    
    new(JDTEventSourceSpecification eventSourceSpecification, ActivationLifeCycle activationLifeCycle, IJavaProject project) {
        this(eventSourceSpecification, activationLifeCycle, project, new JDTJobFactory)
    }
    
    def void initialize()
    
    def EventFilter<JDTEventAtom> getFilter() {
        return filter
    }
    
    def RuleSpecification<JDTEventAtom> getRuleSpecification() {
        if(ruleSpecification === null) {
            ruleSpecification = new RuleSpecification(eventSourceSpecification, activationLifeCycle, jobs)
        }
        return ruleSpecification
    }
    
}