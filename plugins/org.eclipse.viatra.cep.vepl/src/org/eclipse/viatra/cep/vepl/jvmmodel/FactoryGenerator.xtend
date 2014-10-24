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
import org.eclipse.viatra.cep.vepl.vepl.PackagedModel
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder
import org.eclipse.viatra.cep.core.metamodels.events.EventSource
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.common.types.JvmField
import java.util.List
import org.eclipse.xtext.common.types.JvmMember
import org.eclipse.emf.common.util.EList

@SuppressWarnings("discouraged")
@SuppressWarnings("restriction")
class FactoryGenerator {

	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension NamingProvider
	@Inject extension Utils

	def public generateFactory(PackagedModel model, IJvmDeclaredTypeAcceptor acceptor) {
		acceptor.accept(model.toClass(model.factoryFqn)).initializeLater [
			var instanceField = (model.toField("instance", model.newTypeRef("CepFactory")))
			instanceField.setStatic(true)
			members += instanceField
			var instanceMethod = model.toMethod("getInstance", model.newTypeRef("CepFactory")) [
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
						FactoryMethodParameter.EVENTSOURCE)
					parametricEventMethod.parameters.add(model.toParameter("eventSource", model.newTypeRef(EventSource)))
					members += parametricEventMethod

					var simpleEventMethod = fqn.createFactoryMethod(model, acceptor, members,
						FactoryMethodParameter.NULL)
					members += simpleEventMethod
				} else {
					var method = fqn.createFactoryMethod(model, acceptor, members, FactoryMethodParameter.EMPTY)
					members += method
				}
			}
		]
	}

	def private createFactoryMethod(QualifiedName fqn, PackagedModel model, IJvmDeclaredTypeAcceptor acceptor,
		List<JvmMember> members, FactoryMethodParameter methodParameter) {
		var method = model.toMethod("create" + fqn.lastSegment, model.newTypeRef(fqn.toString)) [
			body = [
				append('''return new ''').append('''«referClass(it, fqn, model)»''').append(
					'''(«methodParameter.literal»);''')
			]
		]
		method.setDocumentation('''Factory method for «fqn.type» {@link «fqn.lastSegment»}.''')
		method
	}
}
