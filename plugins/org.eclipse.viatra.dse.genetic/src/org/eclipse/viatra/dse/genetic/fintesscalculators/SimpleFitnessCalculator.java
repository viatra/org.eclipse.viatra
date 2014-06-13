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
package org.eclipse.viatra.dse.genetic.fintesscalculators;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.core.GeneticSharedObject;
import org.eclipse.viatra.dse.genetic.core.InstanceData;
import org.eclipse.viatra.dse.genetic.core.SoftConstraint;
import org.eclipse.viatra.dse.genetic.interfaces.IFitnessCalculator;

public class SimpleFitnessCalculator implements IFitnessCalculator {

    @Override
    public void calculateFitness(GeneticSharedObject sharedObject, ThreadContext context, InstanceData instance) {

        // Calculate violations
        instance.sumOfConstraintViolationMeauserement = 0;
        IncQueryEngine incqueryEngine = context.getIncqueryEngine();
        instance.violations = new HashMap<String, Integer>();
        for (SoftConstraint softConstraint : sharedObject.softConstraints) {
            instance.violations.put(softConstraint.getName(), softConstraint.getNumberOfMatches(incqueryEngine));
            instance.sumOfConstraintViolationMeauserement += softConstraint.getViolationMeasurement(incqueryEngine);
        }

        // Calculate trajectory objectives
        Map<String, Double> objectives = new HashMap<String, Double>();
        for (String objective : sharedObject.comparators.keySet()) {
            objectives.put(objective, 0d);
        }
        for (ITransition transition : instance.trajectory) {
            Map<String, Double> activationCosts = transition.getTransitionMetaData().costs;
            Map<String, Double> ruleCosts = transition.getTransitionMetaData().rule.getCosts();
            addMaps(objectives, activationCosts);
            addMaps(objectives, ruleCosts);
        }
        Map<String, Double> modelObjectiveValues = sharedObject.modelObjectivesCalculator.calculate(context);
        addMaps(objectives, modelObjectiveValues);

        Logger logger = context.getDesignSpaceManager().getLogger();
        logger.debug(objectives.toString() + " violations: " + instance.sumOfConstraintViolationMeauserement);

        instance.objectives = objectives;
    }

    private void addMaps(Map<String, Double> baseMap, Map<String, Double> addMap) {
        if (addMap != null) {
            for (String key : addMap.keySet()) {
                Double d = baseMap.get(key);
                if (d == null) {
                    throw new DSEException("Objective comparator for '" + key + "' is missing.");
                }
                baseMap.put(key, d + addMap.get(key));
            }
        }
    }
}
