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

import java.util.Map;

import org.eclipse.incquery.runtime.extensibility.IMatchChecker;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class ExpressionCheck extends CheckOperation {

    IMatchChecker checker;
    Map<String, Integer> nameMap;

    public ExpressionCheck(IMatchChecker checker, Map<String, Integer> nameMap) {
        super();
        this.checker = checker;
        this.nameMap = nameMap;
    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.localsearch.operations.check.CheckOperation#check(org.eclipse.incquery.runtime.localsearch.MatchingFrame)
     */
    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        checker.evaluateXExpression(frame, nameMap);
        return false;
    }

}
