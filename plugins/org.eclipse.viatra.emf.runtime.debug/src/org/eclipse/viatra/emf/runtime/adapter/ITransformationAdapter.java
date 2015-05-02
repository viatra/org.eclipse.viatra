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
 * Interface that defines the methods of VIATRA transformation adapter objects. 
 * @author Lunk Péter
 */
public interface ITransformationAdapter {
    
    public void onTransformationInit();
    
    public void onFiring(Activation<?> activation);
    
    public void afterFiring(Activation<?> activation);
    
    public void onSchedule(ConflictSet conflictSet);
    
    public void afterSchedule(ConflictSet conflictSet);
    
    public void onTransformationDisposed();
    
}
