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
package org.eclipse.viatra.integration.mwe2.providers;

/**
 * Objects implementing this interface can be used for providing the number of iterations in a for loop
 * @author Peter Lunk
 *
 */
public interface IIterationNumberProvider extends IProvider {
    /**
     * returns the number of iterations
     * @return
     */
    public Integer getIterationNumber();    
}
