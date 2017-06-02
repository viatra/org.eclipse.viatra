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

import java.util.Collections;

import org.eclipse.viatra.query.runtime.matchers.psystem.PTraceable;

/**
 * This implementation does not store any traces and scales to NOP for every traceability feature.
 * @since 1.6
 *
 */
public class NopTraceCollector implements IRewriterTraceCollector {

    public static final IRewriterTraceCollector INSTANCE = new NopTraceCollector();
    
    private NopTraceCollector() {
        // Private constructor to force using the common instance
    }
    
    @Override
    public Iterable<PTraceable> getCanonicalTraceables(PTraceable derivative) {
        return Collections.emptyList();
    }

    @Override
    public Iterable<PTraceable> getRewrittenTraceables(PTraceable source) {
        return Collections.emptyList();
    }


    @Override
    public void addTrace(PTraceable origin, PTraceable derivative) {
        // ignored
    }

    @Override
    public void derivativeRemoved(PTraceable derivative, IDerivativeModificationReason reason) {
        // ignored
    }

    @Override
    public boolean isRemoved(PTraceable traceable) {
        return false;
    }

    @Override
    public Iterable<IDerivativeModificationReason> getRemovalReasons(PTraceable traceable) {
        return Collections.emptyList();
    }

}
