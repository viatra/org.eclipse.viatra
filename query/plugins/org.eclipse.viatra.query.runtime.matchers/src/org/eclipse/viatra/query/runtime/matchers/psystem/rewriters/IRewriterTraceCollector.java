/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.rewriters;

import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable;

/**
 * This is the internal API of {@link IPTraceableTraceProvider} expected to be used by
 * copier and rewriter implementations.
 * 
 * @since 1.6
 * @noreference This interface is not intended to be referenced by clients.
 */
public interface IRewriterTraceCollector extends IPTraceableTraceProvider {

    /**
     * Mark the given derivative to be originated from the given original constraint.
     */
    public void addTrace(PTraceable origin, PTraceable derivative);
    
    /**
     * Indicate that the given derivative is removed from the resulting query, thus its trace
     * information should be removed also.
     */
    public void derivativeRemoved(PTraceable derivative, IDerivativeModificationReason reason);
    
}
