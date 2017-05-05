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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @author Zoltan Ujhelyi
 * @since 1.4
 */
public class InstanceOfJavaClassCheck extends CheckOperation {

    private int position;
    private Class<?> clazz;

    public InstanceOfJavaClassCheck(int position, Class<?> clazz) {
        this.position = position;
        this.clazz = clazz;

    }

    @Override
    protected boolean check(MatchingFrame frame) {
        Preconditions.checkNotNull(frame.getValue(position), "Invalid plan, variable %s unbound", position);
        return clazz.isInstance(frame.getValue(position));
    }

    @Override
    public String toString() {
        return "check     java "+clazz.getName()+"(+"+position+")";
    }
    @Override
    public List<Integer> getVariablePositions() {
        return Lists.asList(position, new Integer[0]);
    }
    
}
