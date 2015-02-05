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
import java.util.Comparator;
import java.util.List;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.objectives.Comparators;
import org.eclipse.viatra.dse.objectives.IObjective;

import com.google.common.base.Preconditions;

/**
 * This hard objective collects a list of other hard objectives and checks if any of them is unsatisfied. In such a
 * case, it returns 0. If all of them is satisfied it returns 1.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class CompositHardObjective implements IObjective {

    private static final String DEFAULT_NAME = "CompositHardObjective";

    protected String name;

    protected List<IObjective> objectives;

    protected Comparator<Double> comparator = Comparators.BIGGER_IS_BETTER;

    public CompositHardObjective(String name, List<IObjective> objectives) {
        Preconditions.checkNotNull(name, "Name of the objective cannot be null.");
        Preconditions.checkNotNull(objectives, "The list of objectives cannot be null.");

        for (IObjective objective : objectives) {
            if (!objective.isHardObjective()) {
                throw new IllegalArgumentException("The objective " + objective.getName()
                        + " should be a hard objective.");
            }
        }

        this.name = name;
        this.objectives = objectives;
    }

    public CompositHardObjective(List<IObjective> objectives) {
        this(DEFAULT_NAME, objectives);
    }

    public CompositHardObjective(String name) {
        this(name, new ArrayList<IObjective>());
    }

    public CompositHardObjective() {
        this(DEFAULT_NAME, new ArrayList<IObjective>());
    }

    public void setComparator(Comparator<Double> comparator) {
        this.comparator = comparator;
    }

    /**
     * Adds a new hard objective.
     * 
     * @param objective
     * @return The actual instance to enable builder pattern like usage.
     */
    public CompositHardObjective addObjective(IObjective objective) {
        if (!objective.isHardObjective()) {
            throw new IllegalArgumentException("The objective " + objective.getName() + " should be a hard objective.");
        }
        objectives.add(objective);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Comparator<Double> getComparator() {
        return comparator;
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

        CompositHardObjective hardObjectiveCopy = new CompositHardObjective(name, newObjectives);
        hardObjectiveCopy.setComparator(comparator);

        return hardObjectiveCopy;
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
