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
import java.util.List
import org.eclipse.viatra.cep.core.metamodels.events.EventSource
import org.eclipse.viatra.cep.vepl.vepl.PackagedModel
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

	def public generateFactory(PackagedModel model, IJvmDeclaredTypeAcceptor acceptor,
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
		]
	}

	def private createFactoryMethod(QualifiedName fqn, PackagedModel model, IJvmDeclaredTypeAcceptor acceptor,
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
}
