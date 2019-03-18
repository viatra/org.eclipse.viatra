/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.addon.querybasedfeatures.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Abel Hegedus
 *
 */
public enum QueryBasedFeatureKind {
    SINGLE_REFERENCE, MANY_REFERENCE, SUM, ITERATION;
    
    private static final Map<String,QueryBasedFeatureKind> KIND_TEXT = new HashMap<>(); 
    static {
        KIND_TEXT.put("single", QueryBasedFeatureKind.SINGLE_REFERENCE);
        KIND_TEXT.put("many", QueryBasedFeatureKind.MANY_REFERENCE);
        KIND_TEXT.put("sum", QueryBasedFeatureKind.SUM);
        KIND_TEXT.put("iteration", QueryBasedFeatureKind.ITERATION);
    }
    
    
    public static QueryBasedFeatureKind parseKindString(String desc) {
        if (KIND_TEXT.containsKey(desc)) {
            return KIND_TEXT.get(desc);
        } else {
            return valueOf(desc);
        }
    }
    
    public static String getStringValue(QueryBasedFeatureKind kind) {
        if(SINGLE_REFERENCE.equals(kind)) {
            return "single";
        } else if(MANY_REFERENCE.equals(kind)) {
            return "many";
        } else if(SUM.equals(kind)) {
            return "sum";
        } else if(ITERATION.equals(kind)) {
            return "iteration";
        } else {
            return null;
        }
    }
}