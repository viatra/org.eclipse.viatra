/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api.adapter;

import java.util.Iterator;

import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
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