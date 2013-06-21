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
package org.eclipse.incquery.patternlanguage.scoping

import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.resource.EObjectDescription
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.scoping.impl.MapBasedScope
import org.eclipse.xtext.xbase.scoping.LocalVariableScopeContext
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.scoping.XbaseScopeProvider

class PatternLanguageScopeProvider extends XbaseScopeProvider {
	
	def PatternBody getContainerBody(EObject obj) {
		var EObject current = obj
		while (current != null && !(current instanceof PatternBody)) {
			current = current.eContainer()
		}
		if (current != null) 
			return current as PatternBody
		else
			return null
	}
	
	override IScope createLocalVarScope(IScope parent, LocalVariableScopeContext scopeContext) {
		val parentScope = super.createLocalVarScope(parent, scopeContext)
		switch context: scopeContext.context {
			PatternBody : {
				val descriptions = context.variables.map(e | e.createIEObjectDescription())
				return MapBasedScope::createScope(
						super.createLocalVarScope(parentScope, scopeContext), descriptions);
			}
			Pattern : {
				val descriptions = context.parameters.map(e | e.createIEObjectDescription())
				return MapBasedScope::createScope(parentScope, descriptions);
					
			}
			case context.containerBody != null: {
				val descriptions = context.containerBody.variables.map(e | e.createIEObjectDescription())
				return MapBasedScope::createScope(
						super.createLocalVarScope(parentScope, scopeContext), descriptions);
			}
		}
		return parentScope
	}
	
	def createIEObjectDescription(Variable parameter) {
		var name = if (parameter.name != null)  QualifiedName::^create(parameter.name) else QualifiedName::EMPTY
		EObjectDescription::^create(name, parameter, null);
	}
}
