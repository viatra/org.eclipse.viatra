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

/**
 * @author Zoltan Ujhelyi, Akos Horvath
 * 
 */
public class LocalSearchException extends Exception {

    private static final long serialVersionUID = 9122178654472982642L;

    public static final String PLAN_EXECUTION_ERROR = "Error while executing search plan";

    public LocalSearchException() {
        super();
    }

    public LocalSearchException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public LocalSearchException(String arg0) {
        super(arg0);
    }

    public LocalSearchException(Throwable arg0) {
        super(arg0);
    }

}
