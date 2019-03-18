/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.patternlanguage.emf.tests.types

import org.junit.runner.RunWith
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import com.google.inject.Inject
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import static org.junit.Assert.*
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage
import org.eclipse.xtext.diagnostics.Diagnostic
import org.junit.Test
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class EReferenceResolutionTest {

    @Inject
    ParseHelper<PatternModel> parseHelper
    
    @Inject extension ValidationTestHelper
    
    @Test
    def referenceResolution() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(Name : Pattern, Body) = {
                Pattern.bodies(Name, Body);
            }
        ')
        model.assertNoErrors
        val pattern = model.patterns.get(0)
        val constraint = pattern.bodies.get(0).constraints.get(0) as PathExpressionConstraint
        val type = PatternLanguageHelper.getPathExpressionEMFTailType(constraint).get
        assertEquals(type, PatternLanguagePackage$Literals::PATTERN_BODY)		
    }
    
    @Test
    def referenceResolutionChain() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(Name : Pattern, Constraint) = {
                Pattern.bodies.constraints(Name, Constraint);
            }
        ')
        model.assertNoErrors
        val pattern = model.patterns.get(0)
        val constraint = pattern.bodies.get(0).constraints.get(0) as PathExpressionConstraint
        val interimType = constraint.edgeTypes.get(0)
        assertEquals(interimType.refname.EType, PatternLanguagePackage$Literals::PATTERN_BODY)		
        val type = constraint.edgeTypes.get(1)
        assertEquals(type.refname.EType, PatternLanguagePackage$Literals::CONSTRAINT)		
    }
    
    @Test
    def referenceResolutionChain4() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(m : PatternModel, c : Constraint) = {
                PatternModel.patterns.bodies.constraints(m, c);
            }
        ''')
        model.assertNoErrors
        val pattern = model.patterns.get(0)
        val constraint = pattern.bodies.get(0).constraints.get(0) as PathExpressionConstraint
        val interimType = constraint.edgeTypes.get(1)
        assertEquals(interimType.refname.EType, PatternLanguagePackage$Literals::PATTERN_BODY)		
        val type = constraint.edgeTypes.get(2)
        assertEquals(type.refname.EType, PatternLanguagePackage$Literals::CONSTRAINT)		
    }
    
    @Test
    def referenceResolutionEscapedKeyword() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern keywordAsIdentifier(A,B) = {
                EClassifierConstraint.^var(A,B); 
            }
        ')
        model.assertNoErrors
        val pattern = model.patterns.get(0)
        val constraint = pattern.bodies.get(0).constraints.get(0) as PathExpressionConstraint
        val type = PatternLanguageHelper.getPathExpressionTailType(constraint).get
        assertEquals(type.refname, PatternLanguagePackage$Literals::UNARY_TYPE_CONSTRAINT__VAR)
    }
    
    @Test
    def referenceResolutionInvalid(){
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"

            pattern resolutionTest(Name : Pattern, Constraint) = {
                Pattern.notExist(Name, Constraint);
            }
        ')
        model.assertError(PatternLanguagePackage$Literals::REFERENCE_TYPE,
            Diagnostic::LINKING_DIAGNOSTIC, "notExist")
    }
}