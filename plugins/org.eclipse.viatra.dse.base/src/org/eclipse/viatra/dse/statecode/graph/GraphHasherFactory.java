/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.statecode.graph;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.statecode.IStateSerializer;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;
import org.eclipse.viatra.dse.statecode.graph.impl.GraphHash;

public class GraphHasherFactory implements IStateSerializerFactory {

    private static final int DEFAULT_MAX_UNFOLDING_DEPTH = 5;

    private final int maxUnfoldingDepth;

    public GraphHasherFactory() {
        this(DEFAULT_MAX_UNFOLDING_DEPTH);
    }

    public GraphHasherFactory(int maxDepth) {
        maxUnfoldingDepth = maxDepth;
    }

    @Override
    public IStateSerializer createStateSerializer(Notifier modelRoot) {
        try {
            EMFScope scope = new EMFScope(modelRoot);
            IncQueryEngine engine = IncQueryEngine.on(scope);
            GraphHash gh = new GraphHash(modelRoot, engine);
            gh.setMaxDepth(maxUnfoldingDepth);
            return gh;
        } catch (IncQueryException e) {
            throw new DSEException(e);
        }
    }

    @Override
    public String toString() {
        return "Graph";
    }
}
