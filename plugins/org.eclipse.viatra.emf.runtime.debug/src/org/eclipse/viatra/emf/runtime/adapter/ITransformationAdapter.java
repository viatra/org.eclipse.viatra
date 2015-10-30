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
package org.eclipse.viatra.emf.runtime.adapter;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictSet;

/**
 * Interface that defines the methods of VIATRA transformation adapter objects. The interface contains callback methods
 * for various transformation events. All callbacks are synchronous, so they can block execution of the transformation.
 * 
 * @author Lunk Péter
 */
public interface ITransformationAdapter {
    
    public void beforeTransformation();
    
    public Activation<?> beforeFiring(Activation<?> activation);
    
    public Activation<?> afterFiring(Activation<?> activation);
    
    public ConflictSet beforeSchedule(ConflictSet conflictSet);
    
    public ConflictSet afterSchedule(ConflictSet conflictSet);
    
    public void afterTransformation();
    
}
