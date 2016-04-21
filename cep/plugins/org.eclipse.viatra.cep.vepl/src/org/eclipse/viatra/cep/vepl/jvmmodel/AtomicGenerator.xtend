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
import org.eclipse.viatra.cep.core.api.events.ParameterizableViatraQueryPatternEventInstance
import org.eclipse.viatra.cep.core.metamodels.events.EventSource
import org.eclipse.viatra.cep.core.metamodels.events.impl.AtomicEventPatternImpl
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
import org.eclipse.viatra.cep.vepl.vepl.ModelElement
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

class AtomicGenerator {
	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension Utils
	@Inject extension NamingProvider

	def public generateAtomicEventClasses(Iterable<ModelElement> patterns, IJvmDeclaredTypeAcceptor acceptor,
		JvmTypeReferenceBuilder typeRefBuilder) {
		for (pattern : patterns) {
			acceptor.accept(pattern.toClass(pattern.classFqn)) [
				documentation = pattern.documentation
				if (pattern instanceof QueryResultChangeEventPattern) {
					superTypes += typeRefBuilder.typeRef(ParameterizableViatraQueryPatternEventInstance)
				} else if (pattern instanceof AtomicEventPattern) {
					superTypes += typeRefBuilder.typeRef(ParameterizableEventInstance)
					if((pattern as AtomicEventPattern).traits!=null){
						if(!(pattern as AtomicEventPattern).traits.traits.empty){
							for(trait : (pattern as AtomicEventPattern).traits.traits){
								superTypes += typeRefBuilder.typeRef(trait.traitInterfaceFqn.toString)
							}
						}
					}
				}
				val paramList = getParamList(pattern)
				if (paramList != null) {
					for (parameter : paramList.parameters) {
						members += pattern.toField(parameter.name, parameter.type)
					}
				}
				if (pattern instanceof AtomicEventPattern) {
					val traitList = (pattern as AtomicEventPattern).traits
					if (traitList != null) {
						for (trait : traitList.traits) {
							for (param : trait.parameters.parameters) {
								val parameter = param.typedParameter
								members += pattern.toField(parameter.name, parameter.type) [
									if (param.value != null) {
										initializer = param.value
									}
								]
							}
						}
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
						''').append(
							'''
							«IF pattern instanceof AtomicEventPattern»
								«IF (pattern as AtomicEventPattern).traits!=null»
									«FOR trait : (pattern as AtomicEventPattern).traits.traits»
										«FOR param : trait.parameters.parameters»				
												getParameters().add(«param.typedParameter.name»);
										«ENDFOR»
									«ENDFOR»
								«ENDIF»
							«ENDIF»
						''')
					]
				]
				var i = 0
				if (paramList != null) {
					for (parameter : paramList.parameters) {
						members += pattern.toGetter(parameter.name, parameter.type)
						members += pattern.toAdvancedSetter(parameter.name, parameter.type, typeRefBuilder, i)
						i = i + 1
					}
				}
				if (pattern instanceof AtomicEventPattern) {
					val traitList = (pattern as AtomicEventPattern).traits
					if (traitList != null) {
						for (trait : traitList.traits) {
							for (param : trait.parameters.parameters) {
								val parameter = param.typedParameter
								
								val getter = pattern.toGetter(parameter.name, parameter.type)
								getter.addOverrideAnnotation(parameter)
								members += getter
								
								val setter = pattern.toAdvancedSetter(parameter.name, parameter.type, typeRefBuilder, i)
								setter.addOverrideAnnotation(pattern)
								members += setter
								i = i + 1
							}
						}
					}
				}

				members += pattern.toMethod("evaluateCheckExpression", typeRefBuilder.typeRef("boolean")) [
					addOverrideAnnotation(it, pattern)
					if (pattern.checkExpression == null) {
						body = [
							append('''return true;''')
						]
					} else {
						body = pattern.checkExpression
					}
				]
			]
			FactoryManager.instance.add(pattern.classFqn)
		}
	}

	def void generateAtomicEventPatterns(Iterable<ModelElement> patterns, IJvmDeclaredTypeAcceptor acceptor,
		JvmTypeReferenceBuilder typeRefBuilder) {
		for (pattern : patterns) {
			acceptor.accept(pattern.toClass(pattern.patternFqn)) [
				documentation = pattern.documentation
				superTypes += typeRefBuilder.typeRef(AtomicEventPatternImpl)
				val paramList = getParamList(pattern)
				if (paramList != null) {
					for (parameter : paramList.parameters) {
						members += pattern.toField(parameter.name, parameter.type)
					}
				}
				members += pattern.toConstructor [
					body = [
						append(
							'''
						super();
						setType(''').append('''«it.referClass(typeRefBuilder, pattern.classFqn, pattern)»''').append(
							'''.class.getCanonicalName());''').append(
							'''
							
							setId("«pattern.patternFqn.toString.toLowerCase»");'''
						)
					]
				]
			]
			FactoryManager.instance.add(pattern.patternFqn)
		}
	}

	def private getCheckExpression(ModelElement element) {
		switch (element) {
			AtomicEventPattern:
				return (element as AtomicEventPattern).checkExpression
			default:
				return null
		}
	}

	def private getParamList(ModelElement modelElement) {
		if (modelElement instanceof AtomicEventPattern) {
			return (modelElement as AtomicEventPattern).parameters
		} else if (modelElement instanceof QueryResultChangeEventPattern) {
			return (modelElement as QueryResultChangeEventPattern).parameters
		}
	}
}