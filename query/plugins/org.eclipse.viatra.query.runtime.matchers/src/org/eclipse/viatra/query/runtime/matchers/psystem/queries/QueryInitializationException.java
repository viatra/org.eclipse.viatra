/*******************************************************************************
 * Copyright (c) 2010-2015, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.psystem.queries;

import org.eclipse.viatra.query.runtime.matchers.planning.QueryProcessingException;

/**
 * Represent an exception that occurred while initializing the specification of a query. 
 * @author Bergmann Gabor
 * @since 0.9
 *
 */
public class QueryInitializationException extends QueryProcessingException {

    public QueryInitializationException(String message, String[] context, String shortMessage, Object patternDescription,
            Throwable cause) {
        super(message, context, shortMessage, patternDescription, cause);
    }

    public QueryInitializationException(String message, String[] context, String shortMessage, Object patternDescription) {
        super(message, context, shortMessage, patternDescription);
    }

    private static final long serialVersionUID = 9106033062252951489L;


    
    
}
