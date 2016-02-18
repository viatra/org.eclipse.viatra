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
package org.eclipse.viatra.addon.viewers.runtime.model;

import java.util.Collection;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.transformation.evm.specific.event.ViatraQueryFilterSemantics;

/**
 *
 * Data Transfer Class for describing a viewer filter configuration.
 * @author istvanrath
 *
 */
public class ViewerFilterDefinition {

    IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> pattern;

    ViatraQueryFilterSemantics semantics;

    IPatternMatch singleFilterMatch;

    Collection<IPatternMatch> filterMatches;

    /**
     * @param pattern the {@link IQuerySpecification} this filter configuration is attached to
     * @param semantics {@link ViatraQueryFilterSemantics} prescribing how this configuration should be interpreted
     * @param singleFilterMatch mandatory for SINGLE {@link ViatraQueryFilterSemantics}
     * @param filterMatches mandatory for non-SINGLE {@link ViatraQueryFilterSemantics}
     */
    public ViewerFilterDefinition(IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> pattern, ViatraQueryFilterSemantics semantics, IPatternMatch singleFilterMatch,
            Collection<IPatternMatch> filterMatches) {
        super();
        this.pattern = pattern;
        this.semantics = semantics;
        this.singleFilterMatch = singleFilterMatch;
        this.filterMatches = filterMatches;
    }
}
