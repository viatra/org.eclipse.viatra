/*******************************************************************************
 * Copyright (c) 2010-2016, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations;

import org.eclipse.viatra.query.runtime.matchers.context.IInputKey;

/**
 * Denotes a {@link ISearchOperation} which involves iterating over an instances of an {@link IInputKey}
 * 
 * @author Grill Balázs
 * @since 1.4
 *
 */
public interface IIteratingSearchOperation extends ISearchOperation{

    /**
     * Get the {@link IInputKey} which instances this operation iterates upon.
     */
    public IInputKey getIteratedInputKey();
    
}
