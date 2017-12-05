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
package org.eclipse.viatra.query.runtime.localsearch.operations.check;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;

/**
 * Abstract base class for search operations that check only the already set variables.
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class CheckOperation implements ISearchOperation {

    /**
     * The executed field ensures that the second call of the check always returns false, resulting in a quick
     * backtracking.
     */
    private boolean executed;

    @Override
    public void onInitialize(MatchingFrame frame, ISearchContext context) {
        executed = false;
    }

    @Override
    public void onBacktrack(MatchingFrame frame, ISearchContext context) {
    }

    @Override
    public boolean execute(MatchingFrame frame, ISearchContext context) {
        executed = executed ? false : check(frame, context);
        return executed;
    }

    /**
     * Executes the checking operation
     * @since 1.7
     */
    protected abstract boolean check(MatchingFrame frame, ISearchContext context) ;

}
