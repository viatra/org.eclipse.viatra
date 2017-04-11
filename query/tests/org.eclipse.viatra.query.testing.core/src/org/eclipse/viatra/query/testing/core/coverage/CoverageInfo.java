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

import com.google.common.collect.Sets;

public class CoverageInfo<T> extends HashMap<T, CoverageState>{

    private static final long serialVersionUID = -8699692647123679741L;

    /**
     * Merge coverage. A key is considered covered, if it's covered in at least one of the infos.
     */
    public CoverageInfo<T> mergeWith(CoverageInfo<T> other){
        CoverageInfo<T> result = new CoverageInfo<>();
        
        for(T key: Sets.union(this.keySet(), other.keySet())){
            if (containsKey(key)){
                result.put(key, get(key).best(other.get(key)));
            }else{
                result.put(key, other.get(key));
            }
        }
        
        return result;
    }
    
    /**
     * @return the ratio of covered keys to the coverable (covered plus not covered) in percents
     */
    public float getCoveragePercent(){
        int covered = 0;
        int notcovered = 0;
        for(CoverageState state: values()){
            switch(state){
            case COVERED:
                covered++;
                break;
            case NOT_COVERED:
                notcovered++;
                break;
            case NOT_REPRESENTED:
            case UNDEFINED:
            default:
                break;
            }
        }
        if ((covered+notcovered)>0){
            return (100f*(float)covered) / ((float)(covered+notcovered));
        }
        return 0;
    }
    
}
