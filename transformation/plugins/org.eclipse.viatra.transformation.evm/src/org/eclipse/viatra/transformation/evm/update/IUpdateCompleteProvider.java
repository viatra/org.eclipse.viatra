/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.update;


/**
 * This interface is used to register listeners for a given type of update complete event.
 * 
 * @author Abel Hegedus
 * 
 */
public interface IUpdateCompleteProvider {

    /**
     * Registers an {@link IUpdateCompleteListener} to receive notification on completed updates.
     * 
     * <p>
     * The listener can be unregistered via {@link #removeUpdateCompleteListener(IUpdateCompleteListener)}.
     * 
     * @param fireNow
     *            if true, listener will be immediately invoked without waiting for the next update
     * 
     * @param listener
     *            the listener that will be notified of each completed update
     */
    boolean addUpdateCompleteListener(final IUpdateCompleteListener listener, final boolean fireNow);

    /**
     * Unregisters a listener registered by
     * {@link IUpdateCompleteProvider#addUpdateCompleteListener(IUpdateCompleteListener, boolean)}.
     * 
     * @param listener
     *            the listener that will no longer be notified.
     */
    boolean removeUpdateCompleteListener(final IUpdateCompleteListener listener);

}