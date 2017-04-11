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
package org.eclipse.viatra.query.runtime.matchers.psystem.rewriters;

import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PDisjunction;

import com.google.common.collect.ImmutableList;

/**
 * A rewriter that stores the previously computed results of a rewriter or a rewriter chain.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.0
 */
public class PDisjunctionRewriterCacher extends PDisjunctionRewriter {

    private final List<PDisjunctionRewriter> rewriterChain;
    private WeakHashMap<PDisjunction, PDisjunction> cachedResults =
            new WeakHashMap<PDisjunction, PDisjunction>();

    private void setupTraceCollectorInChain(){
        IRewriterTraceCollector collector = getTraceCollector();
        for(PDisjunctionRewriter rewriter: rewriterChain){
            rewriter.setTraceCollector(collector);
        }
    }
    
    public PDisjunctionRewriterCacher(PDisjunctionRewriter rewriter) {
        rewriterChain = ImmutableList.of(rewriter);
    }
    
    public PDisjunctionRewriterCacher(PDisjunctionRewriter... rewriters) {
        rewriterChain = ImmutableList.copyOf(rewriters);
    }
    
    public PDisjunctionRewriterCacher(List<PDisjunctionRewriter> rewriterChain) {
        this.rewriterChain = ImmutableList.copyOf(rewriterChain);
    }
    
    @Override
    public PDisjunction rewrite(PDisjunction disjunction) throws RewriterException {
        if (!cachedResults.containsKey(disjunction)) {
            PDisjunction rewritten = disjunction;
            setupTraceCollectorInChain();
            for (PDisjunctionRewriter rewriter : rewriterChain) {
                rewritten = rewriter.rewrite(rewritten);
            }
            
            cachedResults.put(disjunction, rewritten);
        }
        return cachedResults.get(disjunction);
    }
    
}
