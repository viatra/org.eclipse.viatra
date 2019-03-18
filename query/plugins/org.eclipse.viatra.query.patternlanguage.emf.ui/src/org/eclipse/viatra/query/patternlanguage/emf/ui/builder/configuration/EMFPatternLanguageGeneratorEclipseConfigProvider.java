/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.builder.configuration;

import javax.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig;
import org.eclipse.xtext.ui.resource.IStorage2UriMapper;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.xbase.compiler.IGeneratorConfigProvider;

import com.google.common.collect.Iterables;

/**
 * This class is a modified version of {@link org.eclipse.xtext.xbase.ui.builder.EclipseGeneratorConfigProvider} that
 * allows loading configuration options specific to the pattern language.
 * 
 * @author Zoltan Ujhelyi
 * @since 1.7
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageGeneratorEclipseConfigProvider implements IGeneratorConfigProvider {

    @Inject
    EMFPatternLanguageBuilderPreferenceAccess builderPreferenceAccess;
    
    @Inject
    IStorage2UriMapper storage2UriMapper;

    @Override
    public EMFPatternLanguageGeneratorConfig get(EObject context) {
        EMFPatternLanguageGeneratorConfig result = new EMFPatternLanguageGeneratorConfig();
        IProject project = null;
        if (context.eResource() != null) {
            Pair<IStorage, IProject> pair = Iterables.getFirst(storage2UriMapper.getStorages(context.eResource().getURI()), null);
            if (pair != null) {
                project = pair.getSecond();
            }
        }
        builderPreferenceAccess.loadBuilderPreferences(result, project);
        return result;
    }

}
