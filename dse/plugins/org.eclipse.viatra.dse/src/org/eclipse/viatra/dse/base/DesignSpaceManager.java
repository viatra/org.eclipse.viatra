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
package org.eclipse.viatra.dse.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.objectives.ActivationFitnessProcessor;
import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.dse.visualizer.IExploreEventHandler;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Context;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.runtime.emf.rules.batch.BatchTransformationRule;

import com.google.common.collect.Iterators;

public class DesignSpaceManager {

    private final IStateCoder stateCoder;
    private final EditingDomain domain;
    private Notifier model;

    private IDesignSpace designSpace;

    private final TrajectoryInfo trajectory;

    // the occurence vector callback
    private List<IExploreEventHandler> handlers;

    // Dummy context for evm
    private final Context evmContext = Context.create();

    private Logger logger = Logger.getLogger(this.getClass());

    private boolean isNewState = false;
    private Map<BatchTransformationRule<?, ?>, ActivationFitnessProcessor> activationFitnessProcessors;
    private Map<BatchTransformationRule<?, ?>, String> activationFitnessProcessorNames;
    private ThreadContext context;

    private ActivationCodesConflictSet activationCodes;
    private ChangeableConflictSet conflictSet;
    
    private Random random = new Random();

    public DesignSpaceManager(ThreadContext context) {

        this.context = context;
        model = context.getModel();
        designSpace = context.getGlobalContext().getDesignSpace();
        domain = context.getEditingDomain();

        conflictSet = context.getConflictResolver().getLastCreatedConflictSet();

        stateCoder = context.getStateCoder();
        Object initialStateId = stateCoder.createStateCode();
        designSpace.addState(null, null, initialStateId);

        activationCodes = context.getActivationCodesConflictSet();

        this.trajectory = new TrajectoryInfo(initialStateId);

        logger.debug("DesignSpaceManager initialized with initial model: " + initialStateId);
    }

    public void fireActivation(final Object transition) {
        if (fireActivationSilent(transition)) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("A retrieved Transition SHOULD have a matching Activation. Possible causes: the state serializer is faulty; the algorithm choosed a wrong Transition.");
        sb.append("\nSought transition: ");
        sb.append(transition);
        Object currentStateId = getCurrentState();
        sb.append("\nCurrent known state: ");
        sb.append(currentStateId);
        Object actualStateId = stateCoder.createStateCode();
        sb.append("\nActual state: ");
        sb.append((actualStateId.equals(currentStateId) ? "same as current" : actualStateId));
        sb.append("\n");
        sb.append(trajectory);
        sb.append("\nAvailable transitions:");
        for (Activation<?> act : conflictSet.getNextActivations()) {
            IPatternMatch match = (IPatternMatch) act.getAtom();
            Object code = generateMatchCode(match);
            sb.append("\n\t");
            sb.append(code);
        }

        throw new DSEException(sb.toString());
    }

    public boolean tryFireActivation(final Object transition) {
        return fireActivationSilent(transition);
    }

    private boolean fireActivationSilent(final Object transition) {
        final Activation<?> activation = getActivationById(transition);

        if (activation == null) {
            return false;
        }

        BatchTransformationRule<?, ?> rule = getRuleByActivation(activation);

        Map<String, Double> measureCosts = null;
        if (activationFitnessProcessors != null && activationFitnessProcessors.containsKey(rule)) {
            IPatternMatch match = (IPatternMatch) activation.getAtom();
            ActivationFitnessProcessor processor = activationFitnessProcessors.get(rule);
            double fitness = processor.process(match);
            if (measureCosts == null) {
                measureCosts = new HashMap<String, Double>();
            }
            measureCosts.put(activationFitnessProcessorNames.get(rule), fitness);
        }

        ChangeCommand rc = new ChangeCommand(model) {
            @Override
            protected void doExecute() {
                activation.fire(evmContext);
            }
        };

        Object previousState = trajectory.getCurrentStateId();

        domain.getCommandStack().execute(rc);

        Object newStateId = stateCoder.createStateCode();
        activationCodes.updateActivationCodes();

        if (designSpace != null) {
            isNewState = !designSpace.isTraversed(newStateId);
            designSpace.addState(previousState, transition, newStateId);
        }

        trajectory.addStep(transition, rule, newStateId, measureCosts);

        if (handlers != null) {
            for (IExploreEventHandler iExploreEventHandler : handlers) {
                iExploreEventHandler.transitionFired(transition);
            }
        }

        logger.debug("Executed activation: " + transition);/* + " from state: " + previousState + " to "
                + newStateId);*/
        
        return true;
    }

    public boolean executeRandomActivationId() {
        Collection<Object> transitions = getTransitionsFromCurrentState();
        int size = transitions.size();
        if (size == 0) {
            return false;
        }

        int index = random.nextInt(size);
        Iterator<Object> iterator = transitions.iterator();
        for (int i = 0; i < index; ++i) {
            iterator.next();
        }
        Object transition = iterator.next();

        fireActivation(transition);
        return true;
    }

    public int executeTrajectory(Object[] trajectoryToExecute) {
        return executeTrajectory(trajectoryToExecute, 0, trajectoryToExecute.length, false, true);
    }

    public int executeTrajectory(Object[] trajectoryToExecute, int excludedIndex) {
        return executeTrajectory(trajectoryToExecute, 0, excludedIndex, false, true);
    }

    public int executeTrajectoryByTrying(Object[] trajectoryToExecute) {
        return executeTrajectory(trajectoryToExecute, 0, trajectoryToExecute.length, true, true);
    }

    public int executeTrajectoryByTrying(Object[] trajectoryToExecute, int excludedIndex) {
        return executeTrajectory(trajectoryToExecute, 0, excludedIndex, true, true);
    }

    public int executeTrajectoryWithoutStateCoding(Object[] trajectoryToExecute) {
        return executeTrajectory(trajectoryToExecute, 0, trajectoryToExecute.length, false, false);
    }

    public int executeTrajectoryWithoutStateCoding(Object[] trajectoryToExecute, int excludedIndex) {
        return executeTrajectory(trajectoryToExecute, 0, excludedIndex, false, false);
    }

    public int executeTrajectoryByTryingWithoutStateCoding(Object[] trajectoryToExecute) {
        return executeTrajectory(trajectoryToExecute, 0, trajectoryToExecute.length, true, false);
    }

    public int executeTrajectoryByTryingWithoutStateCoding(Object[] trajectoryToExecute, int excludedIndex) {
        return executeTrajectory(trajectoryToExecute, 0, excludedIndex, true, false);
    }

    private int executeTrajectory(Object[] trajectoryToExecute, int includedFromIndex, int excludedToIndex, boolean tryAllActivations, boolean createStateCode) {
        logger.debug("Executing trajectory.");
        int unsuccesfulIndex = -1;
        if (tryAllActivations) {
            unsuccesfulIndex = 0;
        }
        for (int i = includedFromIndex; i < excludedToIndex; i++) {
            Object activationId = trajectoryToExecute[i];
            final Activation<?> activation = getActivationById(activationId);

            if (activation == null) {
                logger.debug("Couldn't execute activation: " + activationId);
                if (tryAllActivations) {
                    unsuccesfulIndex++;
                    continue;
                } else {
                    unsuccesfulIndex = i;
                    logger.debug("Trajectory execution stopped at index " + i + "/" + trajectoryToExecute.length);
                    break;
                }
            }

            BatchTransformationRule<?, ?> rule = getRuleByActivation(activation);

            Map<String, Double> measureCosts = null;
            if (activationFitnessProcessors != null && activationFitnessProcessors.containsKey(rule)) {
                IPatternMatch match = (IPatternMatch) activation.getAtom();
                ActivationFitnessProcessor processor = activationFitnessProcessors.get(rule);
                double fitness = processor.process(match);
                if (measureCosts == null) {
                    measureCosts = new HashMap<String, Double>();
                }
                measureCosts.put(activationFitnessProcessorNames.get(rule), fitness);
            }

            ChangeCommand rc = new ChangeCommand(model) {
                @Override
                protected void doExecute() {
                    activation.fire(evmContext);
                }
            };
            domain.getCommandStack().execute(rc);

            Object newStateId = null;
            if (createStateCode) {
                newStateId = stateCoder.createStateCode();
            }
            activationCodes.updateActivationCodes();

            trajectory.addStep(activationId, rule, newStateId, measureCosts);

            logger.debug("Activation executed: " + activationId);
        }
        if (!createStateCode) {
            trajectory.modifyLastStateCode(stateCoder.createStateCode());
        }
        logger.debug("Trajectory execution finished.");
        return unsuccesfulIndex;

    }

    public Object getTransitionByActivation(Activation<?> activation) {
        return activationCodes.getActivationId(activation);
    }

    public Activation<?> getActivationById(Object activationId) {
        return activationCodes.getActivation(activationId);
    }
    
    public BatchTransformationRule<?, ?> getRuleByActivation(Activation<?> activation) {
        return context.getGlobalContext().getSpecificationRuleMap().get(activation.getInstance().getSpecification());
    }

    public BatchTransformationRule<?, ?> getRuleByActivationId(Object activationId) {
        return getRuleByActivation(getActivationById(activationId));
    }
    
    /**
     * Returns true if the given state is not owned by this crawler.
     * 
     **/
    public boolean isNewModelStateAlreadyTraversed() {
        return !isNewState;
    }

    public List<Object> getTrajectoryFromRoot() {
        return trajectory.getTrajectory();
    }

    public Collection<Object> getTransitionsFromCurrentState() {
        return activationCodes.getCurrentActivationCodes();
    }

    public Collection<Object> getUntraversedTransitionsFromCurrentState() {
        if (designSpace == null) {
            throw new DSEException("Unsupported without a design space");
        }
        Object currentState = trajectory.getCurrentStateId();
        Collection<Object> traversedIds = designSpace.getActivationIds(currentState);

        List<Object> untraversedTransitions = new ArrayList<>();
        for (Object activationId : activationCodes.getCurrentActivationCodes()) {
            if (!traversedIds.contains(activationId)) {
                untraversedTransitions.add(activationId);
            }
        }

        return untraversedTransitions;
    }

    public boolean undoLastTransformation() {

        if (!trajectory.canStepBack()) {
            return false;
        }

        domain.getCommandStack().undo();
        activationCodes.updateActivationCodes();
        
        Object lastActivationId = trajectory.getLastActivationId();

        trajectory.backtrack();

        if (handlers != null) {
            for (IExploreEventHandler iExploreEventHandler : handlers) {
                iExploreEventHandler.undo(lastActivationId);
            }
        }

        logger.debug("Backtrack.");

        return true;
    }

    public void backtrackXTimes(int steps) {
        for (int i = steps; i > 0; i--) {
            domain.getCommandStack().undo();
            trajectory.backtrack();
        }
        activationCodes.updateActivationCodes();
        logger.debug("Backtracked " + steps + " times.");
    }

    public int backtrackUntilLastCommonActivation(Object[] newTrajectory) {
        Iterator<Object> currentTrajectoryIterator = trajectory.getTrajectory().iterator();
        if (!currentTrajectoryIterator.hasNext()) {
            return 0;
        }
        int indexOfLastCommonActivation = 0;
        for (Object activationCode : newTrajectory) {
            if (currentTrajectoryIterator.hasNext()) {
                Object activationCodeFromCurrent = currentTrajectoryIterator.next();
                if (activationCodeFromCurrent.equals(activationCode)) {
                    indexOfLastCommonActivation++;
                } else {
                    break;
                }
            } else {
                // current trajectory is smaller
                break;
            }
        }
        int numberOfBacktracks = trajectory.getDepth() - indexOfLastCommonActivation;
        if (numberOfBacktracks > 0) {
            for (int i = numberOfBacktracks; i > 0; i--) {
                domain.getCommandStack().undo();
                trajectory.backtrack();
            }
            activationCodes.updateActivationCodes();
        }
        logger.debug("Backtracked " + numberOfBacktracks + " times.");
        return indexOfLastCommonActivation;
    }

    public void executeTrajectoryWithMinimalBacktrack(Object[] trajectory) {
        int fromIndex = backtrackUntilLastCommonActivation(trajectory);
        executeTrajectory(trajectory, fromIndex, trajectory.length, false, true);
    }

    public void executeTrajectoryWithMinimalBacktrackWithoutStateCoding(Object[] trajectory) {
        int fromIndex = backtrackUntilLastCommonActivation(trajectory);
        executeTrajectory(trajectory, fromIndex, trajectory.length, false, false);
        Object stateCode = stateCoder.createStateCode();
        this.trajectory.modifyLastStateCode(stateCode);
    }

    public void undoUntilRoot() {
        while(trajectory.canStepBack()) {
            domain.getCommandStack().undo();
            trajectory.backtrack();
        }
        activationCodes.updateActivationCodes();
        logger.debug("Backtracked to root.");
    }
    
    private Object generateMatchCode(IPatternMatch match) {
        return stateCoder.createActivationCode(match);
    }

    public Object getCurrentState() {
        return trajectory.getCurrentStateId();
    }

    private Activation<?> getActivationByIdFromConflictSet(Object soughtActivationId) {
        for (Activation<?> activation : conflictSet.getNextActivations()) {
            
            // we ignore not fireable Activations. These shouldn't be here
            // anyway TODO check if this code makes sense
            if (!activation.isEnabled()) {
                continue;
            }
            
            IPatternMatch match = (IPatternMatch) activation.getAtom();
            Object activationId = stateCoder.createActivationCode(match);
            if (activationId.equals(soughtActivationId)) {
                return activation;
            }
        }
        return null;
    }

    public SolutionTrajectory createSolutionTrajectroy() {
        return trajectory.createSolutionTrajectory(context.getGlobalContext().getStateCoderFactory());
    }

    public TrajectoryInfo getTrajectoryInfo() {
        return trajectory;
    }

    public void setDesignSpace(IDesignSpace designSpace) {
        this.designSpace = designSpace;
    }

    public IDesignSpace getDesignSpace() {
        return designSpace;
    }

    public void registerExploreEventHandler(IExploreEventHandler handler) {
        if (handler == null) {
            return;
        }
        if (handlers == null) {
            handlers = new ArrayList<IExploreEventHandler>();
        }
        handlers.add(handler);
    }

    public void deregisterExploreEventHandler(IExploreEventHandler handler) {
        if (handler == null) {
            return;
        }
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    public void registerActivationCostProcessor(String name, BatchTransformationRule<?, ?> rule,
            ActivationFitnessProcessor activationFitnessProcessor) {
        if (activationFitnessProcessors == null || activationFitnessProcessorNames == null) {
            activationFitnessProcessors = new HashMap<BatchTransformationRule<?, ?>, ActivationFitnessProcessor>();
            activationFitnessProcessorNames = new HashMap<BatchTransformationRule<?, ?>, String>();
        }
        activationFitnessProcessors.put(rule, activationFitnessProcessor);
        activationFitnessProcessorNames.put(rule, name);
    }

    public boolean isCurentStateInTrajectory() {
        Object currentStateId = trajectory.getCurrentStateId();
        List<Object> stateTrajectory = trajectory.getStateTrajectory();
        int size = stateTrajectory.size();
        for (int i = 0; i < size - 1; i++) {
            Object stateId = stateTrajectory.get(i);
            if (currentStateId.equals(stateId)) {
                return true;
            }
        }
        return false;
    }
    
    public IStateCoder getStateCoder() {
        return stateCoder;
    }

}
