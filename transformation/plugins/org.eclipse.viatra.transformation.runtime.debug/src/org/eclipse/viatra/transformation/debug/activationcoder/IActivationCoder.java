/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
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
