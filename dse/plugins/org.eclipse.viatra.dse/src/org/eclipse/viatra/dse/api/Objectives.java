/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api;

import org.eclipse.viatra.dse.objectives.impl.CompositeObjective;
import org.eclipse.viatra.dse.objectives.impl.ConstraintsObjective;
import org.eclipse.viatra.dse.objectives.impl.DummyHardObjective;
import org.eclipse.viatra.dse.objectives.impl.MinimalDepthHardObjective;
import org.eclipse.viatra.dse.objectives.impl.NoRuleActivationsHardObjective;
import org.eclipse.viatra.dse.objectives.impl.TrajectoryCostSoftObjective;

/**
 * 
 * Helper class for creating built-in objectives.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class Objectives {

    private Objectives() {
    }

    /**
     * This objective uses VIATRA Queries to calculate fitness and/or goal constraints. Use methods on the returned
     * objective to configure it.
     * 
     * @param name
     * @return The objective.
     * @see ConstraintsObjective
     */
    public static ConstraintsObjective createConstraintsObjective(String name) {
        return new ConstraintsObjective(name);
    }

    /**
     * This objective calculates fitness on the trajectory by adding either fix costs to the rules, or by calculating
     * custom fitness on activation of rules.
     * 
     * @param name
     * @return The objective.
     * @see TrajectoryCostSoftObjective
     */
    public static TrajectoryCostSoftObjective createTrajcetoryCostObjective(String name) {
        return new TrajectoryCostSoftObjective(name);
    }

    /**
     * This objective adds a goal constraint that a solution state should not have any activations.
     * 
     * @return The objective.
     * @see NoRuleActivationsHardObjective
     */
    public static NoRuleActivationsHardObjective createNoRuleActivationsHardConstraint() {
        return new NoRuleActivationsHardObjective();
    }

    /**
     * This objective adds a goal constraint that a solution state should not have any activations.
     * 
     * @param name
     * @return The objective.
     * @see NoRuleActivationsHardObjective
     */
    public static NoRuleActivationsHardObjective createNoRuleActivationsHardConstraint(String name) {
        return new NoRuleActivationsHardObjective(name);
    }

    /**
     * This objective can combine the calculated fitness value of other objectives. Weights are supported.
     * 
     * @param name
     * @return The objective.
     * @see NoRuleActivationsHardObjective
     */
    public static CompositeObjective createCompositeObjective(String name) {
        return new CompositeObjective(name);
    }

    /**
     * This hard objective is fulfilled in any circumstances. Use it if all states should be regarded as a valid
     * solution.
     * 
     * @return The objective.
     * @see DummyHardObjective
     */
    public static DummyHardObjective createDummyHardObjective() {
        return new DummyHardObjective();
    }

    /**
     * This hard objective is fulfilled in any circumstances. Use it if all states should be regarded as a valid
     * solution.
     * 
     * @param name
     * @return The objective.
     * @see DummyHardObjective
     */
    public static DummyHardObjective createDummyHardObjective(String name) {
        return new DummyHardObjective(name);
    }

    /**
     * This hard objective is fulfilled if the trajectory is longer than a predefined number.
     * 
     * @param minDepth
     *            0 means all trajectory will be regarded as a solution.
     * @return The objective.
     * @see MinimalDepthHardObjective
     */
    public static MinimalDepthHardObjective createMinimalDepthHardObjective(int minDepth) {
        return new MinimalDepthHardObjective(minDepth);
    }

    /**
     * This hard objective is fulfilled if the trajectory is longer than a predefined number.
     * 
     * @param name
     * @param minDepth
     *            0 means all trajectory will be regarded as a solution.
     * @return The objective.
     * @see MinimalDepthHardObjective
     */
    public static MinimalDepthHardObjective createMinimalDepthHardObjective(String name, int minDepth) {
        return new MinimalDepthHardObjective(name, minDepth);
    }

}
