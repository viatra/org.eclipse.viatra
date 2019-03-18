/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.runtime.model.listeners;

import org.eclipse.viatra.addon.viewers.runtime.notation.Containment;
import org.eclipse.viatra.addon.viewers.runtime.notation.Edge;
import org.eclipse.viatra.addon.viewers.runtime.notation.Item;

/**
 * An empty implementation of the {@link IViewerStateListener} interface.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public abstract class AbstractViewerStateListener implements IViewerStateListener {

    @Override
    public void itemAppeared(Item item) {
    }

    @Override
    public void itemDisappeared(Item item) {
    }

    @Override
    public void containmentAppeared(Containment containment) {
    }

    @Override
    public void containmentDisappeared(Containment containment) {
    }

    @Override
    public void edgeAppeared(Edge edge) {
    }

    @Override
    public void edgeDisappeared(Edge edge) {
    }

}
