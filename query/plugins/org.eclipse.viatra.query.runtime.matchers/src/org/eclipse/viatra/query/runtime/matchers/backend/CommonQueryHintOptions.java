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
package org.eclipse.viatra.query.runtime.matchers.backend;

import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.IRewriterTraceCollector;
import org.eclipse.viatra.query.runtime.matchers.psystem.rewriters.NopTraceCollector;

/**
 * Query evaluation hints applicable to any engine
 * @since 1.6
 *
 */
public class CommonQueryHintOptions {
    
    /**
     * This hint instructs the query backends to record trace information into the given trace collector
     */
    public static final QueryHintOption<IRewriterTraceCollector> normalizationTraceCollector = 
            hintOption("normalizationTraceCollector", NopTraceCollector.INSTANCE);
    
    // internal helper for conciseness
    private static <T> QueryHintOption<T> hintOption(String hintKeyLocalName, T defaultValue) {
        return new QueryHintOption<T>(CommonQueryHintOptions.class, hintKeyLocalName, defaultValue);
    }

}
