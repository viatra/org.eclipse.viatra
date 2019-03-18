/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.dse.evolutionary.stopconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.evolutionary.interfaces.IStopCondition;
import org.eclipse.viatra.dse.objectives.TrajectoryFitness;

public class CompositeStopCondition implements IStopCondition {

    public enum CompositeType {
        AND, OR
    }

    private List<IStopCondition> stopConditions = new ArrayList<>(2);
    private CompositeType type = CompositeType.AND;

    public CompositeStopCondition() {
    }

    public CompositeStopCondition(IStopCondition stopCondition1, IStopCondition stopCondition2) {
        stopConditions.add(stopCondition1);
        stopConditions.add(stopCondition2);
    }

    public void addStopCondition(IStopCondition stopCondition) {
        stopConditions.add(stopCondition);
    }

    public CompositeStopCondition withStopCondition(IStopCondition stopCondition) {
        stopConditions.add(stopCondition);
        return this;
    }

    public void setCompositeType(CompositeType type) {
        this.type = type;
    }

    public CompositeStopCondition withCompositeType(CompositeType type) {
        this.type = type;
        return this;
    }

    @Override
    public void init(ThreadContext context) {
        for (IStopCondition stopCondition : stopConditions) {
            stopCondition.init(context);
        }
    }

    @Override
    public boolean checkStopCondition(Collection<TrajectoryFitness> survivedPopulation) {
        if (type.equals(CompositeType.AND)) {
            for (IStopCondition stopCondition : stopConditions) {
                if (!stopCondition.checkStopCondition(survivedPopulation)) {
                    return false;
                }
            }
            return true;
        } else {
            for (IStopCondition stopCondition : stopConditions) {
                if (stopCondition.checkStopCondition(survivedPopulation)) {
                    return true;
                }
            }
            return false;
        }
    }

}
