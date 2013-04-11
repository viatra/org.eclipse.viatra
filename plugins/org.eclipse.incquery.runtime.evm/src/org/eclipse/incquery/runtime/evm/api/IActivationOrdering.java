/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.api;

import java.util.Set;

/**
 * 
 * @author Abel Hegedus
 *
 */
public interface IActivationOrdering<ActivationContainer> {

    ActivationContainer createActivationContainer();
    
    Set<Activation<?>> getActivations(ActivationContainer container);
    
    boolean addActivationToContainer(ActivationContainer container, Activation<?> activation);
    
    boolean removeActivationFromContainer(ActivationContainer container, Activation<?> activation);
}
