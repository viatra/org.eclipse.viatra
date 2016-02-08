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
package org.eclipse.viatra.emf.runtime.debug.breakpoints.impl;

import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.emf.runtime.debug.breakpoints.ITransformationBreakpoint;

/**
 * Class that can be used to specify breakpoint rules via IncQuery query specifications. It is mainly used by 
 * the VIATRA {@link org.eclipse.viatra.emf.runtime.debug.TransformationDebugger}  class.
 * 
 * @author Peter Lunk
 *
 */
public class ConditionalTransformationBreakpoint implements ITransformationBreakpoint{
    IncQueryEngine engine;
    IQuerySpecification<?> spec;
    int numberOfMatches;
    
    public ConditionalTransformationBreakpoint(IncQueryEngine engine, IQuerySpecification<?> spec, int numberOfMatches){
        this.engine = engine;
        this.spec = spec;
        this.numberOfMatches = numberOfMatches;
    }
    
    @Override
    public boolean shouldBreak(Activation<?> a) {
        try {
            return engine.getMatcher(spec).getAllMatches().size() == numberOfMatches;
        } catch (IncQueryException e) {
            e.printStackTrace();
            return false;
        }
    }

}
