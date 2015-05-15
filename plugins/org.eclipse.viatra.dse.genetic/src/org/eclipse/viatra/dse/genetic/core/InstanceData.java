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
package org.eclipse.viatra.dse.genetic.core;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.viatra.dse.designspace.api.ITransition;

public class InstanceData {

    public volatile List<ITransition> trajectory;

    public volatile int rank;
    public volatile double crowdingDistance;

    public volatile double sumOfConstraintViolationMeauserement = 0;
    public volatile Map<String, Integer> violations = new HashMap<String, Integer>();
    public volatile Map<String, Double> objectives;

    public int survive = 0;

    public InstanceData(List<ITransition> initialTrajectory) {
        trajectory = initialTrajectory;
    }

    public Double getFitnessValue(String m) {
        return objectives.get(m);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            for (ITransition t : trajectory) {
                sb.append(t.getId() + ", ");
            }
        } catch (ConcurrentModificationException e) {
        }
        return sb.toString();
    }

    public StringBuilder prettyPrint(StringBuilder sb) {
        sb.append("\tState: " + trajectory.get(trajectory.size() - 1).getResultsIn().getId());
        sb.append("\n\tTrajectory (" + trajectory.size() + " long): ");
        for (ITransition iTransition : trajectory) {
            sb.append(iTransition.getId() + "; ");
        }
        sb.append("\n\tviolations: " + sumOfConstraintViolationMeauserement + "; ");
        for (String objective : objectives.keySet()) {
            sb.append(objective + ": " + objectives.get(objective) + "; ");
        }
        return sb;
    }

    public String prettyPrint() {
        return prettyPrint(new StringBuilder()).toString();
    }
}
