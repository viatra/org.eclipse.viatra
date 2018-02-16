/*******************************************************************************
/**
 * Copyright (c) 2010-2016, Peter Lunk, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.query.patternlanguage.emf.util;

import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;

/**
 * Allows the caller to parse VIATRA query patterns provided in text format. IMPORTANT: This API class assumes that the
 * Xtext parser infrastructure is already initialized. If its not, {@link ViatraQueryException} is thrown.
 * 
 * @author Peter Lunk
 * @since 1.5
 * @deprecated use the new {@link PatternParser.Builder} infrastructure instead
 */
public class PatternParsingUtil {
    
    public static final String PPERROR = PatternParser.Builder.PPERROR;

    private PatternParsingUtil() {
        // Empty utility constructor
    }

    /**
     * Parses a set of patterns; the returned object can be used either to access the parsed patterns or query
     * specifications as well; parse errors are also available.
     * 
     * @since 1.7
     */
    public static PatternParsingResults parsePatternDefinitions(String patternString) {
        return parsePatternDefinitions(patternString, XtextInjectorProvider.INSTANCE.getInjector());
    }

    /**
     * Parses a set of patterns; the returned object can be used either to access the parsed patterns or query
     * specifications as well; parse errors are also available.
     * 
     * @since 1.7
     */
    public static PatternParsingResults parsePatternDefinitions(String patternString, Injector injector) {
        Preconditions.checkState(injector != null, PPERROR);
        
        return PatternParser.parser().withInjector(injector).parse(patternString);
    }

    /**
     * @return A list of parsed query specifications; the contents of the list is undefined if the source file cannot be
     *         parsed completely
     * @since 1.7
     */
    public static Iterable<IQuerySpecification<?>> parseQueryDefinitions(String patternString) {
        return parsePatternDefinitions(patternString).getQuerySpecifications();
    }

    /**
     * @return A list of parsed query specifications; the contents of the list is undefined if the source file cannot be
     *         parsed completely
     * @since 1.7
     */
    public static Iterable<IQuerySpecification<?>> parseQueryDefinitions(String patternString, Injector injector) {
        return parsePatternDefinitions(patternString, injector).getQuerySpecifications();
    }
}
