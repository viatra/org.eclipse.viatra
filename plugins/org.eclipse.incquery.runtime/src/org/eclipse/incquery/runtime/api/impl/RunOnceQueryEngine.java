/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.api.impl;

import java.util.Collection;

import org.apache.log4j.Level;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IRunOnceQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.base.api.BaseIndexOptions;
import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * Run-once query engines can be used to retrieve the current match set of query specifications
 * in a given scope. The engine is initialized with a {@link Notifier} as scope and a base index options
 * that specifically allows traversing derived features that are not well-behaving.
 * 
 * @author Abel Hegedus
 *
 */
public class RunOnceQueryEngine implements IRunOnceQueryEngine {

    /**
     * Override the default base index options to allow traversing and indexing derived features
     * that would be problematic in incremental evaluation.
     * 
     * @author Abel Hegedus
     *
     */
    private final class RunOnceBaseIndexOptions extends BaseIndexOptions {
        public RunOnceBaseIndexOptions() {
            this.traverseOnlyWellBehavingDerivedFeatures = false;
        }
    }

    /**
     * The scope of the engine that is used when creating one-time {@link IncQueryEngine}s.
     */
    private Notifier notifier;
    /**
     * The options that are used for initializing the {@link IncQueryEngine}.
     */
    private BaseIndexOptions baseIndexOptions;


    /**
     * Creates a run-once query engine on the given notifier.
     */
    public RunOnceQueryEngine(Notifier notifier) {
        this.notifier = notifier;
        this.baseIndexOptions = new RunOnceBaseIndexOptions();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.api.IRunOnceQueryEngine#getAllMatches(org.eclipse.incquery.runtime.api.IQuerySpecification)
     */
    @Override
    public <Match extends IPatternMatch> Collection<Match> getAllMatches(
            IQuerySpecification<? extends IncQueryMatcher<Match>> querySpecification) throws IncQueryException {
        
        AdvancedIncQueryEngine engine = AdvancedIncQueryEngine.createUnmanagedEngine(notifier, baseIndexOptions);
        engine.getLogger().setLevel(Level.TRACE);
        IncQueryMatcher<Match> matcher = engine.getMatcher(querySpecification);
        Collection<Match> allMatches = matcher.getAllMatches();
        engine.dispose();
        return allMatches;
    }
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.incquery.runtime.api.IRunOnceQueryEngine#getBaseIndexOptions()
     */
    @Override
    public BaseIndexOptions getBaseIndexOptions() {
        return baseIndexOptions;
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.api.IRunOnceQueryEngine#getScope()
     */
    @Override
    public Notifier getScope() {
        return notifier;
    }

}
