/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.databinding.runtime.util;

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.qrm.EVMBasedQueryResultMultimap;

/**
 * Multimap for managing multiple patterns and related matches for a given notifier.
 * 
 * @author Abel Hegedus
 * 
 */
public class PatternToMatchMultimap<MatchType extends IPatternMatch> extends
        EVMBasedQueryResultMultimap<MatchType, Pattern, MatchType> {

    /**
     * Creates a new multimap for the given engine.
     * 
     * @param engine
     *            the engine to use
     */
    public PatternToMatchMultimap(IncQueryEngine engine) {
        super(engine);
    }

    @Override
    protected Pattern getKeyFromMatch(MatchType match) {
        return match.pattern();
    }

    @Override
    protected MatchType getValueFromMatch(MatchType match) {
        return match;
    }

}
