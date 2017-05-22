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
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

/**
 * This interface provides methods to trace the {@link PTraceable}s of a transformed {@link PQuery} produced by
 * a {@link PDisjunctionRewriter}. In case the associated rewriter is a composite (a.k.a. {@link PDisjunctionRewriterCacher}),
 * this trace provider handles traces end-to-end, hiding all the intermediate transformation steps.
 * 
 * @since 1.6
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IPTraceableTraceProvider {

    /**
     * Find and return the {@link PTraceable}s in the origin query which are the source of the given derivative
     * {@link PTraceable} according to the transformation.
     * 
     * @param derivative a {@link PTraceable} which is contained by the {@link PQuery} produced by the associated rewriter
     */
    public Iterable<PTraceable> getPTraceableTraces(PTraceable derivative);
    
    /**
     * Return the {@link PTraceable}s in the derivative query which have a known origin in the source query.
     */
    public Iterable<PTraceable> getKnownDerivatives();

}
