/*******************************************************************************
 * Copyright (c) 2015-2016, IncQuery Labs Ltd., CEA LIST
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.evm.jdt.util;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IJavaElementDelta;

public enum ElementChangedEventType {

    /**
     * Event type constant (bit mask) indicating an after-the-fact
     * report of creations, deletions, and modifications
     * to one or more Java element(s) expressed as a hierarchical
     * java element delta as returned by <code>getDelta()</code>.
     *
     * Note: this notification occurs during the corresponding POST_CHANGE
     * resource change notification, and contains a full delta accounting for
     * any JavaModel operation  and/or resource change.
     *
     * @see IJavaElementDelta
     * @see org.eclipse.core.resources.IResourceChangeEvent
     * @see #getDelta()
     * @since 2.0
     */
    POST_CHANGE (ElementChangedEvent.POST_CHANGE),
    
    /**
     * Event type constant (bit mask) indicating an after-the-fact
     * report of creations, deletions, and modifications
     * to one or more Java element(s) expressed as a hierarchical
     * java element delta as returned by <code>getDelta</code>.
     *
     * Note: this notification occurs as a result of a working copy reconcile
     * operation.
     *
     * @see IJavaElementDelta
     * @see org.eclipse.core.resources.IResourceChangeEvent
     * @see #getDelta()
     * @since 2.0
     */
    POST_RECONCILE (ElementChangedEvent.POST_RECONCILE);
    
    private final int value;
    ElementChangedEventType(int value) {
        this.value = value;
    }
    
    public final int getValue() {
        return this.value;
    }
}
