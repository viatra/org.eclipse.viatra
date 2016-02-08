/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Abel Hegedus - initial API and implementation
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
