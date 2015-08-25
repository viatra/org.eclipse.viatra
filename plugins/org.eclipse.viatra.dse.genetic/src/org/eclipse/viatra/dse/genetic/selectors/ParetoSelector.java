/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.genetic.selectors;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.interfaces.ISelectNextPopulation;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.objectives.ObjectiveComparatorHelper;

public class ParetoSelector implements ISelectNextPopulation {

    private int maxPopulationSize = 0;
    private Logger logger = Logger.getLogger(getClass());

    public ParetoSelector withMaxPopulationSize(int maxPopulationSize) {
        this.maxPopulationSize = maxPopulationSize;
        return this;
    }
    
    @Override
    public List<InstanceData> selectNextPopulation(Collection<InstanceData> currentPopulation,
            List<IObjective> objectives, int numberOfSelectedInstances, boolean finalSelection, ObjectiveComparatorHelper helper, 
            boolean calcCrowdingDistanceForEachFront) {

        LinkedList<LinkedList<InstanceData>> fronts = NonDominatedAndCrowdingDistanceSelector.nonDominatedSort(currentPopulation, objectives, helper, true);

        LinkedList<InstanceData> firstFront = fronts.getFirst();

        if (logger.getLevel() != null && logger.getLevel().equals(Level.DEBUG)) {
            StringBuilder sb = new StringBuilder();
            sb.append("First front:\n");
            for (InstanceData instance : firstFront) {
                sb.append("\t---\n");
                instance.prettyPrint(sb);
            }
            logger.debug(sb.toString());
        }

        if (maxPopulationSize > 0 && firstFront.size() > maxPopulationSize) {
            return firstFront.subList(0, maxPopulationSize);
        } else {
            return firstFront;
        }
    }

    @Override
    public boolean filtersDuplicates() {
        return false;
    }

}
