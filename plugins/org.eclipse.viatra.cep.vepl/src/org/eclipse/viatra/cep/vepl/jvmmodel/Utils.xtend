/*******************************************************************************
 * Copyright (c) 2004-2014, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.vepl.jvmmodel

import com.google.common.collect.Lists
import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.viatra.cep.vepl.vepl.ModelElement
import org.eclipse.xtext.common.types.JvmAnnotationType
import org.eclipse.xtext.common.types.JvmMember
import org.eclipse.xtext.common.types.JvmOperation
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.common.types.JvmVisibility
import org.eclipse.xtext.common.types.TypesFactory
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.xbase.compiler.TypeReferenceSerializer
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

class Utils {

	@Inject extension TypeReferenceSerializer typeReferenceSerializer
	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension TypeReferences references

	def Iterable<? extends JvmMember> toAdvancedSetter(ModelElement element, String name, JvmTypeReference type,
		JvmTypeReferenceBuilder typeRefBuilder, int index) {
		val advancedSetter = TypesFactory.eINSTANCE.createJvmOperation
		advancedSetter.simpleName = "set" + name.toFirstUpper
		advancedSetter.returnType = typeRefBuilder.typeRef("void")
		advancedSetter.parameters.add(element.toParameter(name, type))
		advancedSetter.setVisibility(JvmVisibility.PUBLIC)
		advancedSetter.setBody [
			append(
				'''
				this.«name» = «name»;
				getParameters().set(«index», «name»);''')
		]
		return Lists.newArrayList(advancedSetter)
	}

	def addOverrideAnnotation(JvmOperation method, EObject context) {
		method.annotations += TypesFactory.eINSTANCE.createJvmAnnotationReference => [
			it.annotation = references.findDeclaredType(typeof(Override), context) as JvmAnnotationType
		]
	}

	def wildCardExtends(JvmTypeReference clone) {
		var result = TypesFactory.eINSTANCE.createJvmWildcardTypeReference();
		var upperBound = TypesFactory.eINSTANCE.createJvmUpperBound();
		upperBound.setTypeReference(clone);
		result.getConstraints().add(upperBound);
		return result;
	}

	def referClass(ITreeAppendable appendable, JvmTypeReferenceBuilder typeRefBuilder, QualifiedName fqn, EObject ctx) {
		referClass(appendable, typeRefBuilder, fqn.toString, ctx)
	}

	def referClass(ITreeAppendable appendable, JvmTypeReferenceBuilder typeRefBuilder, String fqn, EObject ctx) {
		val ref = typeRefBuilder.typeRef(fqn)
		if (ref != null) {
			appendable.serialize(ref, ctx)
		} else {

			//Class resolution error - error handling required here
			//A fallback to writing out the fqn of the class
			appendable.append(fqn.toString)
		}
	}

	def referClass(ITreeAppendable appendable, JvmTypeReferenceBuilder typeRefBuilder, EObject ctx, Class<?> clazz,
		JvmTypeReference... typeArgs) {
		val ref = typeRefBuilder.typeRef(clazz, typeArgs)
		if (ref != null) {
			appendable.serialize(ref, ctx)
		} else {

			//Class resolution error - error handling required here
			//A fallback to writing out the fqn of the class
			appendable.append(clazz.canonicalName)
		}
	}

	def serialize(ITreeAppendable appendable, JvmTypeReference ref, EObject ctx) {
		typeReferenceSerializer.serialize(ref, ctx, appendable)
	}
}
