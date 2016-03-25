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
package org.eclipse.viatra.transformation.evm.api.adapter;

import java.util.Iterator;

import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;

/**
 * Interface that defines the methods of EVM adapter objects. The interface contains callback methods for various EVM
 * events. Through these methods {@link IEVMAdapter} implementations can alter the execution of an EVM program.
 * 
 * @author Peter Lunk
 */
public interface IEVMAdapter {

    public Iterator<Activation<?>> getExecutableActivations(Iterator<Activation<?>> iterator);

    public ChangeableConflictSet getConflictSet(ChangeableConflictSet set);

}
