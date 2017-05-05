/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *   Peter Lunk - revised EVM structure for adapter support
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.api;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictSetUpdater;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;
import org.eclipse.viatra.transformation.evm.specific.resolver.ArbitraryOrderConflictResolver;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Sole purpose is the management all and ordering of enabled activations!
 *
 * @author Abel Hegedus, Peter Lunk
 *
 */
public class Agenda {

    private final Multimap<ActivationState, Activation<?>> activations;
    private ChangeableConflictSet conflictSet;
    private IActivationNotificationListener activationListener;
    private ConflictSetUpdater updatingListener;
    private final Logger logger;
    
    
    /**
    *
    */
    public Agenda() {
        this(new ArbitraryOrderConflictResolver());
    }
    
    
    /**
     *
     */
    public Agenda(final ConflictResolver conflictResolver) {
        this.logger = Logger.getLogger(this.toString());
        this.activations = HashMultimap.create();
        this.conflictSet = conflictResolver.createConflictSet();
        this.updatingListener = new ConflictSetUpdater(conflictSet);
        this.setActivationListener(new DefaultActivationNotificationListener(this));
    }
    
    /**
    *
    */
    public Agenda(final ConflictResolver conflictResolver, IActivationNotificationListener activationListener) {
        this.logger = Logger.getLogger(this.toString());
        this.setActivationListener(activationListener);
        Preconditions.checkState(this.getActivationListener() != null, "Activation Listener is null!");
        this.activations = HashMultimap.create();
        this.conflictSet = conflictResolver.createConflictSet();
        this.updatingListener = new ConflictSetUpdater(conflictSet);
       
   }

    public Multimap<ActivationState, Activation<?>> getActivations() {
        return activations;
    }

    /**
     * Returns the activations that are in the given state
     *
     * @param state
     * @return the activations in the given state
     */
    public Collection<Activation<?>> getActivations(final ActivationState state) {
        return getActivations().get(state);
    }

    public Collection<Activation<?>> getAllActivations() {
        return getActivations().values();
    }

    public IActivationNotificationListener getActivationListener() {
        return activationListener;
    }

    public void setConflictResolver(final ConflictResolver resolver) {
        final ChangeableConflictSet set = resolver.createConflictSet();
        for (final Activation<?> act : conflictSet.getConflictingActivations()) {
            set.addActivation(act);
        }
        updatingListener = new ConflictSetUpdater(set);
        this.conflictSet = set;
    }
    
    public void setActivationListener(IActivationNotificationListener activationListener) {
        this.activationListener = activationListener;
    }

    public ChangeableConflictSet getConflictSet() {
        return conflictSet;
    }
    

    protected Logger getLogger() {
        return logger;
    }
    
    protected ConflictSetUpdater getConflictSetUpdater() {
        return updatingListener;
    }
}

