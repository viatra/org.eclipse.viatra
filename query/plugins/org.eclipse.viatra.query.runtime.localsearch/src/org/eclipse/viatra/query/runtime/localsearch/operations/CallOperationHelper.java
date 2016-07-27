/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.localsearch.operations;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter;
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery;

import com.google.common.collect.Maps;

/**
 * @since 1.4
 */
public class CallOperationHelper {

    public static Map<Integer, Integer> calculateFrameMapping(PQuery calledQuery, Map<Integer, PParameter> parameterMapping) {
        Map<Integer, Integer> frameMapping = Maps.newHashMap();
        for (Entry<Integer, PParameter> entry : parameterMapping.entrySet()) {
            frameMapping.put(entry.getKey(), calledQuery.getPositionOfParameter(entry.getValue().getName()));
        }
        return frameMapping;
    }
}
