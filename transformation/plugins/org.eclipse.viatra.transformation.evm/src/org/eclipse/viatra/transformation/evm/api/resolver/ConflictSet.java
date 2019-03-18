/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.resolver;

import java.util.Set;

import org.eclipse.viatra.transformation.evm.api.Activation;

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
     * Returns a snapshot of activations that are considered as equal by the resolver. Each time the method is called, a
     * new copy of the activation set is returned.
     */
    Set<Activation<?>> getNextActivations();

    /**
     * Returns a snapshot of all activations that are in conflict (all enabled activations). Each time the method is
     * called, a new copy of the conflicting actions are returned.
     */
    Set<Activation<?>> getConflictingActivations();

    ConflictResolver getConflictResolver();

}