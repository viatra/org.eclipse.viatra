/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.vepl.tests.validation

import org.eclipse.viatra.cep.vepl.tests.VeplTestCase
import org.eclipse.viatra.cep.vepl.validation.VeplValidator
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
import org.eclipse.viatra.cep.vepl.vepl.VeplPackage
import org.junit.Test

class ValidationTests extends VeplTestCase {

    @Test
    def void uniqueName() {
        val model1 = '''
            atomicEvent a
            atomicEvent a
        '''.parse

        val erroneousElements = model1.modelElements.filter[e|e instanceof AtomicEventPattern]

        erroneousElements.forEach[e|
            e.assertError(VeplPackage::eINSTANCE.atomicEventPattern, VeplValidator::INVALID_NAME)]

        val model2 = '''
            atomicEvent a
            atomicEvent b
        '''.parse
        model2.assertNoErrors
    }

    @Test
    def void validPatternCallArguments() {
        val model1 = '''
            atomicEvent a(p1:String, p2:int)
            atomicEvent b(p1:String, p2:int)
            
            complexEvent c(p1:String, p2:int){
                as a->b(p1, _)
            }
        '''.parse
        model1.assertNoErrors

        val model2 = '''
            atomicEvent a(p1:String, p2:int)
            atomicEvent b(p1:String, p2:int)
            
            complexEvent c(p1:String, p2:int){
                as a(p1)->b
            }
        '''.parse
        model2.assertError(VeplPackage::eINSTANCE.parameterizedPatternCall, VeplValidator::INVALID_ARGUMENTS)

        val model3 = '''
            atomicEvent a(p1:String, p2:int)
            atomicEvent b(p1:String, p2:int)
            
            complexEvent c(p1:String, p2:int){
                as a(p1)->b()
            }
        '''.parse
        model3.assertError(VeplPackage::eINSTANCE.parameterizedPatternCall, VeplValidator::INVALID_ARGUMENTS)
    }

    @Test
    def void explicitlyImportedQueryPackage() {
        val model = '''
            queryEvent ce() as someUnimportedQuery
        '''.parse
        model.assertError(VeplPackage::eINSTANCE.queryResultChangeEventPattern, VeplValidator::MISSING_QUERY_IMPORT)
    }

    @Test
    def void expressionAtomWithTimewindowMustFeatureMultiplicity() {
        val model1 = '''
            atomicEvent a
            
            complexEvent c(){
                as a[1000]
            }
            
        '''.parse
        model1.assertError(VeplPackage::eINSTANCE.atom, VeplValidator::ATOM_TIMEWINDOW_NO_MULTIPLICITY)

        val model2 = '''
            atomicEvent a
            
            
            complexEvent c(){
                as a{1}
            }
        '''.parse
        model2.assertError(VeplPackage::eINSTANCE.atom, VeplValidator::ATOM_TIMEWINDOW_NO_MULTIPLICITY)
    }

    @Test
    def void complexEventPatternWithPlainAtomExpression() {
        val model = '''
            atomicEvent a
            
            complexEvent c1(){
                as a
            }
            
        '''.parse
        model.assertWarning(VeplPackage::eINSTANCE.complexEventPattern,
            VeplValidator::SINGE_PLAIN_ATOM_IN_COMPLEX_EVENT_EXPRESSION)
    }

}
