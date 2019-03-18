/*******************************************************************************
 * Copyright (c) 2010-2013, Peter Lunk, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import java.util.Iterator;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;

/**
 * Abstract {@link IEVMAdapter} implementation.
 * 
 * @author Peter Lunk
 *
 */
public class AbstractEVMAdapter implements IEVMAdapter {

    
    @Override
    public Iterator<Activation<?>> getExecutableActivations(Iterator<Activation<?>> iterator) {
        return iterator;
    }

    @Override
    public ChangeableConflictSet getConflictSet(ChangeableConflictSet set) {
        return set;
    }
}