/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.transformation.evm.notification;


/**
 * The interface exposes the {@link #notifyUpdate(Atom)} method to
 *  receive notifications when the attributes of the atom objects have changed.
 * 
 * @author Tamas Szabo
 * 
 * @param <Atom>
 */
public interface IAttributeMonitorListener<Atom> {

    /**
     * This method is called by {@link AttributeMonitor} when a feature value
     *  changes in one of the objects in the atom.
     * 
     * @param match
     */
    void notifyUpdate(final Atom atom);

}
