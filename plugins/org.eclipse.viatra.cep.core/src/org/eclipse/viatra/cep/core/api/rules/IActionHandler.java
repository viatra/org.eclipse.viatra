/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.api.rules;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.viatra.cep.core.api.patterns.IObservableComplexEventPattern;

/**
 * Interface to implement by handlers of actions (defined in rules associated with event patterns).
 * 
 * @author Istvan David
 *
 */
public interface IActionHandler {
    void handle(Activation<? extends IObservableComplexEventPattern> activation);
}
