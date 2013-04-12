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
package org.eclipse.incquery.runtime.evm.specific;

import org.eclipse.incquery.runtime.evm.api.ActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;

/**
 * @author Abel Hegedus
 *
 */
public final class Lifecycles {

    public static ActivationLifeCycle getDefault(boolean useUpdate, boolean useDisappear) {
        if(!useUpdate) {
            if(!useDisappear) {
                return DefaultActivationLifeCycle.DEFAULT_NO_UPDATE_AND_DISAPPEAR;
            } else {
                return DefaultActivationLifeCycle.DEFAULT_NO_DISAPPEAR;
            }
        } else if(useDisappear) {
            return DefaultActivationLifeCycle.DEFAULT_NO_UPDATE;
        }
        return DefaultActivationLifeCycle.DEFAULT;
    }
    
}
