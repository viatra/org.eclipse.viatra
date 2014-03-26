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

package org.eclipse.incquery.tooling.core.generator

import com.google.inject.Inject
import org.eclipse.incquery.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern
import org.eclipse.incquery.runtime.IExtensions
import org.eclipse.xtext.common.types.JvmDeclaredType
import org.eclipse.xtext.common.types.JvmIdentifiableElement
import org.eclipse.xtext.common.types.JvmType
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelAssociations

import static extension org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper.*

class GenerateQuerySpecificationExtension {

	@Inject	IJvmModelAssociations associations
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil

	def extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		newArrayList(
		exGen.contribExtension(pattern.fullyQualifiedName, IExtensions::QUERY_SPECIFICATION_EXTENSION_POINT_ID) [
			exGen.contribElement(it, "matcher") [
				exGen.contribAttribute(it, "id", pattern.fullyQualifiedName)

				val querySpecificationClass = associations.getJvmElements(pattern).
				  findFirst[it instanceof JvmDeclaredType && (it as JvmDeclaredType).simpleName.equals(pattern.querySpecificationClassName)] as JvmDeclaredType
				val providerClass = querySpecificationClass.members.
				  findFirst([it instanceof JvmType && (it as JvmType).simpleName.equals(pattern.querySpecificationProviderClassName)]) as JvmIdentifiableElement

				exGen.contribAttribute(it, "querySpecificationProvider", providerClass.qualifiedName)
			]
		]
		)
	}

	def static getRemovableExtensionIdentifiers() {
		newImmutableList(
			"" -> IExtensions::QUERY_SPECIFICATION_EXTENSION_POINT_ID
		)
	}
}