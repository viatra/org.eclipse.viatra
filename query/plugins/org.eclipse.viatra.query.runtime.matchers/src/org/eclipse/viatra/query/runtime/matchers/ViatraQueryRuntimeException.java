/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers;

/**
 * A common base class for all exceptions thrown by various VIATRA Query Runtime APIs.
 * 
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public abstract class ViatraQueryRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8505253058035069310L;

    public ViatraQueryRuntimeException() {
        super();
    }

    public ViatraQueryRuntimeException(String message) {
        super(message);
    }

    public ViatraQueryRuntimeException(Throwable cause) {
        super(cause);
    }

    public ViatraQueryRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ViatraQueryRuntimeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
