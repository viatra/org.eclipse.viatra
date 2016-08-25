/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.debug.model.breakpoint;

import java.io.Serializable;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.viatra.transformation.debug.model.TransformationDebugElement;
import org.eclipse.viatra.transformation.evm.api.Activation;

/**
 * Interface that defines transformation breakpoints
 * @author Peter Lunk
 *
 */
public interface ITransformationBreakpoint extends IBreakpoint, Serializable{
    public static final String NON_PERSISTENT = TransformationDebugElement.MODEL_ID;
    public static final String RULE = "org.eclipse.viatra.transformation.debug.model.rule";
    public static final String CONDITIONAL = "org.eclipse.viatra.transformation.debug.model.conditional";
    /**
     * Method definition that returns true if the execution of the transformation should be halted
     * 
     * @param a
     * @return 
     */
    public boolean shouldBreak(Activation<?> a);
    
    public String getMarkerIdentifier();
        
}
