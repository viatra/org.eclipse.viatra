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

import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.exceptions.LocalSearchException;

import com.google.common.collect.Lists;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class InequalityCheck extends CheckOperation {

    Integer sourceLocation, targetLocation;

    public InequalityCheck(int sourceLocation, int targetLocation) {
        super();
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
    }

    /* (non-Javadoc)
     * @see org.eclipse.viatra.query.runtime.localsearch.operations.check.CheckOperation#check(org.eclipse.viatra.query.runtime.localsearch.MatchingFrame)
     */
    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
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
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InequalityCheck";
    }

    @Override
	public List<Integer> getVariablePositions() {
		return Lists.asList(sourceLocation, targetLocation, new Integer[0]);
	}

}
