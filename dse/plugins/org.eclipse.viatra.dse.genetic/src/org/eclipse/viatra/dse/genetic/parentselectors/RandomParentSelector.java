/*******************************************************************************
 * Copyright (c) 2010-2015, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.genetic.parentselectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.interfaces.IParentSelector;

public class RandomParentSelector implements IParentSelector {

    private List<InstanceData> result;
    private List<InstanceData> resultView;
    private List<InstanceData> parentPopulation;
    private Random rnd;

    @Override
    public void init(ThreadContext context) {
        result = new ArrayList<InstanceData>(4);
        for (int i = 0; i < 4; i++) {
            result.add(null);
        }
        resultView = Collections.unmodifiableList(result);
        rnd = new Random();
    }

    @Override
    public void initForPopulation(List<InstanceData> parentPopulation) {
        this.parentPopulation = parentPopulation;
    }

    @Override
    public List<InstanceData> getNextParents(int numOfParents) {

        for (int i = 0; i < 4; i++) {
            if (i < numOfParents) {
                InstanceData parent1 = parentPopulation.get(rnd.nextInt(parentPopulation.size()));
                result.set(i, parent1);
            } else {
                result.set(i, null);
            }
        }

        return resultView;
    }

}
