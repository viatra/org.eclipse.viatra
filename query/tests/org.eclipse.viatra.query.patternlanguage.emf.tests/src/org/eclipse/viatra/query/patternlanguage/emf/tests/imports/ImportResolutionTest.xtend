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

import org.eclipse.xtext.testing.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.testing.InjectWith
import com.google.inject.Inject
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import static org.junit.Assert.*
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class ImportResolutionTest {
    @Inject
    ParseHelper<PatternModel> parseHelper
    
    @Inject extension ValidationTestHelper
    
    @Test
    def importResolution() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ')
        model.assertNoErrors
        val importDecl = PatternLanguageHelper::getAllPackageImports(model).get(0)
        val ePackage = importDecl.EPackage
        assertNotNull(ePackage)
        assertEquals(ePackage.nsURI, "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage")
    }
    
    @Test
    def importResolutionMultiplePackages() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(b : PatternBody, c : EClassifierConstraint) = {
                PatternBody(b);
                PatternBody.constraints(b, c);
            }
        ')
        model.assertNoErrors
    }
    
    @Test
    def importResolutionExtended() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage" as avql

            pattern resolutionTest(name : Pattern) = {
                avql::Pattern(name);
            }
        ')
        model.assertNoErrors
    }
    
    @Test
    def importResolutionExtendedMultiplePackages() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage" as avql
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage" as vql

            pattern resolutionTest(b : PatternBody, c : vql::EClassifierConstraint) = {
                avql::PatternBody(b);
                PatternBody.constraints(b, c);
            }
        ')
        model.assertNoErrors
    }
    
    @Test
    def multipleImportResolution() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage";
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage";

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ')
        model.assertNoErrors
        val imports = PatternLanguageHelper::getAllPackageImports(model)
        var importDecl = imports.get(0)
        var ePackage = importDecl.EPackage
        assertNotNull(ePackage)
        assertEquals(ePackage.nsURI, "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage")
        importDecl = imports.get(1)
        ePackage = importDecl.EPackage
        assertNotNull(ePackage)
        assertEquals(ePackage.nsURI, "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage")
    }
    
    @Test
    def importResolutionFailed() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://nonexisting.package.uri"

            pattern resolutionTest(Name) = {
                Pattern(Name2);
            }
        ')
        val importDecl = model.importPackages.packageImport.get(0)
        importDecl.assertError(PatternLanguagePackage$Literals::PACKAGE_IMPORT,
            Diagnostic::LINKING_DIAGNOSTIC, "http://nonexisting.package.uri"
        )
    }
    
    
}