/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.matcher;

import org.eclipse.viatra.query.runtime.matchers.backend.IMatcherCapability;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackend;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;
import org.eclipse.viatra.query.runtime.rete.construction.plancompiler.ReteRecipeCompiler;
import org.eclipse.viatra.query.runtime.rete.util.Options;

public class ReteBackendFactory implements IQueryBackendFactory {
    /**
     * EXPERIMENTAL
     */
    private final static int reteThreads = 0;
    
    /**
     * @since 1.5
     */
    @Override
    public IQueryBackend create(IQueryBackendContext context) 
    {
	    ReteEngine engine;
	    engine = new ReteEngine(context, reteThreads);
	    IQueryBackendHintProvider hintConfiguration = engine.getHintConfiguration();
	    ReteRecipeCompiler compiler = 
	    		new ReteRecipeCompiler(
	    				Options.builderMethod.layoutStrategy(context, hintConfiguration), 
	    				context.getLogger(),
	    				context.getRuntimeContext().getMetaContext(),
	    				context.getQueryCacheContext(),
	    				hintConfiguration,
	    				context.getQueryAnalyzer());
	    //EPMBuilder builder = new EPMBuilder(buildable, context);
	    engine.setCompiler(compiler);
	    return engine;
	}
    
    @Override
    public Class<? extends IQueryBackend> getBackendClass() {
    	return ReteEngine.class;
    }

	@Override
	public int hashCode() {
		return ReteBackendFactory.class.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ReteBackendFactory)) {
			return false;
		}
		return true;
	}

    /**
     * @since 1.4
     */
    @Override
    public IMatcherCapability calculateRequiredCapability(PQuery query, QueryEvaluationHint hint) {
        return new IncrementalMatcherCapability();
    }
    
    
}