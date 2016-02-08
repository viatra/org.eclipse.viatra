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
package org.eclipse.viatra.dse.objectives.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.IObjective;

import com.google.common.base.Preconditions;

/**
 * This hard objective collects a list of other hard objectives and checks if any of them is unsatisfied. In such a
 * case, it returns 0. If all of them is satisfied it returns 1.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class CompositeHardObjective extends BaseObjective {

    public static final String DEFAULT_NAME = "CompositHardObjective";
    protected List<IObjective> objectives;

    public CompositeHardObjective(String name, List<IObjective> objectives) {
        super(name);
        Preconditions.checkNotNull(objectives, "The list of objectives cannot be null.");

        for (IObjective objective : objectives) {
            if (!objective.isHardObjective()) {
                throw new IllegalArgumentException("The objective " + objective.getName()
                        + " should be a hard objective.");
            }
        }

        this.objectives = objectives;
    }

    public CompositeHardObjective(List<IObjective> objectives) {
        this(DEFAULT_NAME, objectives);
    }

    public CompositeHardObjective(String name) {
        this(name, new ArrayList<IObjective>());
    }

    public CompositeHardObjective() {
        this(DEFAULT_NAME, new ArrayList<IObjective>());
    }

    /**
     * Adds a new hard objective.
     * 
     * @param objective
     * @return The actual instance to enable builder pattern like usage.
     */
    public CompositeHardObjective withObjective(IObjective objective) {
        if (!objective.isHardObjective()) {
            throw new IllegalArgumentException("The objective " + objective.getName() + " should be a hard objective.");
        }
        objectives.add(objective);
        return this;
    }

    @Override
    public Double getFitness(ThreadContext context) {

        for (IObjective objective : objectives) {
            Double fitness = objective.getFitness(context);
            if (!objective.satisifiesHardObjective(fitness)) {
                return 0d;
            }
        }
        return 1d;
    }

    @Override
    public void init(ThreadContext context) {
        for (IObjective objective : objectives) {
            objective.init(context);
        }
    }

    @Override
    public IObjective createNew() {

        List<IObjective> newObjectives = new ArrayList<IObjective>();

        for (IObjective objective : objectives) {
            newObjectives.add(objective.createNew());
        }

        return new CompositeHardObjective(name, newObjectives)
            .withComparator(comparator)
            .withLevel(level);
    }

    @Override
    public boolean isHardObjective() {
        return true;
    }

    @Override
    public boolean satisifiesHardObjective(Double fitness) {
        return fitness.doubleValue() > 0.5d;
    }

}
