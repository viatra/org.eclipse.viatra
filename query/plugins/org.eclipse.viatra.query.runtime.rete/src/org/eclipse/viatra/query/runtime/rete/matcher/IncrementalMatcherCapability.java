/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.matcher;

import org.eclipse.viatra.query.runtime.matchers.backend.IMatcherCapability;

/**
 * @author Grill Balázs
 * @since 1.4
 *
 */
public class IncrementalMatcherCapability implements IMatcherCapability {

    @Override
    public boolean canBeSubstitute(IMatcherCapability capability) {
        /*
         * TODO: for now, as we are only prepared for Rete and LS, we can assume that
         * a matcher created with Rete can always be a substitute for a matcher created
         * by any backend.
         */
        return true;
    }

}
