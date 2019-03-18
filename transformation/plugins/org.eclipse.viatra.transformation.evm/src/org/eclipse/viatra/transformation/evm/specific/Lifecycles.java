/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.evm.specific;

import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle;
import org.eclipse.viatra.transformation.evm.specific.lifecycle.DefaultActivationLifeCycle;

/**
 * This utility class provides easy access to default lifcycles.
 * 
 * @author Abel Hegedus
 *
 */
public final class Lifecycles {

    /* 
     * These constants were moved here and made private to make future changes to the default
     * life-cycle easier without braking the API.
     */
    private static final DefaultActivationLifeCycle DEFAULT = new DefaultActivationLifeCycle(true, true);
    private static final DefaultActivationLifeCycle DEFAULT_NO_UPDATE = new DefaultActivationLifeCycle(false, true);
    private static final DefaultActivationLifeCycle DEFAULT_NO_DISAPPEAR = new DefaultActivationLifeCycle(true, false);
    private static final DefaultActivationLifeCycle DEFAULT_NO_UPDATE_AND_DISAPPEAR = new DefaultActivationLifeCycle(false, false);
    
    private Lifecycles() {}
    
    /**
     * See {@link DefaultActivationLifeCycle} documentation for details.
     * 
     * @param useUpdate UPDATED state is used
     * @param useDisappear DELETED state is used
     * @return the life cycle
     */
    public static ActivationLifeCycle getDefault(boolean useUpdate, boolean useDisappear) {
        if(!useUpdate) {
            if(!useDisappear) {
                return DEFAULT_NO_UPDATE_AND_DISAPPEAR;
            } else {
                return DEFAULT_NO_UPDATE;
            }
        } else if(!useDisappear) {
            return DEFAULT_NO_DISAPPEAR;
        }
        return DEFAULT;
    }
    
}
