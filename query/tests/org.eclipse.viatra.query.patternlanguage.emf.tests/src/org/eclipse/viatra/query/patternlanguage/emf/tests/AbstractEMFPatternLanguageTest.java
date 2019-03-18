/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests;

import java.util.Collection;

import org.eclipse.emf.ecore.EValidator;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.eclipse.xtext.junit4.AbstractXtextTests;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

@RunWith(XtextRunner.class)
@InjectWith(CustomizedEMFPatternLanguageInjectorProvider.class)
public abstract class AbstractEMFPatternLanguageTest extends AbstractXtextTests {

    static final ImmutableSet<String> defaultPackages = ImmutableSet
            .of("http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage");
    
    @Inject
    EValidator.Registry validationRegistry;
    @Inject
    PatternLanguagePackage languagePackage;

    public String addImports(String content, Collection<String> packages) {
        StringBuilder sb = new StringBuilder();
        for (String pack : packages) {
            sb.append("import \"" + pack + "\"\n");
        }
        sb.append(content);
        return sb.toString();
    }

    public String addDefaultImports(String content) {
        return addImports(content, defaultPackages);
    }

}
