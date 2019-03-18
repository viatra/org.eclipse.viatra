/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util.internal;

import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParser;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.impl.ResourceSetBasedResourceDescriptions;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Customized resource descriptions implementation that ignores synthetic resource contents for inclusion in the Xtext
 * index. Used for the {@link StandaloneParserWithSeparateModules} Guice module.
 */
public class PatternParserResourceDescriptions extends ResourceSetBasedResourceDescriptions {

    @Override
    public Iterable<IResourceDescription> getAllResourceDescriptions() {
        return Iterables.filter(super.getAllResourceDescriptions(),
                Predicates.not(rd -> rd.getURI().toString().contains(PatternParser.SYNTHETIC_URI_PREFIX)));
    }

}
