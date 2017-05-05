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

package org.eclipse.viatra.query.patternlanguage.emf.tests.types

import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import com.google.inject.Inject
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EClassifierConstraint
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguagePackage
import static org.junit.Assert.*
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.emf.ecore.EcorePackage

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class EClassResolutionTest {
    @Inject
    ParseHelper<PatternModel> parseHelper
    
    @Inject extension ValidationTestHelper
    
    @Test
    def eClassResolutionSuccess() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ') as PatternModel
        model.assertNoErrors
        val pattern = model.patterns.get(0)
        val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
        val type = constraint.type as ClassType
        assertEquals(type.classname, PatternLanguagePackage$Literals::PATTERN)		
    }
    
    @Test
    def eClassifierResolutionSuccess() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern ECoreNamedElement(Name) = {
                EString(Name);
            }
        ') as PatternModel
        model.assertNoErrors
        val pattern = model.patterns.get(0)
        val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
        val type = constraint.type as ClassType
        assertEquals(type.classname, EcorePackage$Literals::ESTRING)		
    }

    @Test
    def eClassResolutionFailed() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/PatternLanguage"

            pattern resolutionTest(Name) = {
                UndefinedType(Name);
            }
        ') as PatternModel
        val pattern = model.patterns.get(0)
        val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
        val type = constraint.type as ClassType
        type.assertError(EMFPatternLanguagePackage$Literals::CLASS_TYPE, 
            Diagnostic::LINKING_DIAGNOSTIC, "UndefinedType")		
    }
    
    @Test
    def eClassResolutionFailedMissingImport() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            pattern resolutionTest(Name) = {
                Pattern(Name);
            }
        ') as PatternModel
        val pattern = model.patterns.get(0)
        val constraint = pattern.bodies.get(0).constraints.get(0) as EClassifierConstraint
        val type = constraint.type as ClassType
        type.assertError(EMFPatternLanguagePackage$Literals::CLASS_TYPE, 
            Diagnostic::LINKING_DIAGNOSTIC, "Pattern")		
    }
}