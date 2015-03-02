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
 * This soft objective collects a list of other soft objectives. It returns the weighted sum of the objectives.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class CompositSoftObjective extends BaseObjective {

    public static final String DEFAULT_NAME = "CompositSoftObjective";
    protected List<IObjective> objectives;

    public CompositSoftObjective(String name, List<IObjective> objectives) {
        super(name);
        Preconditions.checkNotNull(objectives, "The list of objectives cannot be null.");

        for (IObjective objective : objectives) {
            if (objective.isHardObjective()) {
                throw new IllegalArgumentException("The objective " + objective.getName()
                        + " should be a soft objective.");
            }
        }

        this.objectives = objectives;
    }

    public CompositSoftObjective(List<IObjective> objectives) {
        this(DEFAULT_NAME, objectives);
    }

    public CompositSoftObjective(String name) {
        this(name, new ArrayList<IObjective>());
    }

    public CompositSoftObjective() {
        this(DEFAULT_NAME, new ArrayList<IObjective>());
    }

    /**
     * Adds a new soft objective.
     * 
     * @param objective
     * @return The actual instance to enable builder pattern like usage.
     */
    public CompositSoftObjective withObjective(IObjective objective) {
        if (!objective.isHardObjective()) {
            throw new IllegalArgumentException("The objective " + objective.getName() + " should be a soft objective.");
        }
        objectives.add(objective);
        return this;
    }

    @Override
    public Double getFitness(ThreadContext context) {

        double result = 0;

        for (IObjective objective : objectives) {
            result += objective.getFitness(context);
        }
        return result;
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

        return new CompositSoftObjective(name, newObjectives)
            .withComparator(comparator)
            .withLevel(level);
    }

    @Override
    public boolean isHardObjective() {
        return false;
    }

    @Override
    public boolean satisifiesHardObjective(Double fitness) {
        return true;
    }

}
