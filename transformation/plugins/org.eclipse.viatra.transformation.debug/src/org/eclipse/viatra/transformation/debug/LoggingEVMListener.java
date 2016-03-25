/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.debug;

import org.apache.log4j.Logger;
import org.eclipse.viatra.transformation.evm.api.Activation;
import org.eclipse.viatra.transformation.evm.api.RuleSpecification;
import org.eclipse.viatra.transformation.evm.api.adapter.IEVMListener;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.event.EventType;

/**
 * {@link IEVMListener} implementation that provides basic, preliminary logging
 * 
 * @author Peter Lunk
 *
 */
public class LoggingEVMListener implements IEVMListener {
    private final Logger logger;

    public LoggingEVMListener(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void initializeListener() {
        logger.debug("ADAPTER INIT");

    }

    @Override
    public void beforeFiring(Activation<?> activation) {
        logger.debug("BEFORE FIRING " + activation.toString());

    }

    @Override
    public void afterFiring(Activation<?> activation) {
        logger.debug("AFTER FIRING " + activation.toString());

    }

    @Override
    public void startTransaction(String transactionID) {
        logger.debug("START TRANSACTION: " + transactionID);
    }

    @Override
    public void endTransaction(String transactionID) {
        logger.debug("END TRANSACTION: " + transactionID);
    }

    @Override
    public void activationChanged(Activation<?> activation, ActivationState oldState, EventType event) {
        logger.debug("ACTIVATION CHANGED " + oldState.toString() + "-->" + event.toString());

    }

    @Override
    public void activationCreated(Activation<?> activation, ActivationState inactiveState) {
        logger.debug("ACTIVATION CREATED " + activation.toString());

    }

    @Override
    public void activationRemoved(Activation<?> activation, ActivationState oldState) {
        logger.debug("ACTIVATION CREATED " + activation.toString() + " " + oldState.toString());

    }

    @Override
    public void addedRule(RuleSpecification<?> specification) {
        logger.debug("ADDED RULE " + specification.toString());

    }

    @Override
    public void removedRule(RuleSpecification<?> specification) {
        logger.debug("REMOVED RULE " + specification.toString());

    }

    @Override
    public void disposeListener() {
        logger.debug("ADAPTER DISPOSE");

    }

}
