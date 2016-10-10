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

import com.google.inject.Inject
import com.google.inject.Injector
import org.eclipse.viatra.query.patternlanguage.emf.tests.EMFPatternLanguageInjectorProvider
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFIssueCodes
import org.eclipse.viatra.query.patternlanguage.emf.validation.EMFPatternLanguageJavaValidator
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.eclipse.xtext.junit4.validation.ValidatorTester
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.typing.ITypeInferrer
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.viatra.query.runtime.matchers.context.common.JavaTransitiveInstancesKey
import org.eclipse.viatra.query.patternlanguage.emf.tests.pltest.PltestPackage
import org.eclipse.viatra.query.patternlanguage.validation.IssueCodes

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class TypeInferenceTest extends AbstractValidatorTest {
	
	@Inject
	ParseHelper<PatternModel> parseHelper
	
	@Inject
	EMFPatternLanguageJavaValidator validator
	
	@Inject
	Injector injector
	
	@Inject
	private ITypeInferrer typeInferrer
	@Inject
	extension private EMFTypeSystem typeSystem
	
	
	ValidatorTester<EMFPatternLanguageJavaValidator> tester
	
	@Inject extension ValidationTestHelper
	extension EcorePackage ecorePackage = EcorePackage::eINSTANCE
	extension PltestPackage pltestPackage = PltestPackage::eINSTANCE
	
	@Before
	def void initialize() {
		tester = new ValidatorTester(validator, injector)
	}
	
	@Test
	def zeroLevelType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern first(class1) = {
				EClass(class1);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE))
		
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeInferrer.getType(param)
		assertEquals(classifierToInputKey(EClass), type) 
	}
	
	@Test
	def firstLevelFindType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern first(class1) = {
				EClass(class1);
			}

			pattern second(class2) = {
				find first(class2);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param1 = model.patterns.get(0).parameters.get(0)
		val param2 = model.patterns.get(1).parameters.get(0)
		val type1 = typeInferrer.getType(param1)
		val type2 = typeInferrer.getType(param2)
		assertEquals(classifierToInputKey(EClass), type1)
		assertEquals(classifierToInputKey(EClass), type2)
	}
	
	@Test
	def secondLevelFindType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern first(class1) = {
				EClass(class1);
			}

			pattern second(class2) = {
				find first(class2);
			}

			pattern third(class3) = {
				find second(class3);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param1 = model.patterns.get(0).parameters.get(0)
		val param2 = model.patterns.get(1).parameters.get(0)
		val param3 = model.patterns.get(2).parameters.get(0)
		val type1 = typeInferrer.getType(param1)
		val type2 = typeInferrer.getType(param2)
		val type3 = typeInferrer.getType(param3)
		assertEquals(classifierToInputKey(EClass), type1)
		assertEquals(classifierToInputKey(EClass), type2)
		assertEquals(classifierToInputKey(EClass), type3)
	}
	
	@Test
	def zeroLevelPathType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern firstPath(class1, attribute1) = {
				EClass.eStructuralFeatures(class1, attribute1);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param1 = model.patterns.get(0).parameters.get(0)
		val param2 = model.patterns.get(0).parameters.get(1)
		val type1 = typeInferrer.getType(param1)
		val type2 = typeInferrer.getType(param2)
		assertEquals(classifierToInputKey(EClass), type1)
		assertEquals(classifierToInputKey(EStructuralFeature), type2)
	}
	
	@Test
	def firstLevelPathType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern firstPath(class1, attribute1) = {
				EClass.eStructuralFeatures(class1, attribute1);
			}

			pattern secondPath(class2, attribute2) = {
				find firstPath(class2, attribute2);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param11 = model.patterns.get(0).parameters.get(0)
		val param21 = model.patterns.get(0).parameters.get(1)
		val param12 = model.patterns.get(1).parameters.get(0)
		val param22 = model.patterns.get(1).parameters.get(1)
		val type11 = typeInferrer.getType(param11)
		val type21 = typeInferrer.getType(param21)
		val type12 = typeInferrer.getType(param12)
		val type22 = typeInferrer.getType(param22)
		assertEquals(classifierToInputKey(EClass), type11)
		assertEquals(classifierToInputKey(EClass), type12)
		assertEquals(classifierToInputKey(EStructuralFeature), type21)
		assertEquals(classifierToInputKey(EStructuralFeature), type22)
	}
	
	@Test
	def injectivityConstraintTest() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern injectivity1(class1, class2) = {
				EClass(class1);
				class1 == class2;
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param1 = model.patterns.get(0).parameters.get(0)
		val param2 = model.patterns.get(0).parameters.get(1)
		val type1 = typeInferrer.getType(param1)
		val type2 = typeInferrer.getType(param2)
		assertEquals(classifierToInputKey(EClass), type1)
		assertEquals(classifierToInputKey(EClass), type2)
	}
	
	@Test
	def mistypedParameter() {
	    // Although Child3 is not a subtype of parameter, they do have a common subtype so the pattern is ok
		val model = parseHelper.parse('''
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

			pattern parameterTest(parameter : GrandChild) = {
				GrandChild2(parameter); 
			}
		''')
		tester.validate(model).assertError(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR)
	    
	}
	@Test
	def notMistypedParameter() {
	    // Although Child3 is not a subtype of parameter, they do have a common subtype so the pattern is ok
		val model = parseHelper.parse('''
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

			pattern parameterTest(parameter : Interface) = {
				Child3(parameter); 
			}
		''')
		model.assertNoErrors
	    
	}
	
	@Test
	def parameterTest() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern parameterTest(parameter) = {
				EDataType(parameter); 
			} or { 
				EClass(parameter);
			} 
		')
		model.assertNoErrors
		tester.validate(model).assertAll(getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE))
		
		val parameter1 = model.patterns.get(0).parameters.get(0)
		val variable1 = model.patterns.get(0).bodies.get(0).variables.get(0)
		val variable2 = model.patterns.get(0).bodies.get(1).variables.get(0)
		val type1 = typeInferrer.getType(parameter1)
		val type2 = typeInferrer.getType(variable1)
		val type3 = typeInferrer.getType(variable2)
		assertEquals(classifierToInputKey(EClassifier), type1)
		assertEquals(classifierToInputKey(EDataType), type2)
		assertEquals(classifierToInputKey(EClass), type3)
	}
	
	@Test
	def parameterTest2() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern parameterTest2(parameter : EClassifier) = {
				EDataType(parameter); 
			} or { 
				EClass(parameter);
			} 
		')
		model.assertNoErrors
		tester.validate(model).assertOK
		
		val parameter1 = model.patterns.get(0).parameters.get(0)
		val variable1 = model.patterns.get(0).bodies.get(0).variables.get(0)
		val variable2 = model.patterns.get(0).bodies.get(1).variables.get(0)
		val type1 = typeInferrer.getType(parameter1)
		val type2 = typeInferrer.getInferredType(variable1)
		val type3 = typeInferrer.getInferredType(variable2)
		assertEquals(classifierToInputKey(EClassifier), type1)
		assertEquals(classifierToInputKey(EDataType), type2)
		assertEquals(classifierToInputKey(EClass), type3)
	}
	
	@Test
	def parameterTest3() {
		val model = parseHelper.parse('''
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern relatedClasses(src : EClass, trg : EClass) {
			    find feature+(src, trg);
			}

			pattern feature(cl : EClass, f) {
			    EClass.eStructuralFeatures(cl, f);
			} or {
			    EClass.eSuperTypes(cl, f);
			}
		''')
		
		val srcParam = model.patterns.get(0).parameters.get(0)
		val trgParam = model.patterns.get(0).parameters.get(1)
		val clParam = model.patterns.get(1).parameters.get(0)
		val fParam = model.patterns.get(1).parameters.get(1)
		
		val srcType = typeInferrer.getType(srcParam)
		val trgType = typeInferrer.getInferredType(trgParam)
		val clType = typeInferrer.getInferredType(clParam)
		val fType = typeInferrer.getInferredType(fParam)
		assertEquals(classifierToInputKey(EClass), srcType)
		assertEquals(classifierToInputKey(EClass), trgType)
		assertEquals(classifierToInputKey(EClass), clType)
		assertEquals(classifierToInputKey(ENamedElement), fType)
		
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
	}
	
	@Test
	def parameterTest4() {
		val model = parseHelper.parse('''
			import "http://www.eclipse.org/emf/2002/Ecore"

			pattern call(h : EAttribute) {
			    find testInheritance(h, _);
			}
			
			pattern testInheritance(a, b) {
			    EAttribute.eAttributeType(a, b);
			} or {
			    EReference.eReferenceType(a, b);
			}
		''')
		
		val hParam = model.patterns.get(0).parameters.get(0)
		val aParam = model.patterns.get(1).parameters.get(0)
		val bParam = model.patterns.get(1).parameters.get(1)
		
		val hType = typeInferrer.getType(hParam)
		val aType = typeInferrer.getInferredType(aParam)
		val bType = typeInferrer.getInferredType(bParam)
		assertEquals(classifierToInputKey(EAttribute), hType)
		assertEquals(classifierToInputKey(EStructuralFeature), aType)
		assertEquals(classifierToInputKey(EClassifier), bType)
		
		tester.validate(model).assertAll(
		    getWarningCode(EMFIssueCodes::FEATURE_NOT_REPRESENTABLE),
		    getWarningCode(EMFIssueCodes::FEATURE_NOT_REPRESENTABLE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
	}
	
	@Test
	def intLiteralType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			pattern literalValue(literalType) = {
				literalType == 10;
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeInferrer.getType(param)
		assertEquals(new JavaTransitiveInstancesKey((Integer)), type) 
	}
	
	@Test
	def stringLiteralType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			pattern literalValue(literalType) = {
				literalType == "helloworld";
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeInferrer.getType(param)
		assertEquals(new JavaTransitiveInstancesKey(String), type) 
	}
	
	@Test
	def boolLiteralType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			pattern literalValue(literalType) = {
				literalType == true;
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeInferrer.getType(param)
		assertEquals(new JavaTransitiveInstancesKey(Boolean), type) 
	}
	
	@Test
	def doubleLiteralType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			pattern literalValue(literalType) = {
				literalType == 3.14;
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeInferrer.getType(param)
		assertEquals(new JavaTransitiveInstancesKey(Double), type) 
	}
	
	@Test
	def countAggregatedComputationValueType() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			pattern literalValue(literalType) = {
				uselessVariable == 10;
				literalType == count find patternToFind(uselessVariable);
			}

			pattern patternToFind(uselessParameter) = {
				uselessParameter == 10;
				check(true);
			}
		')
		model.assertNoErrors
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
		
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeInferrer.getType(param)
		assertEquals("literalType", param.name) 
		assertEquals(new JavaTransitiveInstancesKey(Integer), type) 
	}
	
	@Test
	def supertypeAsParameter() {
		val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern feature(feature: EStructuralFeature) { 
                EReference(feature);
            }

            pattern propertyClass(attribute : EAttribute) {
                find feature(attribute);
            }
		''')
		model.assertNoErrors
		tester.validate(model).assertWarning(EMFIssueCodes::PARAMETER_TYPE_INVALID)
	}
	
	@Test
	def supertypeAsParameter2() {
		val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern feature(feature: EStructuralFeature) { 
                EReference(feature);
            } or {
                EAttribute(feature);
            }

            pattern propertyClass(attribute : EAttribute) {
                find feature(attribute);
            }
		''')
		model.assertNoErrors
		tester.validate(model).assertOK
	}

	@Test
	def supertypeAsParameter3() {
		val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern feature(feature) { 
                EReference(feature);
            }

            pattern propertyClass(attribute : EAttribute) {
                find feature(attribute);
            }
		''')
		tester.validate(model).assertAll(
		    getErrorCode(EMFIssueCodes.VARIABLE_TYPE_INVALID_ERROR),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
	}
	
	@Test
	def supertypeAsParameter4() {
		val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern feature(feature) { 
                EReference(feature);
            } or {
                EAttribute(feature);
            }

            pattern propertyClass(attribute : EAttribute) {
                find feature(attribute);
            }
		''')
		tester.validate(model).assertAll(
		    //No error as supertype was inferred for the parameter of the call
		    //getErrorCode(EMFIssueCodes.VARIABLE_TYPE_INVALID_ERROR),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
	}
	
	
	@Test
	def supertypeAsParameter5() {
		val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern parameterTest(parameter) {
                GrandChild.name(parameter, _);
            }
		''')
		tester.validate(model).assertAll(
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
	}
	
	@Test
    def supertypeAsParameter6() {
        // EInt is not less specific then Integer
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"

            pattern t3(n : EInt) {
                n == eval(Integer.parseInt("2"));
                check(n > 2);
            }
        ''')
        model.assertNoErrors
        tester.validate(model).assertOK
    }
	
	@Test
    def ambiguousParameterType1() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern noSupertypeCalculable(parameter) = {
                Child1(parameter);
            } or {
                Child2(parameter);
            }
        ''')
        tester.validate(model).assertAll(
            getErrorCode(EMFIssueCodes::PARAMETER_TYPE_AMBIGUOUS),
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
        
        val param = model.patterns.get(0).parameters.get(0)
        val type = typeInferrer.getType(param)
        assertEquals("parameter", param.name) 
        assertEquals(classifierToInputKey(EObject), type) 
    }
    
	@Test
    def ambiguousParameterType2() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern noSupertypeCalculable(parameter : Common) = {
                Child1(parameter);
            } or {
                Child2(parameter);
            }
        ''')
        tester.validate(model).assertOK
        
        val param = model.patterns.get(0).parameters.get(0)
        val type = typeInferrer.getType(param)
        assertEquals("parameter", param.name) 
        assertEquals(classifierToInputKey(common), type) 
    }
    
	@Test
    def ambiguousParameterType3() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern noSupertypeCalculable(parameter : Interface) = {
                Child1(parameter);
            } or {
                Child2(parameter);
            }
        ''')
        tester.validate(model).assertOK
        
        val param = model.patterns.get(0).parameters.get(0)
        val type = typeInferrer.getType(param)
        assertEquals("parameter", param.name) 
        assertEquals(classifierToInputKey(interface), type) 
    }
    
	@Test
    def recursiveParameterTypeInference() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/emf/2002/Ecore"
            
            pattern recursive(cl, name) {
                EClass.name(cl, name);
            } or {
                EStructuralFeature.name(cl, name);
            } or {
                EClass.eSuperTypes(cl, parent);
                find recursive(parent, name);
            }
        ''')
        tester.validate(model).assertAll(
            getWarningCode(IssueCodes::RECURSIVE_PATTERN_CALL),
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE),
            getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
        )
        
        val param = model.patterns.get(0).parameters.get(0)
        val type = typeInferrer.getType(param)
        assertEquals("cl", param.name) 
        assertEquals(classifierToInputKey(ENamedElement), type) 
    }
    
	@Test
    def complexHierarchyOK() {
        val model = parseHelper.parse('''
            package org.eclipse.viatra.query.patternlanguage.emf.tests
            import "http://www.eclipse.org/viatra/query/patternlanguage/emf/test"

            pattern test(a: Common, aa: Common) {
                GrandGrandChildE.a1(a, aa);
            } or {
                GrandGrandChildF.a2(a, aa);
            }
        ''')
        tester.validate(model).assertOK
        
        val param = model.patterns.get(0).parameters.get(0)
        val type = typeInferrer.getType(param)
        assertEquals("a", param.name) 
        assertEquals(classifierToInputKey(common), type) 
    }
	
	@Test
	def errorTypeTest1() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern errorTypeTest(parameter) = {
				EClass(parameter);
				EDataType(parameter);
			} 
		')
		tester.validate(model).assertAll(
		    getErrorCode(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR),
		    getInfoCode(EMFIssueCodes::MISSING_PARAMETER_TYPE)
		)
	}
	
	@Test
	def errorTypeTest2() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern warningTypeTest1(parameter : EClass) = {
				EDataType(parameter);
			} 
		')
		tester.validate(model).assertError(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR)
		
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeInferrer.getType(param)
		assertEquals("parameter", param.name) 
		assertEquals(classifierToInputKey(EClass), type) 
	}
	
	@Test
	def errorTypeTest3() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern warningTypeTest2(parameter : EDataType) = {
				EClass(parameter);
			} 
		')
		tester.validate(model).assertError(EMFIssueCodes::VARIABLE_TYPE_INVALID_ERROR)
		
		val param = model.patterns.get(0).parameters.get(0)
		val type = typeInferrer.getType(param)
		assertEquals("parameter", param.name) 
		assertEquals(classifierToInputKey(EDataType), type) 
	}
	
	@Test
	def warningTypeTest1() {
		val model = parseHelper.parse('
			package org.eclipse.viatra.query.patternlanguage.emf.tests
			import "http://www.eclipse.org/emf/2002/Ecore"
			
			pattern warningTypeTest3(parameter : EClassifier) = {
				EClass(parameter);
			} 
		')
		tester.validate(model).assertWarning(EMFIssueCodes::PARAMETER_TYPE_INVALID)
		
		val param = model.patterns.get(0).parameters.get(0)
		val localVariable = model.patterns.get(0).bodies.get(0).variables.get(0)
		val type = typeInferrer.getType(param)
		assertEquals("parameter", param.name) 
		assertEquals(classifierToInputKey(EClassifier), type) 
		assertEquals("parameter", localVariable.name) 
		assertEquals(classifierToInputKey(EClassifier), typeInferrer.getType(localVariable)) 
		assertEquals(classifierToInputKey(EClass), typeInferrer.getInferredType(localVariable)) 
	}
	
}