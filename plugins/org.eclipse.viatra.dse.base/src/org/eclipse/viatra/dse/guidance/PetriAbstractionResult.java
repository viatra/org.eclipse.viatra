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
package org.eclipse.viatra.dse.guidance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra.dse.api.TransformationRule;

public final class PetriAbstractionResult {

    List<PetriAbstractionSolution> solutions = new ArrayList<PetriAbstractionSolution>();

    public void addSolution(Map<TransformationRule<? extends IPatternMatch>, Integer> occurrence,
            List<TransformationRule<? extends IPatternMatch>> trajectory) {
        solutions.add(new PetriAbstractionSolution(occurrence, trajectory));
    }

    public List<PetriAbstractionSolution> getSolutions() {
        return solutions;
    }
}
