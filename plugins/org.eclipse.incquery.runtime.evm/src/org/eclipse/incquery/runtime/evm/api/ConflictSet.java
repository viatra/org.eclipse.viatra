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
 * @author Abel Hegedus
 *
 */
public interface ConflictSet {

    ConflictResolver<?> getConflictResolver();
    
    Activation<?> getNextActivation();
    
    Set<Activation<?>> getConflictingActivations();
    
    Set<Activation<?>> getEnabledActivations();
    
    boolean addActivation(Activation<?> activation);
    
    boolean removeActivation(Activation<?> activation);
    
}
