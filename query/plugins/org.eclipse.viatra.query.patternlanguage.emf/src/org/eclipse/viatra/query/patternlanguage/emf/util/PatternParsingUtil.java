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

import java.util.List;

import org.eclipse.viatra.query.patternlanguage.emf.internal.XtextInjectorProvider;
import org.eclipse.viatra.query.patternlanguage.emf.specification.SpecificationBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.patternparser.PatternParser;
import org.eclipse.viatra.query.patternlanguage.emf.util.patternparser.PatternParsingResults;
import org.eclipse.viatra.query.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

import com.google.common.collect.Lists;
import com.google.inject.Injector;

/**
 * Allows the caller to parse VIATRA query patterns provided in text format. IMPORTANT: This API class assumes that the Xtext parser infrastructure is already initialized. If its not,  {@link ViatraQueryException} is thrown.
 * 
 * @throws ViatraQueryException
 *  
 * @author Peter Lunk
 * @since 1.5
 */
public class PatternParsingUtil {
    public static final String PPERROR = "The VIATRA query language parser infrastructure is not initialized, pattern parsing is not supported.";

    public static List<IQuerySpecification<?>> parsePatterns(String patternString) throws ViatraQueryException {
        Injector injector = XtextInjectorProvider.INSTANCE.getInjector();
        if (injector != null) {
            PatternParser parser = injector.getInstance(PatternParser.class);
            PatternParsingResults results = parser.parse(patternString);
            if (!results.hasError()) {
                SpecificationBuilder builder = new SpecificationBuilder();
                List<Pattern> patterns = results.getPatterns();
                List<IQuerySpecification<?>> specList = Lists.newArrayList();
                for (Pattern pattern : patterns) {
                    boolean isPrivate = CorePatternLanguageHelper.isPrivate(pattern);
                    try {
                        IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> spec = builder
                                .getOrCreateSpecification(pattern);
                        if (!isPrivate) {
                            specList.add(spec);
                        }
                    } catch (ViatraQueryException e) {
                        ViatraQueryLoggingUtil.getDefaultLogger().error(e.getMessage(), e);
                    }
                }
                return specList;
            }
            return Lists.newArrayList();
        } else {
            throw new ViatraQueryException(PPERROR, "Error while parsing patterns.");
        }

    }
}
