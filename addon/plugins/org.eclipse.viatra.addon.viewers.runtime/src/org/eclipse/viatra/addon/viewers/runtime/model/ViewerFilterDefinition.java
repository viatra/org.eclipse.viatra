/*******************************************************************************
 * Copyright (c) 2010-2013, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
