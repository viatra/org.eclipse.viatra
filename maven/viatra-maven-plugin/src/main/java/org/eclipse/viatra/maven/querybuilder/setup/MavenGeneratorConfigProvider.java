/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.maven.querybuilder.setup;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageGeneratorConfig;
import org.eclipse.xtext.xbase.compiler.GeneratorConfig;
import org.eclipse.xtext.xbase.compiler.IGeneratorConfigProvider;

/**
 * 
 * @since 1.7
 */
public class MavenGeneratorConfigProvider implements IGeneratorConfigProvider {

    private static EMFPatternLanguageGeneratorConfig config;

    public static void setGeneratorConfig(EMFPatternLanguageGeneratorConfig config) {
        MavenGeneratorConfigProvider.config = config;
    }

    @Override
    public GeneratorConfig get(EObject context) {
        return config;
    }

}
