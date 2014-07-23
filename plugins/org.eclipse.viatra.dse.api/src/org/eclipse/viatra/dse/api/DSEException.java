/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api;

/**
 * Represents a general runtime exception that happened during the execution of the design space exploration process.
 * Problems that cause this exception are not recoverable within the scope of the design space exploration process.
 */
public class DSEException extends RuntimeException {

    private static final long serialVersionUID = -8312212010574763824L;

    /**
     * @see RuntimeException#RuntimeException().
     */
    public DSEException() {
        super();
    }

    /**
     * @see RuntimeException#RuntimeException(String)).
     */
    public DSEException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)).
     */
    public DSEException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable)).
     */
    public DSEException(Throwable cause) {
        super(cause);
    }

}
