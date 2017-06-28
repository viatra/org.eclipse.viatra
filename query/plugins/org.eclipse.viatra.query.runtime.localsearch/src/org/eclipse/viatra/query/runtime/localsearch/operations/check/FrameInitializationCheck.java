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
package org.eclipse.viatra.query.runtime.localsearch.operations.check;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;

/**
 * Initializes the {@link MatchingFrame} with the given key array. Can fail if non-equal
 * values are given to unified parameters.
 * 
 * @author Grill Balázs
 * @since 1.3
 * @noextend This class is not intended to be subclassed by clients.
 *
 */
public class FrameInitializationCheck extends CheckOperation {

    private final int[] parameterKeys;
    
    public FrameInitializationCheck(int[] parameterKeys) {
       this.parameterKeys = Arrays.copyOf(parameterKeys, parameterKeys.length);
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Collections.emptyList();
    }

    /**
     * @deprecated Use {@link #check(MatchingFrame, ISearchContext)} instead
     */
    @Deprecated
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        return check(frame, null);
    }

    @Override
    protected boolean check(MatchingFrame frame, ISearchContext context) throws LocalSearchException {
        return frame.setKeys(this.parameterKeys);
    }

    @Override
    public String toString() {
        return String.format("parameter positions %s", Arrays.toString(parameterKeys));
    }

}
