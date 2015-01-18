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
import java.util.List
import org.eclipse.viatra.cep.core.api.rules.ICepRule
import org.eclipse.viatra.cep.core.metamodels.events.EventSource
import org.eclipse.viatra.cep.vepl.vepl.EventModel
import org.eclipse.xtext.common.types.JvmMember
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

@SuppressWarnings("discouraged", "restriction")
class FactoryGenerator {

	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension NamingProvider
	@Inject extension Utils

	def public generateFactory(EventModel model, IJvmDeclaredTypeAcceptor acceptor,
		JvmTypeReferenceBuilder typeRefBuilder) {
		acceptor.accept(model.toClass(model.factoryFqn)) [
			var instanceField = (model.toField("instance", typeRefBuilder.typeRef("CepFactory")))
			instanceField.setStatic(true)
			members += instanceField
			var instanceMethod = model.toMethod("getInstance", typeRefBuilder.typeRef("CepFactory")) [
				body = [
					append(
						'''
						if(instance == null){
							instance = new CepFactory();
						}
						return instance;''')
				]
			]
			instanceMethod.setStatic(true)
			members += instanceMethod
			for (fqn : FactoryManager.instance.registeredClasses) {
				if (fqn.event) {
					var parametricEventMethod = fqn.createFactoryMethod(model, acceptor, members,
						FactoryMethodParameter.EVENTSOURCE, typeRefBuilder)
					parametricEventMethod.parameters.add(
						model.toParameter("eventSource", typeRefBuilder.typeRef(EventSource)))
					members += parametricEventMethod

					var simpleEventMethod = fqn.createFactoryMethod(model, acceptor, members,
						FactoryMethodParameter.NULL, typeRefBuilder)
					members += simpleEventMethod
				} else {
					var method = fqn.createFactoryMethod(model, acceptor, members, FactoryMethodParameter.EMPTY,
						typeRefBuilder)
					members += method
				}
			}
			val rules = FactoryManager.instance.registeredClasses.filter[fqn|fqn.rule].toList
			members += rules.createAllRulesMethod(model, acceptor, members, typeRefBuilder)
		]
	}

	def private createFactoryMethod(QualifiedName fqn, EventModel model, IJvmDeclaredTypeAcceptor acceptor,
		List<JvmMember> members, FactoryMethodParameter methodParameter, JvmTypeReferenceBuilder typeRefBuilder) {
		var method = model.toMethod("create" + fqn.lastSegment, typeRefBuilder.typeRef(fqn.toString)) [
			body = [
				append('''return new ''').append('''«referClass(it, typeRefBuilder, fqn, model)»''').append(
					'''(«methodParameter.literal»);''')
			]
		]
		method.setDocumentation('''Factory method for «fqn.type» {@link «fqn.lastSegment»}.''')
		method
	}

	def private createAllRulesMethod(List<QualifiedName> ruleFqns, EventModel model, IJvmDeclaredTypeAcceptor acceptor,
		List<JvmMember> members, JvmTypeReferenceBuilder typeRefBuilder) {
		var method = model.toMethod("allRules", typeRefBuilder.typeRef(List, typeRefBuilder.typeRef(ICepRule))) [
			body = [
				append('''List<ICepRule> rules = ''')
				append(
					'''«referClass(typeRefBuilder, model, Lists)».newArrayList();
						''');
				for (fqn : ruleFqns) {
					append('''rules.add(new ''').append('''«referClass(typeRefBuilder, fqn, model)»''').append(
						'''());
							''')
				}
				append('''return rules;''')
			]
		]
		method.setDocumentation('''Factory method for instantiating every defined rule.''')
		method
	}
}
