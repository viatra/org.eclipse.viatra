/*******************************************************************************
 * Copyright (c) 2010-2014, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations.check;

import java.util.Collections;
import java.util.List;

import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;
import org.eclipse.viatra.query.runtime.localsearch.matcher.ISearchContext;
import org.eclipse.viatra.query.runtime.localsearch.operations.CheckOperationExecutor;
import org.eclipse.viatra.query.runtime.localsearch.operations.ISearchOperation;

/**
 * This operation handles constants in search plans by checking if a variable is bound to a certain constant value. Such
 * operations should be executed as early as possible during plan execution.
 * 
 * @author Marton Bur
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CheckConstant implements ISearchOperation {

    private class Executor extends CheckOperationExecutor {
        
        @Override
        protected boolean check(MatchingFrame frame, ISearchContext context) {
            return frame.get(position).equals(value);
        }
        
        @Override
        public ISearchOperation getOperation() {
            return CheckConstant.this;
        }
    }
    
    private int position;
    private Object value;

    public CheckConstant(int position, Object value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public ISearchOperationExecutor createExecutor() {
        return new Executor();
    }

    @Override
    public List<Integer> getVariablePositions() {
        return Collections.singletonList(position);
    }
    
    @Override
    public String toString() {
        return "check     constant "+position+"='"+value+"'";
    }
    
}
