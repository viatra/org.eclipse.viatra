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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;
import org.eclipse.viatra.transformation.evm.api.event.ActivationState;
import org.eclipse.viatra.transformation.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictResolver;
import org.eclipse.viatra.transformation.evm.api.resolver.ConflictSetUpdater;
import org.eclipse.viatra.transformation.evm.notification.IActivationNotificationListener;
import org.eclipse.viatra.transformation.evm.specific.resolver.ArbitraryOrderConflictResolver;

/**
 * Sole purpose is the management all and ordering of enabled activations!
 *
 * @author Abel Hegedus, Peter Lunk
 *
 */
public class Agenda {

    private final Map<ActivationState, Set<Activation<?>>> activations;
    private ChangeableConflictSet conflictSet;
    private IActivationNotificationListener activationListener;
    private ConflictSetUpdater updatingListener;
    private final Logger logger;
    
    
    public Agenda() {
        this(new ArbitraryOrderConflictResolver());
    }
    
    public Agenda(final ConflictResolver conflictResolver) {
        this.logger = Logger.getLogger(this.toString());
        this.activations = new HashMap<>();
        this.conflictSet = conflictResolver.createConflictSet();
        this.updatingListener = new ConflictSetUpdater(conflictSet);
        this.setActivationListener(new DefaultActivationNotificationListener(this));
    }
    
    public Agenda(final ConflictResolver conflictResolver, IActivationNotificationListener activationListener) {
        this.logger = Logger.getLogger(this.toString());
        this.setActivationListener(activationListener);
        Preconditions.checkState(this.getActivationListener() != null, "Activation Listener is null!");
        this.activations = new HashMap<>();
        this.conflictSet = conflictResolver.createConflictSet();
        this.updatingListener = new ConflictSetUpdater(conflictSet);
       
   }

    /**
     * @since 2.0
     */
    public Map<ActivationState, Set<Activation<?>>> getActivations() {
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
        return getActivations().values().stream().flatMap(Set::stream).collect(Collectors.toSet());
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

