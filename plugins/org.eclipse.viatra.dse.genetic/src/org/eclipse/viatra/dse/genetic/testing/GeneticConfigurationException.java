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
package org.eclipse.viatra.dse.genetic.testing;

import org.eclipse.viatra.dse.api.DSEException;

public class GeneticConfigurationException extends DSEException{

    private static final long serialVersionUID = -1875675292849836784L;

    /**
     * @see RuntimeException#RuntimeException().
     */
    public GeneticConfigurationException() {
        super();
    }

    /**
     * @see RuntimeException#RuntimeException(String)).
     */
    public GeneticConfigurationException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)).
     */
    public GeneticConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable)).
     */
    public GeneticConfigurationException(Throwable cause) {
        super(cause);
    }

}
