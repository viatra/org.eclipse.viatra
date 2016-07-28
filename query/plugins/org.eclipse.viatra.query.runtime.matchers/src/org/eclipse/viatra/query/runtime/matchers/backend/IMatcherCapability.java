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
package org.eclipse.viatra.query.runtime.matchers.backend;

/**
 * Implementations of this interface can be used to decide whether a matcher created by an arbitrary backend can
 * potentially be used as a substitute for another matcher.
 * 
 * @author Grill Balázs
 * @since 1.4
 *
 */
public interface IMatcherCapability {

    /**
     * Returns true if matchers of this capability can be used as a substitute for a matcher implementing the given capability
     * 
     * @param capability
     * @return
     */
    public boolean canBeSubstitute(IMatcherCapability capability);
    
}
