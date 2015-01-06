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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.api.TransformationRule;
import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.api.IDesignSpaceManager;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.IState.TraversalStateType;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TrajectoryInfo;
import org.eclipse.viatra.dse.designspace.api.TransitionMetaData;
import org.eclipse.viatra.dse.guidance.IRuleApplicationChanger;
import org.eclipse.viatra.dse.guidance.IRuleApplicationNumberChanged;
import org.eclipse.viatra.dse.monitor.PerformanceMonitorManager;
import org.eclipse.viatra.dse.statecode.IStateSerializer;
import org.eclipse.viatra.dse.statecode.IStateSerializerFactory;
import org.eclipse.viatra.dse.visualizer.IExploreEventHandler;

public class DesignSpaceManager implements IDesignSpaceManager, IRuleApplicationChanger {

    private static final String EXECUTE = "execute";
    // ***** essential fields **********
    // the state serializer instance used to generate state and transition IDs
    private final IStateSerializer stateSerializer;
    private final IStateSerializerFactory serializerFactory;

    private final RuleEngine ruleEngine;

    // the editing domain encapsulating the working model
    private final EditingDomain domain;
    private EObject modelRoot;

    private final IDesignSpace designSpace;

    // **** other fields

    private final TrajectoryInfo trajectory;

    // the occurence vector callback
    private IRuleApplicationNumberChanged iRuleApplicationNumberChanged;
    private List<IExploreEventHandler> handlers;

    // Dummy context for evm
    private final Context evmContext = Context.create();

    private Logger logger = Logger.getLogger(this.getClass());

    private boolean isNewState = false;

    private static final long SLEEP_INTERVAL = 1;

    public DesignSpaceManager(EObject modelRoot, EditingDomain domain, IStateSerializerFactory factory,
            IDesignSpace designSpace, TrajectoryInfo trajectory, RuleEngine ruleEngine, IncQueryEngine engine) {
        checkNotNull(designSpace, "Cannot initialize crawler on a null design space!");
        checkNotNull(domain, "Cannot initialize crawler on a null editing domain!");
        checkNotNull(factory, "Cannot initialize crawler without a serializer factory!");

        this.modelRoot = modelRoot;
        this.ruleEngine = ruleEngine;
        this.designSpace = designSpace;
        this.domain = domain;
        this.serializerFactory = factory;

        // init serializer
        stateSerializer = factory.createStateSerializer(modelRoot);

        Object initialStateId = stateSerializer.serializeContainmentTree();
        isNewState = designSpace.addState(null, initialStateId, generateTransitions());
        IState rootState = designSpace.getStateById(initialStateId);

        if (rootState == null) {
            throw new DSEException("The root state should not be null under any condition!");
        }

        this.trajectory = new TrajectoryInfo(rootState, trajectory);

        logger.debug("DesignSpaceManager initialized with root (" + rootState.getId() + ")");
    }

    @Override
    public void fireActivation(final ITransition transition) {

        final Activation<?> activation = getActivationByTransitionId(transition);

        // assemble the new RecordingCommand to fire the Transition
        ChangeCommand rc = new ChangeCommand(modelRoot) {
            @Override
            protected void doExecute() {
                activation.fire(evmContext);
            }
        };

        IState previousState = trajectory.getCurrentState();

        // execute the command
        PerformanceMonitorManager.startTimer(EXECUTE);
        domain.getCommandStack().execute(rc);
        PerformanceMonitorManager.endTimer(EXECUTE);

        Object newStateId = stateSerializer.serializeContainmentTree();

        isNewState = designSpace.addState(transition, newStateId, generateTransitions());
        IState newState = designSpace.getStateById(newStateId);

        if (!isNewState) {
            if (newState == null) {
                throw new DSEException("It should not be possible that isNewState is false while newState is null!");
            }
            while (!newState.isProcessed()) {
                try {
                    Thread.sleep(SLEEP_INTERVAL);
                } catch (InterruptedException e) {
                }
            }
        }

        trajectory.addStep(transition);

        // maintain rule application number
        if (iRuleApplicationNumberChanged != null) {
            iRuleApplicationNumberChanged.increment(transition.getTransitionMetaData().rule, ruleEngine);
        }
        if (handlers != null) {
            for (IExploreEventHandler iExploreEventHandler : handlers) {
                iExploreEventHandler.transitionFired(transition);
            }
        }

        logger.debug("Fired Transition (" + transition.getId() + ") from " + previousState.getId() + " to " + newStateId);
    }

    public ITransition getTransitionByActivation(Activation<?> activation) {
        IPatternMatch match = (IPatternMatch) activation.getAtom();
        Object code = generateMatchCode(match);
        for (ITransition transition : trajectory.getCurrentState().getOutgoingTransitions()) {
            if (transition.getId().equals(code)) {
                return transition;
            }
        }
        return null;
    }

    public Activation<?> getActivationByTransitionId(ITransition transition) {
        for (Activation<?> act : ruleEngine.getConflictingActivations()) {
            IPatternMatch match = (IPatternMatch) act.getAtom();
            Object code = generateMatchCode(match);
            if (code.equals(transition.getId())) {
                return act;
            }
        }

        String errorMsg = "A retrieved Transition SHOULD have a matching Activation. Possible causes: the state serializer is faulty; the algorithm choosed a wrong Transition.";
        errorMsg += "\nSought transition: " + transition.getId();
        Object firedFromId = transition.getFiredFrom().getId();
        errorMsg += "\nTransition's source: " + firedFromId;
        Object currentStateId = getCurrentState().getId();
        errorMsg += "\nCurrent state: " + (currentStateId.equals(firedFromId) ? "same" : currentStateId);
        errorMsg += "\nAvailable transitions:";
        for (Activation<?> act : ruleEngine.getConflictingActivations()) {
            IPatternMatch match = (IPatternMatch) act.getAtom();
            Object code = generateMatchCode(match);
            errorMsg += "\n\t" + code;
        }

        throw new DSEException(errorMsg);
    }

    // ** API for strategy **//

    /**
     * Returns true if the given state is not owned by this crawler.
     * 
     **/

    @Override
    public boolean isNewModelStateAlreadyTraversed() {
        return !isNewState;
    }

    @Override
    public List<Object> getTrajectoryFromRoot() {
        return trajectory.getFullTransitionIdTrajectory();
    }

    @Override
    public List<Object> getTrajectoryFromRootAcyclic() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Object> getTrajectoryFromRootAcyclicShortest() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends ITransition> getTransitionsFromCurrentState() {
        return trajectory.getCurrentState().getOutgoingTransitions();
    }

    @Override
    public List<? extends ITransition> getUntraversedTransitionsFromCurrentState() {
        IState currentState = trajectory.getCurrentState();
        if (currentState.getTraversalState() != TraversalStateType.TRAVERSED) {
            return Collections.emptyList();
        }

        List<ITransition> immediatelyFireableTransitions = new ArrayList<ITransition>();
        for (ITransition transition : currentState.getOutgoingTransitions()) {
            if (!transition.isAssignedToFire()) {
                immediatelyFireableTransitions.add(transition);
            }
        }

        return immediatelyFireableTransitions;
    }

    @Override
    public List<? extends ITransition> getUntraversedTransitionsFromCurrentState(
            TransformationRule<? extends IPatternMatch> ruleFilter) {
        IState currentState = trajectory.getCurrentState();
        if (currentState.getTraversalState() != TraversalStateType.TRAVERSED) {
            return Collections.emptyList();
        }

        List<ITransition> immediatelyFireableTransitions = new ArrayList<ITransition>();
        for (ITransition transition : currentState.getOutgoingTransitions()) {
            if (!transition.isAssignedToFire() && transition.getTransitionMetaData().rule.equals(ruleFilter)) {
                immediatelyFireableTransitions.add(transition);
            }
        }

        return immediatelyFireableTransitions;
    }

    @Override
    public List<? extends ITransition> getTransitionsFromCurrentState(
            TransformationRule<? extends IPatternMatch> ruleFilter) {
        IState currentState = trajectory.getCurrentState();

        List<ITransition> transitions = new ArrayList<ITransition>();
        for (ITransition transition : currentState.getOutgoingTransitions()) {
            if (transition.getTransitionMetaData().rule.equals(ruleFilter)) {
                transitions.add(transition);
            }
        }

        return transitions;
    }

    @Override
    public List<? extends ITransition> getUntraversedTransitionsOnBackWay(int numOfStatesBack) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends ITransition> getUntraversedTransitionsWithMaximumDistanceOf(int distance) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    // ** API for moving in the design space**//

    @Override
    public boolean undoLastTransformation() {
        // check if it is valid to step back from here (you can't step back from
        // the crawler root)
        if (!trajectory.canStepBack()) {
            // return false indicating that the undo was not executed
            logger.debug("Failed undo request. Cannot undo.");
            return false;
        }

        // get the current state
        IState previousState = trajectory.getCurrentState();

        // we move the model by executing undo on the command stack
        domain.getCommandStack().undo();

        // save transition id
        ITransition lastTransition = trajectory.getLastTransition();

        trajectory.stepBack();

        // maintain rule application number
        if (iRuleApplicationNumberChanged != null) {
            iRuleApplicationNumberChanged.decrement(lastTransition.getTransitionMetaData().rule, ruleEngine);
        }
        if (handlers != null) {
            for (IExploreEventHandler iExploreEventHandler : handlers) {
                iExploreEventHandler.undo(lastTransition);
            }
        }

        logger.debug(
                "Successul undo from " + lastTransition.getResultsIn().getId() + " transition "
                        + lastTransition.getId() + " to " + lastTransition.getFiredFrom().getId());

        // return with true, indicating that we indeed executed a step back.
        return true;
    }

    private Object generateMatchCode(IPatternMatch match) {
        return stateSerializer.serializePatternMatch(match);
    }

    @Override
    public IState getCurrentState() {
        return trajectory.getCurrentState();
    }

    @Override
    public void setiRuleApplicationNumberChanged(IRuleApplicationNumberChanged iRuleApplicationNumberChanged) {
        this.iRuleApplicationNumberChanged = iRuleApplicationNumberChanged;
    }

    private Map<Object, TransitionMetaData> generateTransitions() {
        Map<Object, TransitionMetaData> transitions = new HashMap<Object, TransitionMetaData>();

        for (Activation<?> activation : ruleEngine.getConflictingActivations()) {

            // we ignore not fireable Activations. These shouldn't be here
            // anyway
            if (!activation.isEnabled()) {
                continue;
            }

            IPatternMatch match = (IPatternMatch) activation.getAtom();
            Object matchHash = generateMatchCode(match);

            @SuppressWarnings("unchecked")
            TransformationRule<? extends IPatternMatch> specification = (TransformationRule<? extends IPatternMatch>) activation
                    .getInstance().getSpecification();

            Map<String, Double> measureCosts = specification.measureCosts(match);

            TransitionMetaData transitionMetaData = new TransitionMetaData();
            transitionMetaData.rule = specification;
            transitionMetaData.costs = measureCosts;

            transitions.put(matchHash, transitionMetaData);
        }

        return transitions;
    }

    @Override
    public SolutionTrajectory createSolutionTrajectroy() {
        return trajectory.createSolutionTrajectory(serializerFactory);
    }

    @Override
    public TrajectoryInfo getTrajectoryInfo() {
        return trajectory;
    }

    @Override
    public void saveDesignSpace() {
        try {
            designSpace.saveDesignSpace("designSpace.txt");
        } catch (IOException e) {
            logger.error("Saving designspace failed", e);
        }
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

}
