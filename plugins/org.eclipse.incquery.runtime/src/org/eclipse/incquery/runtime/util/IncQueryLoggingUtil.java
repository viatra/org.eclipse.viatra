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
	        if (parentLogger == null) {
	            defaultRuntimeLogger = Logger.getLogger(IncQueryLoggingUtil.class);
	        } else {
	            defaultRuntimeLogger = Logger.getLogger(parentLogger.getName() + ".runtime");
	        }
	        if (defaultRuntimeLogger == null)
	            throw new AssertionError("Configuration error: unable to create default EMF-IncQuery runtime logger.");
	    }

	    return defaultRuntimeLogger;
	}
	
	/**
	 * Provides a class-specific logger that also stores the global logger settings of the EMF-IncQuery runtime
	 * @param clazz
	 * @return
	 */
	public static Logger getLogger(Class<?> clazz) {
	    return Logger.getLogger(getDefaultLogger().getName() + "." + clazz.getName());
	}

	private static Logger defaultRuntimeLogger;

}
