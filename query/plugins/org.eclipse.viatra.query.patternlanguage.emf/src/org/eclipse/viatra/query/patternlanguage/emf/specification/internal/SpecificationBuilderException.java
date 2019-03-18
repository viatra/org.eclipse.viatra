/*******************************************************************************
 * Copyright (c) 2010-2014, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.specification.internal;

import org.eclipse.viatra.query.runtime.matchers.psystem.queries.QueryInitializationException;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class SpecificationBuilderException extends QueryInitializationException {

    private static final long serialVersionUID = -9107359020858174569L;

    public SpecificationBuilderException(String message, String[] context, String shortMessage,
            Object patternDescription, Throwable cause) {
        super(message, context, shortMessage, patternDescription, cause);
    }

    public SpecificationBuilderException(String message, String[] context, String shortMessage,
            Object patternDescription) {
        super(message, context, shortMessage, patternDescription);
    }

}
