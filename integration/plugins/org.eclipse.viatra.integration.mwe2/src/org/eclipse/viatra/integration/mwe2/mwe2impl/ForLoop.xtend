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
package org.eclipse.viatra.integration.mwe2.mwe2impl

import org.eclipse.viatra.integration.mwe2.providers.IIterationNumberProvider
import org.eclipse.viatra.integration.mwe2.providers.impl.BaseIterationNumberProvider

/**
 * Composite transformation step that implements a 'for' style loop. The number 
 * of iterations can either be explicitly specified at compile time, using the 
 * iterations attribute, or dynamically calculated by an IIterationNumberProvider. 
 * 
 * @author Peter Lunk
 */
class ForLoop extends Sequence {
    private var IIterationNumberProvider provider;
    
    /**
     * Specify the numebr of iteration explicitly
     */
    def void setIterations(String maxValue){
        try {
            var value = Integer.parseInt(maxValue)
            provider = new BaseIterationNumberProvider(value)
        }catch (NumberFormatException e) {
            e.printStackTrace
        }
    }
    
    /**
     * Add a provider
     */	
    def void setIterationProvider(IIterationNumberProvider provider){
        this.provider = provider
    }

    override void execute() {
        for (var i = 0; i < provider.iterationNumber; i = i + 1){
            step.forEach [
                execute
            ]
        }
    }
        
}