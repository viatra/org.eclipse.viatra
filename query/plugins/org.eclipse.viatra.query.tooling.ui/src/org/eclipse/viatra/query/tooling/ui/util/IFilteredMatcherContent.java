/*******************************************************************************
 * Copyright (c) 2010-2016, Abel Hegedus, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.util;

import java.util.Collection;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;

/**
 * Common interface for user interface objects that provide access to a matcher 
 * and a filter set for the matcher.
 * 
 * @author Abel Hegedus
 * @since 1.4
 *
 */
public interface IFilteredMatcherContent<MATCH extends IPatternMatch> {

    ViatraQueryMatcher<MATCH> getMatcher();
    
    IPatternMatch getFilterMatch();
    
    IFilteredMatcherCollection getParent();
    
    /**
     * Collects the filtered matches defined by the {@link #getFilterMatch()}
     * @since 1.7.1
     */
    Collection<MATCH> getFilteredMatches();
    
    /**
     * Counts the number of filtered matches defined by {@link #getFilterMatch()}
     * @since 1.7.1
     */
    int countFilteredMatches();
    
    /**
     * Returns whethere there is a filtered match defined by {@link #getFilterMatch()}
     * @since 1.7.1
     */
    boolean hasFilteredMatch();
}
