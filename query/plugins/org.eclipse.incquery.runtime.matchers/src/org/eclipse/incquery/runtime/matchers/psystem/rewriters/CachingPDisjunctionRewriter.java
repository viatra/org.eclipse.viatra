/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem.rewriters;

import java.util.WeakHashMap;

import org.eclipse.incquery.runtime.matchers.psystem.queries.PDisjunction;

/**
 * An abstract disjunction rewriter that stores the results of previous rewrites.
 * @author Zoltan Ujhelyi
 * @deprecated Use the {@link PDisjunctionRewriterCacher} implementation instead.
 */
public abstract class CachingPDisjunctionRewriter extends PDisjunctionRewriter {

    private WeakHashMap<PDisjunction, PDisjunction> cachedResults =
            new WeakHashMap<PDisjunction, PDisjunction>();

    @Override
    public PDisjunction rewrite(PDisjunction disjunction) throws RewriterException {
        if (!cachedResults.containsKey(disjunction)) {
            PDisjunction rewritten = doRewrite(disjunction);
            cachedResults.put(disjunction, rewritten);
        }
        return cachedResults.get(disjunction);
    }
    
    protected abstract PDisjunction doRewrite(PDisjunction disjunction) throws RewriterException;
}
