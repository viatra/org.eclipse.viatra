/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Akos Horvath, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Akos Horvath - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.localsearch.exceptions;

import org.eclipse.incquery.runtime.exception.IncQueryException;

/**
 * @author Zoltan Ujhelyi, Akos Horvath
 * 
 */
public class LocalSearchException extends IncQueryException {

    private static final long serialVersionUID = -2585896573351435974L;

    public static final String PLAN_EXECUTION_ERROR = "Error while executing search plan";
    public static final String TYPE_ERROR = "Invalid type of variable";

    public LocalSearchException(String description, Throwable rootException) {
        super(description, description, rootException);
    }

    public LocalSearchException(String description) {
        super(description, description);
    }


}
