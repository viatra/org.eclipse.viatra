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

import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.xtext.testing.InjectWith
import org.junit.runner.RunWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import com.google.inject.Inject
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.junit.Test
import org.eclipse.xtext.junit4.validation.ValidatorTester
import com.google.inject.Injector
import org.junit.Before
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class EnumValidationTest extends AbstractValidatorTest {
    
    @Inject
    ParseHelper<PatternModel> parseHelper
    @Inject
    EMFPatternLanguageValidator validator
    @Inject
    Injector injector
    
    ValidatorTester<EMFPatternLanguageValidator> tester
    
    @Inject extension ValidationTestHelper
    
    @Before
    def void initialize() {
        tester = new ValidatorTester(validator, injector)
    }
    
    @Test
    def validateEnum() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/GenModel"

            pattern resolutionTest(Model : GenModel) = {
                GenModel(Model);
                GenModel.runtimeVersion(Model, ::EMF23);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertOK
    }
    @Test
    def validateQualifiedEnum() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/GenModel"

            pattern resolutionTest(Model : GenModel) = {
                GenModel(Model);
                GenModel.runtimeVersion(Model, GenRuntimeVersion::EMF23);
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertOK
    }
    @Test
    def validateQualifiedEnumWithEquality() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/GenModel"

            pattern resolutionTest(Model: GenModel) = {
                GenModel(Model);
                GenModel.runtimeVersion(Model, Version);
                Version == GenRuntimeVersion::EMF23;
            }
        ')
        model.assertNoErrors
        tester.validate(model).assertOK
    }

    @Test
    def validateIncorrectEnum() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/GenModel"

            pattern resolutionTest(Model : GenModel) = {
                GenModel(Model);
                GenModel.runtimeVersion(Model, GenDelegationKind::None);
            }
        ')
        tester.validate(model).assertError(IssueCodes::INVALID_ENUM_LITERAL)
    }
    @Test
    def validateEnumConstraintPatternCall() {
        val model = parseHelper.parse('
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/GenModel"


            pattern runtimeVersion(Version) = {
                GenRuntimeVersion(Version);
            }

            pattern call(c : GenModel) = {
                GenModel(c);
                find runtimeVersion(GenRuntimeVersion::EMF24);
            }
        ')
        tester.validate(model).assertAll(
            getInfoCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
}