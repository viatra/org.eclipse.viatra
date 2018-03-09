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

import java.util.Arrays;
import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.CheckOperationExecutor;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;

/**
 * @author Zoltan Ujhelyi
 * @noextend This class is not intended to be subclassed by clients.
 */
public class InequalityCheck implements ISearchOperation {

    private class Executor extends CheckOperationExecutor {
        
        @Override
        protected boolean check(MatchingFrame frame, ISearchContext context) {
            Object source = frame.getValue(sourceLocation);
            Object target = frame.getValue(targetLocation);
            if (source == null) {
                throw new LocalSearchException("Source not bound.");
            }
            if (target == null) {
                throw new LocalSearchException("Target not bound");
            }
            return !source.equals(target);
        }
        
        @Override
        public ISearchOperation getOperation() {
            return InequalityCheck.this;
        }
    }
    
    int sourceLocation;
    int targetLocation;

    public InequalityCheck(int sourceLocation, int targetLocation) {
        super();
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
    }
    
    @Override
    public ISearchOperationExecutor createExecutor() {
        return new Executor();
    }

    @Override
    public String toString() {
        return "check     "+sourceLocation+" != "+targetLocation;
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Arrays.asList(sourceLocation, targetLocation);
    }

}
