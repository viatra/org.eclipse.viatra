/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.composition

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.types.ITypeInferrer
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageValidator
import org.eclipse.viatra.query.patternlanguage.emf.validation.IssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations
import org.eclipse.xtext.common.types.JvmType

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class SingleConstraintCompositionTest extends AbstractValidatorTest {

    @Inject
    ParseHelper<PatternModel> parseHelper

    @Inject
    EMFPatternLanguageValidator validator

    @Inject
    ITypeInferrer typeInferrer

    @Inject
    Injector injector
    
    @Inject extension ValidationTestHelper
    
    @Inject
    IJvmModelAssociations associations

    ValidatorTester<EMFPatternLanguageValidator> tester

    @Before
    def void initialize() {
        tester = new ValidatorTester(validator, injector)
    }

    @Test
    def void noEmbeddedPattern() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern transitive(p : EClass) {
                EClass.eSuperTypes(p, _p2);
            }
            '''
        )
        val constraint = model.patterns.get(0).bodies.get(0).constraints.get(0) as PathExpressionConstraint
        tester.validate(model).assertOK
        Assert.assertArrayEquals(#{}, associations.getJvmElements(constraint).filter(JvmType).toList.toArray)
    }
    
    @Test
    def void validClosure() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern transitive(p : EClass) {
                EClass.eSuperTypes+(p, _p2);
            }
            '''
        )
        tester.validate(model).assertOK;
    }

    @Test
    def void validClosureChain() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern transitive(p : EClass) {
                EClass.eStructuralFeatures.eType+(p, _p2);
            }
            '''
        )
        tester.validate(model).assertOK;
    }
    @Test
    def void validReflexiveClosure() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern transitive(p : EClass) {
                EClass.eSuperTypes*(p, _p2);
            }
            '''
        )
        tester.validate(model).assertOK;
    }
    @Test
    def void validReflexiveClosureChain() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern transitive(p : EClass) {
                EClass.eReferences.eType*(p, _p2);
            }
            '''
        )
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes.FEATURE_NOT_REPRESENTABLE)
        )
    }

    @Test
    def void misTypedClosure() {
        val model = parseHelper.parse(
            '''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern transitive(p : EClass, b : EStructuralFeature) {
                    EClass.eStructuralFeatures+(p, b);
                }
            '''
        )
        tester.validate(model).assertError(IssueCodes::TRANSITIVE_PATTERNCALL_TYPE)
    }

    @Test
    def void misTypedReflexiveClosure() {
        val model = parseHelper.parse(
            '''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern transitive(p : EClass, b : EStructuralFeature) {
                    EClass.eStructuralFeatures*(p, b);
                }
            '''
        )
        tester.validate(model).assertError(IssueCodes::TRANSITIVE_PATTERNCALL_TYPE)
    }

    @Test
    def void negatedClosure() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern transitive(p : Pattern) {
                neg EClass.eSuperTypes+(p,_p2);
            }
            '''
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_NOT_APPLICABLE));
    }
    
    @Test
    def void negatedReflexiveClosure() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern transitive(p : EClass) {
                neg EClass.eSuperTypes*(p,_p2);
            }
            '''
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_NOT_APPLICABLE));
    }

    @Test
    def void aggregatedClosure() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern transitive(p : EClass) = {
                3 == count EClass.eSuperTypes+(p, _p2);
            }
            '''
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_NOT_APPLICABLE));
    }
    
    @Test
    def void aggregatedReflexiveClosure() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern transitive(p : EClass) = {
                3 == count EClass.eSuperTypes*(p, _p2);
            }
            '''
        )
        tester.validate(model).assertAll(getErrorCode(IssueCodes::TRANSITIVE_PATTERNCALL_NOT_APPLICABLE));
    }
    
    @Test
    def void validNegation() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern simpleNegation(p : EClassifier) {
                EClassifier(p); // not required here per se; but added for readability
                neg EClass(p);
            }
            '''
        )
        tester.validate(model).assertOK;
    }

    @Test
    def void testCountAllPassed() {
        parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern callerPattern(c : Circle, output) {
                Circle(c);
                Red(v);
                output == count Circle.red(c, v);
                Red.redness(_h, output);
            }'''
        ).assertNoErrors
    }

    @Test
    def void testCountTypeChecking() {
        val parsed = parseHelper.parse('''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern parameterCount(call : PatternCall, c) {
                c == count PatternCall.parameters(call, _);
            }
        ''')
        parsed.assertNoErrors
        val param_c = parsed.patterns.get(0).parameters.get(1)
        val inferredType = typeInferrer.getType(param_c)
        Assert.assertEquals("Parameter c is expected to have a type of Integers", Integer, (inferredType as JavaTransitiveInstancesKey).instanceClass)
    }
    
    @Test
    def void testCountWithAggregate() {
        val parsed = parseHelper.parse('''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern parameterCount(c) {
                c == count PatternCall.parameters(_, #param);   
            }
        ''')
        tester.validate(parsed).assertAll(
            getErrorCode(IssueCodes::INVALID_AGGREGATOR_PARAMETER)
        )
    }
    
    @Test
    def void testCountWithAggregate2() {
        val parsed = parseHelper.parse('''
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern parameterCount(c) {
                c == count PatternCall.parameters(#param1, #param2);   
            }
        ''')
        tester.validate(parsed).assertAll(
            getErrorCode(IssueCodes::INVALID_AGGREGATOR_PARAMETER),
            getErrorCode(IssueCodes::INVALID_AGGREGATOR_PARAMETER)
        )
    }

    @Test
    def void testMissingComposition() {
        var parsed = parseHelper.parse(
            '''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern callerPattern(p : Pattern, output : java Integer) {
                Pattern(p);
                output == count Pattern.nam(p, _anyv);
            }'''
        );
        parsed.assertError(
            PatternLanguagePackage::eINSTANCE.referenceType,
            Diagnostic::LINKING_DIAGNOSTIC,
            "nam cannot be resolved"
        )

    }

    @Test
    def void testSumAggregator() {
        var parsed = parseHelper.parse(
            '''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
                import "http://www.eclipse.org/emf/2002/Ecore"
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                        
                pattern belowAverage(c : Circle, r : Red) {
                    Circle.red(c, r);
                    Red.redness(r, value);
                
                    C == count Circle.red(c, _);
                    S == sum Red.redness(r, #v);
                    A == eval(S / C);
                    check(value < A as Integer);
                }
            '''
        )
        
        val body = parsed.patterns.get(0).bodies.get(0)
        val variable_S = body.variables.findFirst[name == "S"]
        Assert.assertEquals(
            "Variable S is expected to have a type of Integer", 
            Integer,
            (typeInferrer.getType(variable_S) as JavaTransitiveInstancesKey).instanceClass
        )
        
        val variable_C = body.variables.findFirst[name == "C"]
        Assert.assertEquals(
            "Variable C is expected to have a type of Integer", 
            Integer,
            (typeInferrer.getType(variable_C) as JavaTransitiveInstancesKey).instanceClass
        )
        
        val variable_A = body.variables.findFirst[name == "A"]
        Assert.assertEquals(
            "Variable A is expected to have a type of Integer", 
            Integer,
            (typeInferrer.getType(variable_A) as JavaTransitiveInstancesKey).instanceClass
        )
        
        parsed.assertNoErrors
    }
    
    @Test
    def void testAvgAggregator() {
        var parsed = parseHelper.parse(
            '''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
                import "http://www.eclipse.org/emf/2002/Ecore"
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                        
                pattern positiveAverage(c : Circle, r : Red) {
                    Circle.red(c, r);
                
                    A == avg Red.redness(r, #v);
                    check(A as Double > 0);
                }
            '''
        )
        
        val body = parsed.patterns.get(0).bodies.get(0)
        
        val variable_A = body.variables.findFirst[name == "A"]
        Assert.assertEquals(
            "Variable A is expected to have a type of Double", 
            Double,
            (typeInferrer.getType(variable_A) as JavaTransitiveInstancesKey).instanceClass
        )
        
        parsed.assertNoErrors
    }

    @Test
    def void testMinAggregator() {
        var parsed = parseHelper.parse(
            '''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern smallestValue(c : Circle, redness : EInt) {
                    Circle.red(c, parameter);
                    redness == min Red.redness(parameter, #_);
                }
            '''
        )
        parsed.assertNoErrors
    }
    
    @Test
    def void testMinAggregatorChain() {
        var parsed = parseHelper.parse(
            '''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern smallestValue(c : Circle, redness : EInt) {
                    redness == min Circle.red.redness(c, #_);
                }
            '''
        )
        parsed.assertNoErrors
        tester.validate(parsed).assertAll(
            getWarningCode(IssueCodes.AGGREGATED_FEATURE_CHAIN)
        )
    }

    @Test
    def void testAggregatorMissingMarker() {
        var parsed = parseHelper.parse('''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern smallestValue(call : PatternCall, value : EInt) {
                    PatternCall.parameters(call, parameter);
                    value == min Red.redness(parameter, _);
                }
            '''
        )
        parsed.assertError(
            PatternLanguagePackage::eINSTANCE.aggregatedValue,
            IssueCodes.INVALID_AGGREGATOR_PARAMETER
        )
    }
    
    @Test
    def void testAggregatorDuplicateMarkers() {
        var parsed = parseHelper.parse('''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern smallestValue(value) {
                    value == min Red.redness(#_, #_);
                }
            '''
        )
        tester.validate(parsed).assertAll(
            getErrorCode(IssueCodes.INVALID_AGGREGATOR_PARAMETER),
            getErrorCode(IssueCodes.INVALID_AGGREGATOR_PARAMETER)
        )
    }
    
    @Test
    def void testAggregatorDubiusVariableReuse() {
        var parsed = parseHelper.parse('''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern smallestValue(value) {
                    value == min Red.redness(_, #value);
                }
            '''
        )
        tester.validate(parsed).assertAll(
            getErrorCode(IssueCodes.DUBIUS_VARIABLE_NAME),
            getWarningCode(IssueCodes::MISSING_PARAMETER_TYPE)
        )
    }
    
    @Test
    def void testInvalidAggregator() {
        var parsed = parseHelper.parse('''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern smallestValue(value) {
                    value == Integer Red.redness(_, #v);
                }
            '''
        )
        tester.validate(parsed).assertAll(
            getErrorCode(IssueCodes.INVALID_AGGREGATOR)
        )
    }
    
    @Test
    def void testInvalidAggregatorContext() {
        var parsed = parseHelper.parse('''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern smallestValue(value : Red) {
                    Red.redness(value, #v);
                }
            '''
        )
        tester.validate(parsed).assertAll(
            getErrorCode(IssueCodes.INVALID_AGGREGATE_CONTEXT)
        )
    }
    
    @Test
    def void testInvalidAggregatorContext2() {
        var parsed = parseHelper.parse('''
                package org.eclipse.viatra.query.patternlanguage.emf.tests
                
                import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"
                import "http://www.eclipse.org/emf/2002/Ecore"
                
                pattern smallestValue(value : Red) {
                    Red.redness(value, #);
                }
            '''
        )
        tester.validate(parsed).assertAll(
            getErrorCode(IssueCodes.INVALID_AGGREGATE_CONTEXT)
        )
    }
    
    @Test
    def void mistypedAggregator() {
        var parsed = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern mistypedAggregator(number : java Integer) {
                number == max Pattern.bodies(_, #count);
            }
            '''
        )
        tester.validate(parsed).assertAll(
            getErrorCode(IssueCodes.VARIABLE_TYPE_INVALID_ERROR)
        )
    }
    
    @Test
    def void supertypedCountAggregator() {
        var parsed = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern p(a : Variable){
                Expression(a);
                0 == count Variable(a);
            }
            '''
        )
        tester.validate(parsed).assertOK
    }
    
    @Test
    def void mistypedCountAggregator() {
        var parsed = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern p(a : Pattern){
                0 == count Variable(a);
            }
            '''
        )
        tester.validate(parsed).assertAll(
            getWarningCode(IssueCodes.MISTYPED_PARAMETER)
        )
    }
    
    @Test
    def void javaTypeEmbed1() {
        var parsed = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern p(a : java String){
                a == "abc";
                neg java String(a);
            }
            '''
        )
        parsed.assertError(PatternLanguagePackage.Literals.PATTERN_BODY, Diagnostic.SYNTAX_DIAGNOSTIC, "'neg'")
    }
    
    @Test
    def void javaTypeEmbed2() {
        var parsed = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern p(a : java String){
                a == "abc";
                java String+(a);
            }
            '''
        )
        parsed.assertError(PatternLanguagePackage.Literals.TYPE_CHECK_CONSTRAINT, Diagnostic.SYNTAX_DIAGNOSTIC, "extraneous input")
    }
    
    @Test
    def void javaTypeEmbed3() {
        var parsed = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/PatternLanguage"
            
            pattern p(a : java Integer){
                a == count java String(a);
            }
            '''
        )
        parsed.assertError(PatternLanguagePackage.Literals.COMPARE_CONSTRAINT, Diagnostic.SYNTAX_DIAGNOSTIC, "'count'")
    }
}
