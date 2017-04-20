/*******************************************************************************
 * Copyright (c) 2010-2017, Grill Balázs, IncQueryLabs
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Grill Balázs - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core.coverage;

import java.util.HashMap;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * Associates {@link CoverageState}s to objects.
 *
 * @param <T> type of objects whose coverages are stored
 * @since 1.6
 */
public class CoverageInfo<T> extends HashMap<T, CoverageState>{

    private static final long serialVersionUID = -8699692647123679741L;

    /**
     * Merge coverage. A key is considered covered, if it's covered in at least one of the infos.
     */
    public CoverageInfo<T> mergeWith(CoverageInfo<T> other){
        CoverageInfo<T> result = new CoverageInfo<>();
        
        for(T key: Sets.union(this.keySet(), other.keySet())){
        	CoverageState state = get(key);
        	CoverageState otherState = other.get(key);
        	if ((state != null) && (otherState != null)) {
        		result.put(key, state.best(otherState));
        	} else {
        		result.put(key, Objects.firstNonNull(state, otherState));
        	}
        }
        
        return result;
    }
    
}
