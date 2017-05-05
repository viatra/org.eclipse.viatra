/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.imports

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage$Literals
import org.eclipse.viatra.query.patternlanguage.validation.IssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class PackageDeclarationTest {
    @Inject
    ParseHelper<PatternModel> parseHelper
    
    @Inject extension ValidationTestHelper
    
    @Test
    def simplePackage() {
        val model = parseHelper.parse('
            package school
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ') as PatternModel
        model.assertNoErrors
        val packageName = model.packageName
        assertEquals(packageName, "school");
    }
    @Test
    def noPackage() {
        val model = parseHelper.parse('
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ') as PatternModel
        model.assertNoErrors
    }
    @Test
    def hierarchicPackage() {
        val model = parseHelper.parse('
            package hu.bme.mit.school;
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ') as PatternModel
        model.assertNoErrors
        val packageName = model.packageName
        assertEquals(packageName, "hu.bme.mit.school");
    }
    
    @Test
    def capitalizedPackageName() {
                val model = parseHelper.parse('
            package School
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ') as PatternModel
        model.assertError(PatternLanguagePackage$Literals::PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
    }
}