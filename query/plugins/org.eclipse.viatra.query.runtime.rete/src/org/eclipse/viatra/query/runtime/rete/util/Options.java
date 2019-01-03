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

package org.eclipse.viatra.query.runtime.rete.util;

import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.viatra.query.runtime.matchers.context.IQueryBackendContext;
import org.eclipse.viatra.query.runtime.matchers.planning.IQueryPlannerStrategy;
import org.eclipse.viatra.query.runtime.rete.construction.basiclinear.BasicLinearLayout;
import org.eclipse.viatra.query.runtime.rete.construction.quasitree.QuasiTreeLayout;
import org.eclipse.viatra.query.runtime.rete.network.communication.ddf.DifferentialCommunicationGroup;

/**
 * Feature switches.
 * @author Gabor Bergmann
 * @noreference
 */
public class Options {

    public enum NodeSharingOption {
        NEVER, // not recommended, patternmatcher leaks possible
        INDEXER_AND_REMOTEPROXY, ALL
    }

    public static final NodeSharingOption nodeSharingOption = NodeSharingOption.ALL;
    public static final boolean releaseOnetimeIndexers = true; // effective only
                                                               // with
                                                               // nodesharing
                                                               // ==NEVER

    public enum InjectivityStrategy {
        EAGER, LAZY
    }

    public static final InjectivityStrategy injectivityStrategy = InjectivityStrategy.EAGER;

    public static final boolean enableInheritance = true;

    // public final static boolean useComplementerMask = true;

    public static final boolean employTrivialIndexers = true;

    // public final static boolean synchronous = false;

    public static final int numberOfLocalContainers = 1;
    public static final int firstFreeContainer = 0; // 0 if head container is
                                                    // free to contain pattern
                                                    // bodies, 1 otherwise

    /**
     * Enable for internal debugging of Rete communication scheme; 
     *  catches cases where the topological sort is violated by a message sent "backwards"
     * @since 1.6
     */
    public static final boolean MONITOR_VIOLATION_OF_RETE_NODEGROUP_TOPOLOGICAL_SORTING = false;
    
    /**
     * Enable for internal debugging of message delivery in {@link DifferentialCommunicationGroup}s;
     * catches cases when there is a violation of increasing timestamps during message delivery within a group.  
     * @since 2.2
     */
    public static final boolean MONITOR_VIOLATION_OF_DIFFERENTIAL_DATAFLOW_TIMESTAMPS = false;
    
    /**
     * 
     * @author Gabor Bergmann
     * @noreference
     */
    public enum BuilderMethod {
        LEGACY, // ONLY with GTASM
        PSYSTEM_BASIC_LINEAR, PSYSTEM_QUASITREE;
        /**
         * @since 1.5
         */
        public IQueryPlannerStrategy layoutStrategy(IQueryBackendContext bContext, IQueryBackendHintProvider hintProvider) {
            switch (this) {
            case PSYSTEM_BASIC_LINEAR:
                return new BasicLinearLayout(bContext);
            case PSYSTEM_QUASITREE:
                return new QuasiTreeLayout(bContext, hintProvider);
            default:
                throw new UnsupportedOperationException();
            }
        }
    }

    public static final BuilderMethod builderMethod =
    // BuilderMethod.PSYSTEM_BASIC_LINEAR;
    BuilderMethod.PSYSTEM_QUASITREE;
    
    public enum FunctionalDependencyOption {
        OFF,
        OPPORTUNISTIC
    }
    public static final FunctionalDependencyOption functionalDependencyOption = 
            FunctionalDependencyOption.OPPORTUNISTIC;
    
    public enum PlanTrimOption {
        OFF,
        OPPORTUNISTIC
    }
    public static final PlanTrimOption planTrimOption = 
            PlanTrimOption.OPPORTUNISTIC;

}
