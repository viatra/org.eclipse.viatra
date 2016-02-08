/*******************************************************************************
 * Copyright (c) 2010-2014, Tamas Szabo (itemis AG), Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.ui.queryexplorer.content.matcher;

import org.eclipse.viatra.query.runtime.api.IPatternMatch;

import com.google.common.base.Function;

/**
 * @author Tamas Szabo (itemis AG)
 *
 */
public class TransformerFunction implements Function<IPatternMatch, PatternMatchContent> {

    private PatternMatcherContent matcher;
    
    public TransformerFunction(PatternMatcherContent matcher) {
        this.matcher = matcher;
    }
    
    @Override
    public PatternMatchContent apply(IPatternMatch match) {
        return new PatternMatchContent(matcher, match);
    }
    
}
