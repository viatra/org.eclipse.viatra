/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.matcher.integration;

import org.eclipse.incquery.runtime.matchers.backend.IQueryBackend;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.incquery.runtime.matchers.backend.IQueryBackendHintProvider;
import org.eclipse.incquery.runtime.matchers.context.IPatternMatcherRuntimeContext;

/**
 * @author Marton Bur, Zoltan Ujhelyi
 *
 */
public class LocalSearchBackendFactory implements IQueryBackendFactory{

    @Override
    public IQueryBackend create(IPatternMatcherRuntimeContext matcherContext, IQueryBackendHintProvider hintProvider) {
        return new LocalSearchBackend(matcherContext, hintProvider);
    }

}
