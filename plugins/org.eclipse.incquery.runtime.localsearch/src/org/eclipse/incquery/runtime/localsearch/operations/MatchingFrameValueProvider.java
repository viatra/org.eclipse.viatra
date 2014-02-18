/*******************************************************************************
 * Copyright (c) 2010-2014, stampie, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   stampie - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.operations;

import java.util.Map;

import org.eclipse.incquery.runtime.localsearch.MatchingFrame;
import org.eclipse.incquery.runtime.matchers.psystem.IValueProvider;

import com.google.common.base.Preconditions;

/**
 * 
 * 
 * @author Zoltan Ujhelyi
 *
 */
public class MatchingFrameValueProvider implements IValueProvider {
    
    final Map<String, Integer> nameMap;
    final MatchingFrame frame;
    
    public MatchingFrameValueProvider(MatchingFrame frame, Map<String, Integer> nameMap) {
        super();
        this.frame = frame;
        this.nameMap = nameMap;
    }

    @Override
    public Object getValue(String variableName) throws IllegalArgumentException {
        Integer index = nameMap.get(variableName);
        Preconditions.checkArgument(index != null, "Unknown parameter variable name");
        return frame.get(index);
    }

}
