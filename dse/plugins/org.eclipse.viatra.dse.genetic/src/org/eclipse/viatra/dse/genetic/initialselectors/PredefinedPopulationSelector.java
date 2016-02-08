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
package org.eclipse.viatra.dse.genetic.initialselectors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;
import org.eclipse.viatra.dse.genetic.interfaces.InitialPopulationSelector;
import org.eclipse.viatra.dse.objectives.Fitness;

public class PredefinedPopulationSelector extends InitialPopulationSelector {

    private DesignSpaceManager dsm;

    private IStoreChild store;

    private boolean isInterrupted = false;

    private int trajectoryIndex = 0;
    private int activationIndex = 0;
    private boolean silentFail = false;

    private List<List<Object>> trajectories;

    private ThreadContext context;

    private Logger logger = Logger.getLogger(getClass());
    
    public PredefinedPopulationSelector(List<List<Object>> trajectories) {
        if (trajectories == null || trajectories.isEmpty()) {
            throw new DSEException(getClass().getSimpleName() + " was initilaized with "
                    + (trajectories == null ? "NULL" : "empty") + " list of trajectories.");
        }
        this.trajectories = trajectories;
    }

    public PredefinedPopulationSelector(String fileName) throws IOException {
        
        trajectories = new ArrayList<List<Object>>();
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));

            for (String line; (line = br.readLine()) != null && !line.isEmpty();) {
                String[] values = line.split("\t");
                List<Object> trajectory = new ArrayList<Object>(values.length);
                for (int index = 0; index < values.length; index++) {
                    trajectory.add(values[index]);
                }
                trajectories.add(trajectory);
            }

        } catch (IOException e) {
            logger.error("Failed to read " + fileName + " file.",e);
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
        
    }

    @Override
    public void setChildStore(IStoreChild store) {
        this.store = store;
    }

    @Override
    public void init(ThreadContext context) {
        this.context = context;
        if (store == null) {
            throw new DSEException("No IStoreChild is set for the Selector");
        }
        dsm = context.getDesignSpaceManager();
    }

    @Override
    public ITransition getNextTransition(boolean lastWasSuccessful) {

        if (isInterrupted) {
            return null;
        }

        List<Object> trajectory = trajectories.get(trajectoryIndex);

        mainloop: while (true) {

            if (activationIndex >= trajectory.size()) {
                store.addChild(context);
                activationIndex = 0;
                trajectoryIndex++;
                if (trajectoryIndex >= trajectories.size()) {
                    return null;
                }
                trajectory = trajectories.get(trajectoryIndex);
                while (dsm.undoLastTransformation()) {
                }
            }

            Object activationId = trajectory.get(activationIndex);

            Collection<? extends ITransition> transitions = dsm.getTransitionsFromCurrentState();

            for (ITransition transition : transitions) {
                if (activationId.equals(transition.getId())) {

                    activationIndex++;
                    if (transition.isAssignedToFire()) {
                        dsm.fireActivation(transition);
                        continue mainloop;
                    } else {
                        return transition;
                    }

                }
            }

        }

    }

    @Override
    public void newStateIsProcessed(boolean isAlreadyTraversed, Fitness objectives, boolean constraintsNotSatisfied) {
    }

    @Override
    public void setPopulationSize(int populationSize) {
        if (populationSize > trajectories.size()) {
            throw new DSEException("Not enough trajectories in the predefined population.");
        }
    }

    @Override
    public void interrupted() {
        isInterrupted = true;
    }

    public boolean isSilentFail() {
        return silentFail;
    }

    public void setSilentFail(boolean silentFail) {
        this.silentFail = silentFail;
    }

}
