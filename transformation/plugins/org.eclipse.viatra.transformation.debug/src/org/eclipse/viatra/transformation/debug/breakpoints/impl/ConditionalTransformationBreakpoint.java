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
package org.eclipse.viatra.transformation.debug.breakpoints.impl;

import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.transformation.debug.breakpoints.ITransformationBreakpoint;
import org.eclipse.viatra.transformation.evm.api.Activation;

/**
 * Class that can be used to specify breakpoint rules via query specifications. It is mainly used by 
 * the VIATRA {@link org.eclipse.viatra.transformation.debug.TransformationDebugger}  class.
 * 
 * @author Peter Lunk
 *
 */
public class ConditionalTransformationBreakpoint implements ITransformationBreakpoint{
    ViatraQueryEngine engine;
    IQuerySpecification<?> spec;
    int numberOfMatches;
    
    public ConditionalTransformationBreakpoint(ViatraQueryEngine engine, IQuerySpecification<?> spec, int numberOfMatches){
        this.engine = engine;
        this.spec = spec;
        this.numberOfMatches = numberOfMatches;
    }
    
    @Override
    public boolean shouldBreak(Activation<?> a) {
        try {
            return engine.getMatcher(spec).getAllMatches().size() == numberOfMatches;
        } catch (ViatraQueryException e) {
            e.printStackTrace();
            return false;
        }
    }

}
