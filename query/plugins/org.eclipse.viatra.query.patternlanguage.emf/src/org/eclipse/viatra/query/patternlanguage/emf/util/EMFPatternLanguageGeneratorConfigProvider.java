/*******************************************************************************
 * Copyright (c) 2010-2017, stampie, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   stampie - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.workspace.IProjectConfig;
import org.eclipse.xtext.workspace.IProjectConfigProvider;
import org.eclipse.xtext.xbase.compiler.GeneratorConfig;
import org.eclipse.xtext.xbase.compiler.IGeneratorConfigProvider;

/**
 * @author Zoltan Ujhelyi
 * @since 1.7
 *
 */
@SuppressWarnings("restriction")
public class EMFPatternLanguageGeneratorConfigProvider implements IGeneratorConfigProvider {

    @Inject
    private IProjectConfigProvider projectConfigProvider;
    @Inject
    private Logger logger;

    @Override
    public GeneratorConfig get(EObject context) {
        EMFPatternLanguageGeneratorConfig config = new EMFPatternLanguageGeneratorConfig();
        ResourceSet resourceSet = EcoreUtil2.getResourceSet(context);
        if (resourceSet != null) {
            IProjectConfig projectConfig = projectConfigProvider.getProjectConfig(resourceSet);
            if (projectConfig != null) {
                URI configFilePath = projectConfig.getPath().appendSegments(new String[] {".settings", "org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguage.prefs"});
                File f = new File(configFilePath.toFileString());
                if (f.canRead()) {
                    Properties vqlCompilerSettings = new Properties();
                    try {
                        vqlCompilerSettings.load(new FileInputStream(f));
                        config.parseBuilderConfigurationPropertiesFile(vqlCompilerSettings);
                    } catch (IOException e) {
                        logger.warn(e);
                    }
                }
            }
        }
        return config;
    }

}
