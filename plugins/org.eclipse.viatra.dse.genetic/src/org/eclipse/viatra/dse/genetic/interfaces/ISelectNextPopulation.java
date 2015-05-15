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
package org.eclipse.viatra.dse.genetic.interfaces;

import java.util.Collection;
import java.util.List;

import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.objectives.IObjective;
import org.eclipse.viatra.dse.objectives.ObjectiveComparatorHelper;

public interface ISelectNextPopulation {

    List<InstanceData> selectNextPopulation(Collection<InstanceData> currentPopulation,
            List<IObjective> objectives, int numberOfSelectedInstances, boolean finalSelection, ObjectiveComparatorHelper helper);

    boolean filtersDuplicates();

}
