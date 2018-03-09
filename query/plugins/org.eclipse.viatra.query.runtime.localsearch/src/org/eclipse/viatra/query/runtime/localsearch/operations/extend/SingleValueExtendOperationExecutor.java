/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.extend;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.ExtendOperationExecutor;

/**
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class SingleValueExtendOperationExecutor<T> extends ExtendOperationExecutor<T> {
    protected int position;
    
    /**
     * @param position the frame position all values are to be added
     */
    public SingleValueExtendOperationExecutor(int position) {
        super();
        this.position = position;
    }

    @Override
    protected final boolean fillInValue(T newValue, MatchingFrame frame, ISearchContext context) {
        frame.setValue(position, newValue);
        return true;
    }

    @Override
    protected final void cleanup(MatchingFrame frame, ISearchContext context) {
        frame.setValue(position, null);
    }
}