/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.cep.core.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LoggerUtils {
    private static final String LOGGER_NAME = "org.eclipse.viatra.cep";
    private static LoggerUtils instance;
    private Logger logger;

    public static LoggerUtils getInstance() {
        if (instance == null) {
            instance = new LoggerUtils();
        }
        return instance;
    }

    private LoggerUtils() {
        this.logger = Logger.getLogger(LOGGER_NAME);
    }

    private LoggerUtils(String name) {
        this.logger = Logger.getLogger(name);
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLevel(Level level) {
        logger.setLevel(level);
    }
}
