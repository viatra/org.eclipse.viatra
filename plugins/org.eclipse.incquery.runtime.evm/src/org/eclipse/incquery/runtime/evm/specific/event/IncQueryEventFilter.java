/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific.event;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;

/**
 * @author Abel Hegedus
 *
 */
public class IncQueryEventFilter<Match extends IPatternMatch> implements EventFilter<Match> {

    private Match filterMatch;
    
    public Match getFilterMatch() {
        return filterMatch;
    }

    @Override
    public boolean isProcessable(Match eventAtom) {
        return filterMatch.isCompatibleWith(eventAtom);
    }

    protected IncQueryEventFilter(Match filterMatch) {
        checkArgument(filterMatch != null, "Cannot create filter with null match");
        this.filterMatch = filterMatch;
    }
    
}
