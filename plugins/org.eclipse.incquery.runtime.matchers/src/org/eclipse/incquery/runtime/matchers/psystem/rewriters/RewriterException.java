/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.matchers.psystem.rewriters;

import org.eclipse.incquery.runtime.matchers.planning.QueryPlannerException;

/**
 * An exception to wrap various issues during PDisjunction rewriting.
 * @author Zoltan Ujhelyi
 *
 */
public class RewriterException extends QueryPlannerException {

    private static final long serialVersionUID = -4703825954995497932L;

    public RewriterException(String message, String[] context, String shortMessage, Object patternDescription,
            Throwable cause) {
        super(message, context, shortMessage, patternDescription, cause);
    }

    public RewriterException(String message, String[] context, String shortMessage, Object patternDescription) {
        super(message, context, shortMessage, patternDescription);
    }

}
