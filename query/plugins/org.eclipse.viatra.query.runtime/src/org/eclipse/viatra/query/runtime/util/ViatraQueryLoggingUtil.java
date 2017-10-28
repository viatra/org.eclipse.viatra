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
package org.eclipse.viatra.query.runtime.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.google.common.base.Preconditions;

/**
 * Centralized logger of the VIATRA Query runtime.
 * @author Bergmann Gabor
 *
 */
public class ViatraQueryLoggingUtil {

    private ViatraQueryLoggingUtil() {/*Utility class constructor*/}
    
    private static Logger externalLogger;

    public static void setExternalLogger(Logger externalLogger) {
        Preconditions.checkArgument(externalLogger != null, "Must not set up null logger");
        ViatraQueryLoggingUtil.externalLogger = externalLogger;
    }
    /**
     * Provides a static default logger.
     */
    public static Logger getDefaultLogger() {
        if (defaultRuntimeLogger == null) {
            Logger parentLogger = externalLogger;
            if (parentLogger == null) {
                defaultRuntimeLogger = Logger.getLogger(ViatraQueryLoggingUtil.class);
            } else {
                defaultRuntimeLogger = Logger.getLogger(parentLogger.getName() + ".runtime");
            }
            if (defaultRuntimeLogger == null)
                throw new AssertionError("Configuration error: unable to create default VIATRA Query runtime logger.");
        }

        return defaultRuntimeLogger;
    }
    
    /**
     * Provides a class-specific logger that also stores the global logger settings of the VIATRA Query runtime
     * @param clazz
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(getDefaultLogger().getName() + "." + clazz.getName());
    }
    
    /**
     * Adds an appender to the default logger that will output to the default console with the TTCC conversion pattern.
     * Call this method if you don't have Log4J configured from properties.
     * In addition, additivity is set to false for the default logger.
     */
    public static void setupConsoleAppenderForDefaultLogger() {
        Logger logger = getDefaultLogger();
        logger.setAdditivity(false);
        logger.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
    }

    private static Logger defaultRuntimeLogger;

}
