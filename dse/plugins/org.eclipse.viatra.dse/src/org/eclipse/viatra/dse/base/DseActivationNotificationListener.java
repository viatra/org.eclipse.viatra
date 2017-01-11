/*******************************************************************************
 * Copyright (c) 2010-2016, Andras Szabolcs Nagy and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.base;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.Agenda;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * This class is responsible for maintaining an <i>activation - activation code</i> bidirectional map incrementally.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class DseActivationNotificationListener implements IActivationNotificationListener {

    protected IActivationNotificationListener defaultActivationListener;
    protected BiMap<Activation<?>, Object> activationIds;
    protected IStateCoder stateCoder;

    protected Set<Activation<?>> newActivations = new HashSet<>();
    protected Set<Activation<?>> removedActivations = new HashSet<>();
    private ChangeableConflictSet conflictSet;
//    private Logger logger = Logger.getLogger(getClass());

    private static boolean isIncremental = false;

    public static void setIncremental(boolean isIncremental) {
        DseActivationNotificationListener.isIncremental = isIncremental;
    }

    public DseActivationNotificationListener(Agenda agenda, IStateCoder stateCoder) {
        defaultActivationListener = agenda.getActivationListener();
        conflictSet = agenda.getConflictSet();
        activationIds = HashBiMap.create();
        this.stateCoder = stateCoder;
    }

    private Object createActivationCode(Activation<?> activation) {
        return stateCoder.createActivationCode((IPatternMatch) activation.getAtom());
    }

    @Override
    public void activationRemoved(Activation<?> activation, ActivationState oldState) {
        defaultActivationListener.activationRemoved(activation, oldState);

        if (isIncremental) {
//*
            removedActivations.add(activation);
            newActivations.remove(activation);
/*/
            if(!removedActivations.add(activation)) {
                logger.debug("Abnormal: already marked to remove: " + activation);
            } else {
                logger.debug("marked to remove: " + activation);
            }
            if(newActivations.remove(activation)) {
                logger.debug("Abnormal: removed from new activations: " + activation);
            }
//*/
        }
    }

    @Override
    public void activationCreated(Activation<?> activation, ActivationState inactiveState) {
        defaultActivationListener.activationCreated(activation, inactiveState);
        if (isIncremental) {
//*
            newActivations.add(activation);
            removedActivations.remove(activation);
            /*/
            if (activation.isEnabled()) {
                if (!newActivations.add(activation)) {
                    logger.debug("Abnormal: already added as new: " + activation);
                } else {
                    logger.debug("activation added: " + activation);
                }
            }
            if(removedActivations.remove(activation)) {
                logger.debug("Abnormal: was already marked to remove: " + activation);
            }
//*/
        }
    }

    @Override
    public void activationChanged(Activation<?> activation, ActivationState oldState, EventType event) {
        defaultActivationListener.activationChanged(activation, oldState, event);
        if (isIncremental) {
            if (activation.getState().isInactive()) {
                removedActivations.add(activation);
                newActivations.remove(activation);
//            logger.debug("Removed as became inactive: " + activation);
            }
        }
    }

    public Object getActivationId(Activation<?> activation) {
        return activationIds.get(activation);
    }

    public Activation<?> getActivation(Object activationId) {
        return activationIds.inverse().get(activationId);
    }

    public BiMap<Activation<?>, Object> getActivationIds() {
        return activationIds;
    }
    
    public void updateActivationCodes() {
//        logger.debug("Updating activation codes.");

        if (isIncremental) {
          for (Activation<?> activation : removedActivations) {
              activationIds.remove(activation);
//              logger.debug("removed activation: " + activationId);
          }
    
          for (Activation<?> activation : newActivations) {
              if (activation.getState().isInactive()) {
                  continue;
              }
              Object activationId = createActivationCode(activation);
              activationIds.forcePut(activation, activationId);
//              logger.debug("new activation: " + activationId);
//              Activation<?> similarActivation = activationIds.inverse().get(activationId);
//              if (similarActivation != null) {
//                  logger.debug("Activation " + toStringAct(activation) + " is already present with id: " + activationId);
//                  if (similarActivation.isEnabled()) {
//                      logger.warn("Duplicate activation code: " + activationId);
//                  } else {
//                      logger.debug("Force put: " + activationId);
//                  }
//                  continue;
//              }
//              activationIds.put(activation, activationId);
          }
          removedActivations.clear();
          newActivations.clear();
        } else {
            activationIds.clear();
            for (Activation<?> activation : conflictSet.getConflictingActivations()) {
                Object activationCode = createActivationCode(activation);
                activationIds.forcePut(activation, activationCode);
            }
        }


    }
    
}
