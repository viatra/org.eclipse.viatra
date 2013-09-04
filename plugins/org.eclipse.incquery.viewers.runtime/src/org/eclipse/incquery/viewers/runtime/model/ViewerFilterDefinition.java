/*******************************************************************************
 * Copyright (c) 2010-2013, istvanrath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.viewers.runtime.model;

import java.util.Collection;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryFilterSemantics;

/**
 * 
 * Data Transfer Class for describing a viewer filter configuration.
 * @author istvanrath
 *
 */
public class ViewerFilterDefinition {

    Pattern pattern;
    
    IncQueryFilterSemantics semantics;
    
    IPatternMatch singleFilterMatch;
    
    Collection<IPatternMatch> filterMatches;
    
    /**
     * @param pattern the {@link Pattern} this filter configuration is attached to
     * @param semantics {@link IncQueryFilterSemantics} prescribing how this configuration should be interpreted
     * @param singleFilterMatch mandatory for SINGLE {@link IncQueryFilterSemantics}
     * @param filterMatches mandatory for non-SINGLE {@link IncQueryFilterSemantics}
     */
    public ViewerFilterDefinition(Pattern pattern, IncQueryFilterSemantics semantics, IPatternMatch singleFilterMatch,
            Collection<IPatternMatch> filterMatches) {
        super();
        this.pattern = pattern;
        this.semantics = semantics;
        this.singleFilterMatch = singleFilterMatch;
        this.filterMatches = filterMatches;
    }


   
    
}
