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
package org.eclipse.incquery.runtime.localsearch.operations.check;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.localsearch.exceptions.LocalSearchException;

/**
 * This operation handles constants in search plans by checking if a variable is bound to a certain constant value. Such
 * operations should be executed as early as possible during plan execution.
 * 
 * @author Marton Bur
 *
 */
public class CheckConstant extends CheckOperation {

    private int position;
    private Object value;

    public CheckConstant(int position, Object value) {
        this.position = position;
        this.value = value;
    }

    @Override
    protected boolean check(MatchingFrame frame) throws LocalSearchException {
        return frame.get(position).equals(value);
    }


}
