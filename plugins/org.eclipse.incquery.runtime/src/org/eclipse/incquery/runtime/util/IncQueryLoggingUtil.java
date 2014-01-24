/*******************************************************************************
 * Copyright (c) 2010-2013, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.util;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

/**
 * Centralized logger of the EMF-IncQuery runtime.
 * @author Bergmann Gabor
 *
 */
public class IncQueryLoggingUtil {


    private static Logger externalLogger;

    public static void setExternalLogger(Logger externalLogger) {
        Preconditions.checkArgument(externalLogger != null, "Must not set up null logger");
        IncQueryLoggingUtil.externalLogger = externalLogger;
    }
	/**
	 * Provides a static default logger.
	 */
	public static Logger getDefaultLogger() {
	    if (defaultRuntimeLogger == null) {
	        Logger parentLogger = externalLogger;
	        if (parentLogger == null)
	            throw new AssertionError("Configuration error: EMF-IncQuery logger not found.");

	        defaultRuntimeLogger = Logger.getLogger(parentLogger.getName() + ".runtime");
	        if (defaultRuntimeLogger == null)
	            throw new AssertionError("Configuration error: unable to create default EMF-IncQuery runtime logger.");
	    }

	    return defaultRuntimeLogger;
	}

	private static Logger defaultRuntimeLogger;

}
