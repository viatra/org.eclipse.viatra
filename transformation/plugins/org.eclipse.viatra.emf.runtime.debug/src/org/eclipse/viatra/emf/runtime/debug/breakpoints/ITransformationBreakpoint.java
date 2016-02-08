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
package org.eclipse.viatra.emf.runtime.debug.breakpoints;

import org.eclipse.incquery.runtime.evm.api.Activation;

/**
 * Interface that defines transformation breakpoints
 * @author Peter Lunk
 *
 */
public interface ITransformationBreakpoint {
    /**
     * Method definition that returns true if the execution of the transformation should be halted
     * 
     * @param a
     * @return 
     */
    public boolean shouldBreak(Activation<?> a);
}
