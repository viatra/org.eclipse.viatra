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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.interfaces.IParentSelector;

public class MyRoundRobinParentSelector implements IParentSelector {

    private List<InstanceData> result;
    private List<InstanceData> resultView;
    private List<InstanceData> parentPopulation;
    private Iterator<InstanceData> iterator;
    private Random rnd;

    @Override
    public void init(ThreadContext context) {
        result = new ArrayList<InstanceData>(2);
        result.add(null);
        result.add(null);
        resultView = Collections.unmodifiableList(result);
        rnd = new Random();
    }

    @Override
    public void initForPopulation(List<InstanceData> parentPopulation) {
        this.parentPopulation = parentPopulation;
        iterator = parentPopulation.iterator();
    }

    @Override
    public List<InstanceData> getNextParents(int numOfParents) {

        if (!iterator.hasNext()) {
            iterator = parentPopulation.iterator();
        }
        InstanceData parent1 = iterator.next();
        result.set(0, parent1);

        if (numOfParents == 1) {
            result.set(1, null);
        } else if (numOfParents == 2) {
            InstanceData parent2;
            do {
                parent2 = parentPopulation.get(rnd.nextInt(parentPopulation.size()));
            } while (parent1 == parent2);
            result.set(1, parent2);
        } else {
            throw new DSEException("Unsupported number of parents.");
        }

        return resultView;

    }

}
