/*******************************************************************************
 * Copyright (c) 2010-2016, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.tests.types

import com.google.inject.Inject
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.viatra.query.patternlanguage.emf.tests.util.AbstractValidatorTest
import org.eclipse.viatra.query.patternlanguage.emf.types.EMFTypeSystem
import org.eclipse.viatra.query.runtime.emf.types.EClassTransitiveInstancesKey
import org.eclipse.viatra.query.runtime.matchers.context.IInputKey
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.junit.Test
import org.junit.runner.RunWith

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.CoreMatchers.*
import org.junit.Ignore
import org.eclipse.viatra.query.patternlanguage.emf.tests.pltest.PltestPackage
import org.eclipse.viatra.query.patternlanguage.emf.tests.CustomizedEMFPatternLanguageInjectorProvider

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(CustomizedEMFPatternLanguageInjectorProvider))
class TypeSystemTest extends AbstractValidatorTest {
   
   @Inject
   extension EMFTypeSystem typeSystem
   extension EcorePackage ecorePackage = EcorePackage.eINSTANCE
   extension PltestPackage plTestPackage = PltestPackage.eINSTANCE
   
   @Test
   def singleEClassMinimalization() {
       val types = <IInputKey>newHashSet(
           new EClassTransitiveInstancesKey(EReference)
       )
       
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, false)
       
       assertThat(minimizedTypes.size, equalTo(1))
       assertThat(minimizedTypes, hasItem(types.findFirst[true]))
   }
   
   @Test
   def duplicateEClassMinimalization() {
       val types = <IInputKey>newHashSet( 
           EReference.classifierToInputKey,
           EReference.classifierToInputKey
       )
       
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, false)
       
       assertThat(minimizedTypes.size, equalTo(1))
       assertThat(minimizedTypes, hasItem(EReference.classifierToInputKey))
   }
   
   @Test
   def superEClassMinimalization() {
       val types = <IInputKey>newHashSet(
           EReference.classifierToInputKey,
           EStructuralFeature.classifierToInputKey
       )
       
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, true)
       
       assertThat(minimizedTypes.size, equalTo(1))
       assertThat(minimizedTypes, hasItem(EReference.classifierToInputKey))
   }
   
   @Test
   def superEClassSkipMinimalizationToCommonSupertype() {
       val types = <IInputKey>newHashSet(
           EAttribute.classifierToInputKey,
           EReference.classifierToInputKey
       )
       
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, false)
       
       assertThat(minimizedTypes.size, equalTo(2))
       assertThat(minimizedTypes, hasItem(EAttribute.classifierToInputKey))
       assertThat(minimizedTypes, hasItem(EReference.classifierToInputKey))
   }
   @Test
   def superEClassMinimalizationToCommonSupertype() {
       val types = <IInputKey>newHashSet(
           EAttribute.classifierToInputKey,
           EReference.classifierToInputKey
       )
       
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, true)
       
       assertThat(minimizedTypes.size, equalTo(1))
       assertThat(minimizedTypes, hasItem(EStructuralFeature.classifierToInputKey))
   }
   
   @Test
   def differentEClassMinimalization() {
       val types = <IInputKey>newHashSet(
           EReference.classifierToInputKey,
           EDataType.classifierToInputKey
       )
       
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, false)
       
       assertThat(minimizedTypes.size, equalTo(2))
       assertThat(minimizedTypes, hasItem(EReference.classifierToInputKey))
       assertThat(minimizedTypes, hasItem(EDataType.classifierToInputKey))
   }

   @Test
   def singleEDatatypeMinimalization() {
       val types = <IInputKey>newHashSet( 
           EString.classifierToInputKey
       )
       
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, false)
       
       assertThat(minimizedTypes.size, equalTo(1))
       assertThat(minimizedTypes, hasItem(EString.classifierToInputKey))
   }
   
   @Test
   def duplicateEDatatypeMinimalization() {
       val types = <IInputKey>newHashSet( 
           EString.classifierToInputKey,
           EString.classifierToInputKey
       )
       
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, false)
       
       assertThat(minimizedTypes.size, equalTo(1))
       assertThat(minimizedTypes, hasItem(EString.classifierToInputKey))
   }
   @Test
   @Ignore("Autoboxing not yet supported for EDataTypes")
   def primitiveEDatatypeMinimalization() {
       val types = <IInputKey>newHashSet( 
           EInt.classifierToInputKey,
           EIntegerObject.classifierToInputKey
       )
       
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, false)
       
       assertThat(minimizedTypes.size, equalTo(1))
       assertThat(minimizedTypes, hasItem(EInt.classifierToInputKey))
   }
   
   @Test
   def nonminimizableTypes() {
       val types = <IInputKey>newHashSet(
           child1.classifierToInputKey,
           child2.classifierToInputKey
       )
       val minimizedTypes = typeSystem.minimizeTypeInformation(types, false)
       assertThat(minimizedTypes.size, equalTo(2))
       assertThat(minimizedTypes, hasItem(child1.classifierToInputKey))
       assertThat(minimizedTypes, hasItem(child2.classifierToInputKey))
   }
}