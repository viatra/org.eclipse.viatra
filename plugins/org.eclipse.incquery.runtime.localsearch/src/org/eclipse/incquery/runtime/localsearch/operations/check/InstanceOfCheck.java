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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.incquery.runtime.localsearch.MatchingFrame;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class InstanceOfCheck extends CheckOperation {

    private Integer position;
    private EClass clazz;

    public InstanceOfCheck(int position, EClass clazz) {
        this.position = position;
        this.clazz = clazz;

    }

    /* (non-Javadoc)
     * @see org.eclipse.incquery.runtime.localsearch.operations.check.CheckOperation#check(org.eclipse.incquery.runtime.localsearch.MatchingFrame)
     */
    @Override
    protected boolean check(MatchingFrame frame) {
        if (frame.getValue(position) instanceof EObject) {
            return clazz.equals(((EObject) frame.getValue(position)).eClass());
        }
        return false;
    }

}
