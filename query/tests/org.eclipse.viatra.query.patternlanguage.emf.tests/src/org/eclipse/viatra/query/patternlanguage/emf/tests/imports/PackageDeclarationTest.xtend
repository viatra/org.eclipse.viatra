/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.imports

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage$Literals
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class PackageDeclarationTest {
    @Inject
    ParseHelper<PatternModel> parseHelper
    
    @Inject extension ValidationTestHelper
    
    @Test
    def simplePackage() {
        val model = parseHelper.parse('
            package school
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ')
        model.assertNoErrors
        val packageName = model.packageName
        assertEquals(packageName, "school");
    }
    @Test
    def noPackage() {
        val model = parseHelper.parse('
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ')
        model.assertNoErrors
    }
    @Test
    def hierarchicPackage() {
        val model = parseHelper.parse('
            package hu.bme.mit.school;
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ')
        model.assertNoErrors
        val packageName = model.packageName
        assertEquals(packageName, "hu.bme.mit.school");
    }
    @Test
    def packageAlias() {
        val model = parseHelper.parse('
            package hu.bme.mit.school;
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage" as pl

            pattern resolutionTest(Name) = {
                pl::Pattern(Name);
            }
        ')
        model.assertNoErrors
        val packageName = model.packageName
        assertEquals(packageName, "hu.bme.mit.school");
    }
    @Test
    def packageAliasWithSemicolon() {
        val model = parseHelper.parse('
            package hu.bme.mit.school;
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage" as pl;

            pattern resolutionTest(Name) = {
                pl::Pattern(Name);
            }
        ')
        model.assertNoErrors
        val packageName = model.packageName
        assertEquals(packageName, "hu.bme.mit.school");
    }
    
    @Test
    def capitalizedPackageName() {
                val model = parseHelper.parse('
            package School
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ')
        model.assertError(PatternLanguagePackage$Literals::PATTERN_MODEL, IssueCodes::PACKAGE_NAME_MISMATCH)
    }
}