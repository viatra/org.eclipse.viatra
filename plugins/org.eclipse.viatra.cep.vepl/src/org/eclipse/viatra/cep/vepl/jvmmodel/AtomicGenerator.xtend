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

import com.google.inject.Inject
import org.eclipse.viatra.cep.core.api.events.ParameterizableEventInstance
import org.eclipse.viatra.cep.core.api.events.ParameterizableIncQueryPatternEventInstance
import org.eclipse.viatra.cep.core.metamodels.events.EventSource
import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
import org.eclipse.viatra.cep.vepl.vepl.ModelElement
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern

class AtomicGenerator {
	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension Utils
	@Inject extension NamingProvider
	

	def public generateAtomicEventClasses(Iterable<ModelElement> patterns, IJvmDeclaredTypeAcceptor acceptor, JvmTypeReferenceBuilder typeRefBuilder) {
		for (pattern : patterns) {
			acceptor.accept(pattern.toClass(pattern.classFqn))[
				documentation = pattern.documentation
				if (pattern instanceof QueryResultChangeEventPattern) {
					superTypes += typeRefBuilder.typeRef(ParameterizableIncQueryPatternEventInstance)
				} else if (pattern instanceof AtomicEventPattern) {
					superTypes += typeRefBuilder.typeRef(ParameterizableEventInstance)
				}
				val paramList = getParamList(pattern)
				if (paramList != null) {
					for (parameter : paramList.parameters) {
						members += pattern.toField(parameter.name, parameter.type)
					}
				}
				members += pattern.toConstructor [
					parameters += pattern.toParameter("eventSource", typeRefBuilder.typeRef(EventSource))
					body = [
						append(
							'''
							super(eventSource);''').append(
							'''
								«IF paramList != null»

									«FOR parameter : paramList.parameters»
										getParameters().add(«parameter.name»);
									«ENDFOR»
								«ENDIF»
							''')
					]
				]
				if (paramList != null) {
					var i = 0
					for (parameter : paramList.parameters) {
						members += pattern.toGetter(parameter.name, parameter.type)
						members += pattern.toAdvancedSetter(parameter.name, parameter.type, typeRefBuilder, i)
						i = i + 1
					}
				}
			]
			FactoryManager.instance.add(pattern.classFqn)
		}
	}

	def void generateAtomicEventPatterns(Iterable<ModelElement> patterns, IJvmDeclaredTypeAcceptor acceptor, JvmTypeReferenceBuilder typeRefBuilder) {
		for (pattern : patterns) {
			acceptor.accept(pattern.toClass(pattern.patternFqn)) [
				documentation = pattern.documentation
				superTypes += typeRefBuilder.typeRef(AtomicEventPatternImpl)
				members += pattern.toConstructor [
					body = [
						append(
							'''
							super();
							setType(''').append('''«it.referClass(typeRefBuilder, pattern.classFqn, pattern)»''').append(
							'''.class.getCanonicalName());''').append(
							'''
							
							setId("«pattern.patternFqn.toString.toLowerCase»");'''
						)]
				]
				members += pattern.toMethod("checkStaticBindings", typeRefBuilder.typeRef("boolean")) [
					if (pattern.staticBindings == null) {
						body = [
							append('''return true;''')
						]
					} else {
						body = pattern.staticBindings
					}
				]
			]
			FactoryManager.instance.add(pattern.patternFqn)
		}
	}

	def private getStaticBindings(ModelElement element) {
		switch (element) {
			AtomicEventPattern:
				return (element as AtomicEventPattern).staticBindings
			default:
				return null
		}
	}

	def private getParamList(ModelElement modelElement) {
		if(modelElement instanceof AtomicEventPattern){
			return (modelElement as AtomicEventPattern).parameters
		}else if(modelElement instanceof QueryResultChangeEventPattern){
			return (modelElement as QueryResultChangeEventPattern).parameters
		}
	}
}