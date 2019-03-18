/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.communication;

import java.util.Set;

import org.eclipse.viatra.transformation.debug.model.breakpoint.ITransformationBreakpointHandler;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;

public interface IDebuggerTargetAgent {
    public void suspended();
    
    public void breakpointHit(ITransformationBreakpointHandler breakpoint);
    
    public void terminated() throws ViatraDebuggerException;
    
    public void conflictSetChanged(Set<Activation<?>> nextActivations, Set<Activation<?>> conflictingActivations);
    
    public void activationFired(Activation<?> activation);
    
    public void activationFiring(Activation<?> activation);
    
    public void nextActivationChanged(Activation<?> activation);

    public void addedRule(RuleSpecification<?> specification, EventFilter<?> filter);

    public void removedRule(RuleSpecification<?> specification, EventFilter<?> filter);
    
}
