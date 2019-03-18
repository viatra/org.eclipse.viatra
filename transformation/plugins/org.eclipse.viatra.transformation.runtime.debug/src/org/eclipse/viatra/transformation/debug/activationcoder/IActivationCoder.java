/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.viatra.transformation.debug.activationcoder;

import org.eclipse.viatra.transformation.debug.transformationtrace.model.ActivationTrace;
import org.eclipse.viatra.transformation.evm.api.Activation;

/**
 * Interface that defines methods for for creating traces based on individual rule activations.
 * 
 * @author Peter Lunk
 *
 */
public interface IActivationCoder {
    public ActivationTrace createActivationCode(Activation<?> activation);
}
