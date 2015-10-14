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
import org.eclipse.viatra.cep.vepl.vepl.ModelElement
import org.eclipse.viatra.cep.vepl.vepl.Trait
import org.eclipse.xtext.xbase.jvmmodel.IJvmDeclaredTypeAcceptor
import org.eclipse.xtext.xbase.jvmmodel.JvmTypeReferenceBuilder
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder

class TraitGenerator {
	@Inject extension JvmTypesBuilder jvmTypesBuilder
	@Inject extension NamingProvider

	def public generateInterface(Iterable<ModelElement> traits, IJvmDeclaredTypeAcceptor acceptor,
		JvmTypeReferenceBuilder typeRefBuilder) {
		for (trait : traits) {
			acceptor.accept(trait.toClass(trait.traitSpecificationFqn)) [
				final = true
				for (param : (trait as Trait).parameters.parameters) {
					members += param.toField(param.typedParameter.name, param.typedParameter.type)
					members += param.toGetter(param.typedParameter.name, param.typedParameter.type)
					members += param.toSetter(param.typedParameter.name, param.typedParameter.type)
				}

//				members += trait.toMethod("evaluateCheckExpression", typeRefBuilder.typeRef("boolean")) [
//					for (param : (trait as Trait).parameters.parameters) {
//						parameters += trait.toParameter(param.typedParameter.name, param.typedParameter.type)
//					}
//					if ((trait as Trait).checkExpression == null) {
//						body = [
//							append('''return true;''')
//						]
//					} else {
//						body = (trait as Trait).checkExpression
//					}
//				]
			]
		}
	}
}