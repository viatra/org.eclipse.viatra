/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf;

import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParser;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 */
public class EMFPatternLanguageConfigurationConstants {

    private EMFPatternLanguageConfigurationConstants() {/* Hidden utility class constructor */}
    
    /**
     * Configuration key for {@link PatternParser} class to decide whether pattern parser runs need to be separated.
     */
    public static final String SEPARATE_PATTERN_PARSER_RUNS_KEY = "SEPARATE_PATTERN_PARSER_RUNS";
    
    /**
     * Configuration key for enabling or disabling classpath validation
     */
    public static final String VALIDATE_CLASSPATH_KEY = "VALIDATE_CLASSPATH";
}
