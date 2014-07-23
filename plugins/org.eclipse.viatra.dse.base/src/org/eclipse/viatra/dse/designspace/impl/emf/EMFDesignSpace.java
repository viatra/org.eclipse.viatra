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
package org.eclipse.viatra.dse.designspace.impl.emf;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.dse.designspace.api.IDesignSpace;
import org.eclipse.viatra.dse.designspace.api.IDesignSpaceChangeHandler;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.designspace.api.TransitionMetaData;
import org.eclipse.viatra.dse.emf.designspace.DesignSpace;
import org.eclipse.viatra.dse.emf.designspace.EMFDesignSpaceFactory;
import org.eclipse.viatra.dse.emf.designspace.State;
import org.eclipse.viatra.dse.emf.designspace.Transition;


public class EMFDesignSpace implements IDesignSpace {

    private static final int CACHE_SIZE = 1000;

    private final ConcurrentHashMap<Object, EMFThreadsafeState> objectToStateMap = new ConcurrentHashMap<Object, EMFThreadsafeState>();
    private final ConcurrentHashMap<Object, EMFThreadsafeState> cache = new ConcurrentHashMap<Object, EMFThreadsafeState>();
    private final AtomicLong numberOfStates = new AtomicLong(0);
    private final AtomicLong numberOfTransitions = new AtomicLong(0);

    private final Logger logger = Logger.getLogger(this.getClass());

    private DesignSpace ds = EMFDesignSpaceFactory.eINSTANCE.createDesignSpace();

    @Override
    public synchronized IState[] getRoot() {
        IState[] result = new IState[ds.getRootStates().size()];
        return ds.getRootStates().toArray(result);
    }

    @Override
    public synchronized void addRoot(IState root) {
        ds.getRootStates().add((State) root);
    }

    @Override
    public boolean addState(ITransition sourceTransition, Object newStateId,
            Map<Object, TransitionMetaData> outgoingTransitionIds) {

        // Is the state exists? if yes then return false
        EMFThreadsafeState state = getStateById(newStateId);
        EMFThreadsafeTransition internalSourceTransition = (EMFThreadsafeTransition) sourceTransition;
        if (state != null) {
            // set transition's results in
            if (sourceTransition != null) {
                if (state.getIncomingTransitions().contains(sourceTransition)) {
                    return false;
                }
                state.getIncomingTransitions().add(internalSourceTransition);
                sourceTransition.setResultsIn(state);
                logger.debug(" " + sourceTransition.getFiredFrom().getId() + ":" + sourceTransition.getId()
                        + " points to " + newStateId + " object id:" + state);
            }
            return false;
        }

        // There can be a race to create the new state

        // create new state
        state = new EMFThreadsafeState(EMFDesignSpaceFactory.eINSTANCE.createState(), newStateId);
        logger.debug(" Created state with id " + newStateId + " " + state);

        // set outgoing transitions
        for (Object transitionId : outgoingTransitionIds.keySet()) {
            Transition outTransition = EMFDesignSpaceFactory.eINSTANCE.createTransition();
            outTransition.setRuleData(outgoingTransitionIds.get(transitionId));
            EMFThreadsafeTransition transition = new EMFThreadsafeTransition(state.getState(), transitionId,
                    outTransition);
            logger.debug(" State with id " + state.getId() + " received new out transition " + transition.getId() + " "
                    + state);
        }

        // set incoming transition or root state
        if (sourceTransition != null) {
            // if this is a normal "step" from one state to an other
            state.getIncomingTransitions().add(internalSourceTransition);
            logger.debug(" State with id " + state.getId() + " received new in transition " + sourceTransition.getId()
                    + " " + state);
        } else {
            addRoot(state);
            logger.debug(" Root has been elected with id " + state.getId() + " " + state);
            state.setProcessed();
        }

        // save new state
        EMFThreadsafeState elderState = objectToStateMap.putIfAbsent(newStateId, state);
        if (elderState == null) {
            // thread won the state creation
            logger.debug(" Won the state creation race (if there was any) for id " + state.getId() + " " + state);
            if (sourceTransition != null) {
                sourceTransition.setResultsIn(state);
            }
            cache.put(newStateId, state);
            numberOfStates.incrementAndGet();
            numberOfTransitions.addAndGet(outgoingTransitionIds.size());
            synchronized (ds.getStates()) {
                ds.getStates().add(state.getState());
            }

            for (Transition t : state.getState().getOutTransitions()) {
                ds.getTransitions().add(t);
            }
            return true;
        } else {
            // thread lost the state creation
            logger.debug(" LOST the state creation race for id " + state.getId() + " " + state);
            if (sourceTransition != null) {
                sourceTransition.setResultsIn(elderState);
                elderState.getIncomingTransitions().add(internalSourceTransition);
            }
            return false;
        }
    }

    @Override
    public EMFThreadsafeState getStateById(Object id) {
        EMFThreadsafeState state = cache.get(id);
        if (state == null) {
            state = objectToStateMap.get(id);
            if (state == null) {
                return null;
            }
            // refresh cache
            if (cache.size() > CACHE_SIZE) {
                cache.clear();
            }
            cache.put(id, state);
        }
        return state;
    }

    @Override
    public long getNumberOfStates() {
        return numberOfStates.get();
    }

    @Override
    public long getNumberOfTransitions() {
        return numberOfTransitions.get();
    }

    @Override
    public void saveDesignSpace(String fileName) throws IOException {
        final String extension = ".emfds";

        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put(extension, new XMIResourceFactoryImpl());

        // Obtain a new resource set
        ResourceSet resSet = new ResourceSetImpl();

        // Create a resource
        Resource resource = resSet.createResource(URI.createURI(fileName + extension));
        // Get the first model element and cast it to the right type, in my
        // example everything is hierarchical included in this first node
        resource.getContents().add(ds);

        // Now save the content.
        try {
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void addDesignSpaceChangedListener(IDesignSpaceChangeHandler changeEvent) {

    }

    @Override
    public void removeDesignSpaceChangedListener(IDesignSpaceChangeHandler changeEvent) {

    }

}
