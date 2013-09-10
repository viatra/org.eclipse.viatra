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
package org.eclipse.incquery.runtime.evm.api.resolver;

import java.util.Set;

import org.eclipse.incquery.runtime.evm.api.Activation;

/**
 * @author Abel Hegedus
 *
 */
public interface ConflictSet {

    /**
     *
     * @return the next activation chosen by the resolver
     */
    Activation<?> getNextActivation();

    /**
     *
     * @return the set of activations that are considered as equal by the resolver
     */
    Set<Activation<?>> getNextActivations();

    /**
     *
     * @return the set of all activations that are in conflict (all enabled activations)
     */
    Set<Activation<?>> getConflictingActivations();

    /**
     * @return
     */
    ConflictResolver getConflictResolver();

}