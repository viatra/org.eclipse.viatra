/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.operations.check;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.incquery.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.incquery.runtime.localsearch.operations.ISearchOperation;

/**
 * Abstract base class for search operations that check only the already set variables.
 */
public abstract class CheckOperation implements ISearchOperation {

    /**
     * The executed field ensures that the second call of the check always returns false, resulting in a quick
     * backtracking.
     */
    private boolean executed;

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        executed = false;
    }

    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        executed = executed ? false : check(frame);
        return executed;
    }

    /**
     * Executes the checking operation
     * 
     * @param frame
     * @return
     */
    protected abstract boolean check(MatchingFrame frame) throws LocalSearchException;

}
