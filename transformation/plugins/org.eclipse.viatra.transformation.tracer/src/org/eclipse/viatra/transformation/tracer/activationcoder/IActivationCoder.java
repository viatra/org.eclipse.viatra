/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.tracer.activationcoder;

import java.util.Map;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRule;
import org.eclipse.viatra.transformation.tracer.transformationtrace.ActivationTrace;

/**
 * Interface that defines methods for for creating traces based on individual rule activations.
 * 
 * @author Peter Lunk
 *
 */
public interface IActivationCoder {
    public ActivationTrace createActivationCode(Activation<?> activation, Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules);
}
