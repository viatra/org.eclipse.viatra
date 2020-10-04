/*******************************************************************************
 * Copyright (c) 2010-2013, Bergmann Gabor, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.viatra.query.runtime.matchers.util.Preconditions;

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
                defaultRuntimeLogger = Logger.getLogger("org.eclipse.viatra");
            } else {
                defaultRuntimeLogger = Logger.getLogger(parentLogger.getName() + ".runtime");
            }
            if (defaultRuntimeLogger == null)
                throw new AssertionError("Configuration error: unable to create default VIATRA Query runtime logger.");
        }

        return defaultRuntimeLogger;
    }
    
    private static String getLoggerClassname(Class<?> clazz) {
        return clazz.getName().startsWith(getDefaultLogger().getName()) 
                ? clazz.getName() 
                : getDefaultLogger().getName() + "." + clazz.getName();
    }
    
    /**
     * Provides a class-specific logger that also stores the global logger settings of the VIATRA Query runtime
     * @param clazz
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(getLoggerClassname(clazz));
    }
    
    /**
     * Provides a named logger that also stores the global logger settings of the VIATRA Query runtime
     * @param clazz
     * @param name a non-empty name to append to the class names
     * @since 2.5
     */
    public static Logger getLogger(Class<?> clazz, String name) {
        return Logger.getLogger(getLoggerClassname(clazz) + '.' + name);
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
