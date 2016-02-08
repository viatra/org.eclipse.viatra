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
package org.eclipse.viatra.query.tooling.core.generator

import com.google.inject.Inject
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.runtime.IExtensions
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedPatternGroup
import org.eclipse.viatra.query.runtime.extensibility.SingletonExtensionFactory

class GenerateQuerySpecificationExtension {

	@Inject extension EMFPatternLanguageJvmModelInferrerUtil
	@Inject extension ExtensionGenerator exGen

	def extensionContribution(PatternModel model) {
		if (model.patterns.empty) {
			newImmutableList()
		} else {
			newImmutableList({
				val groupClass = model.findInferredClass(typeof(BaseGeneratedPatternGroup))
				contribExtension(groupClass.qualifiedName, IExtensions::QUERY_SPECIFICATION_EXTENSION_POINT_ID) [
					contribElement(it, "group") [
						contribAttribute(it, "id", groupClass.qualifiedName)
						contribAttribute(it, "group",
							typeof(SingletonExtensionFactory).canonicalName + ":" + groupClass.qualifiedName)
					]
				]
			})
		}
	}

	def static getRemovableExtensionIdentifiers() {
		newImmutableList(
			{
				"" -> IExtensions::QUERY_SPECIFICATION_EXTENSION_POINT_ID
			})
	}
}
