/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.eclipse.incquery.runtime.api.IncQueryEngine;

/**
 * @author Abel Hegedus
 *
 * triggering mechanism
 *  - allows the execution of activations
 *  - move everything form Agenda that is not strictly related to managing the activation set
 *  - possibly allow an Activation Ordering (Comparator<Activation>?)
 */
public class Executor {

    private Agenda agenda;
    private Context context;
    private boolean scheduling = false;
    
    protected Executor(final IncQueryEngine engine) {
        this(engine, Context.create());
    }

    protected Executor(final IncQueryEngine engine, final Context context) {
        this.context = checkNotNull(context, "Cannot create trigger engine with null context!");
        agenda = new Agenda(engine);
    }

    protected void schedule() {
        
        if(!startScheduling()) {
            return;
        }
        
        Set<Activation<?>> enabledActivations = agenda.getEnabledActivations();
        while(!enabledActivations.isEmpty()) {
            Activation<?> activation = enabledActivations.iterator().next();
            agenda.getIncQueryEngine().getLogger().debug(String.format("Executing %s in %s.",activation,this));
            activation.fire(context);
        }
        
        endScheduling();
    }

    private synchronized void endScheduling() {
        agenda.getIncQueryEngine().getLogger().debug(String.format("Executing ended in %s.",this));
        scheduling = false;
    }

    protected synchronized boolean startScheduling() {
        if(scheduling) {
            agenda.getIncQueryEngine().getLogger().debug(String.format("Re-entrant schedule call ignored in %s.", this));
            return false;
        } else {
            scheduling = true;
            agenda.getIncQueryEngine().getLogger().debug(String.format("Executing started in %s.",this));
            return true;
        }
    }
    
    /**
     * @return the agenda
     */
    public Agenda getAgenda() {
        return agenda;
    }
    
    /**
     * @return the context
     */
    public Context getContext() {
        return context;
    }
    
    protected void dispose() {
        agenda.dispose();
    }
    
}
