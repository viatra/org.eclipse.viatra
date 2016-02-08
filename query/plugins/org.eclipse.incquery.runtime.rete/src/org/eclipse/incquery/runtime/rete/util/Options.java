/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.rete.util;

import org.eclipse.incquery.runtime.matchers.planning.IQueryPlannerStrategy;
import org.eclipse.incquery.runtime.rete.construction.basiclinear.BasicLinearLayout;
import org.eclipse.incquery.runtime.rete.construction.quasitree.QuasiTreeLayout;

public class Options {

    public enum NodeSharingOption {
        NEVER, // not recommended, patternmatcher leaks possible
        INDEXER_AND_REMOTEPROXY, ALL
    }

    public final static NodeSharingOption nodeSharingOption = NodeSharingOption.ALL;
    public static final boolean releaseOnetimeIndexers = true; // effective only
                                                               // with
                                                               // nodesharing
                                                               // ==NEVER

    public enum InjectivityStrategy {
        EAGER, LAZY
    }

    public final static InjectivityStrategy injectivityStrategy = InjectivityStrategy.EAGER;

    public final static boolean enableInheritance = true;

    // public final static boolean useComplementerMask = true;

    public static final boolean employTrivialIndexers = true;

    // public final static boolean synchronous = false;

    public final static int numberOfLocalContainers = 1;
    public final static int firstFreeContainer = 0; // 0 if head container is
                                                    // free to contain pattern
                                                    // bodies, 1 otherwise

    public enum BuilderMethod {
        LEGACY, // ONLY with GTASM
        PSYSTEM_BASIC_LINEAR, PSYSTEM_QUASITREE;
        public IQueryPlannerStrategy layoutStrategy() {
            switch (this) {
            case PSYSTEM_BASIC_LINEAR:
                return new BasicLinearLayout();
            case PSYSTEM_QUASITREE:
                return new QuasiTreeLayout();
            default:
            	throw new UnsupportedOperationException();
            }
        }
    }

    public final static BuilderMethod builderMethod =
    // BuilderMethod.PSYSTEM_BASIC_LINEAR;
    BuilderMethod.PSYSTEM_QUASITREE;
    
    public enum FunctionalDependencyOption {
    	OFF,
    	OPPORTUNISTIC
    }
    public static FunctionalDependencyOption functionalDependencyOption = 
    		FunctionalDependencyOption.OPPORTUNISTIC;
    
    public enum PlanTrimOption {
    	OFF,
    	OPPORTUNISTIC
    }
    public static PlanTrimOption planTrimOption = 
    		PlanTrimOption.OPPORTUNISTIC;


}
