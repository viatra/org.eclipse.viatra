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

import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.dse.statecode.IStateCoderFactory;
import org.eclipse.viatra.dse.statecode.graph.impl.GraphHash;

public class GraphHasherFactory implements IStateCoderFactory {

    private static final int DEFAULT_MAX_UNFOLDING_DEPTH = 5;

    private final int maxUnfoldingDepth;

    public GraphHasherFactory() {
        this(DEFAULT_MAX_UNFOLDING_DEPTH);
    }

    public GraphHasherFactory(int maxDepth) {
        maxUnfoldingDepth = maxDepth;
    }

    @Override
    public IStateCoder createStateCoder() {
        GraphHash gh = new GraphHash();
        gh.setMaxDepth(maxUnfoldingDepth);
        return gh;
    }

    @Override
    public String toString() {
        return "Graph";
    }
}
