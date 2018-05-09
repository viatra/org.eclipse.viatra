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
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternModel
import org.eclipse.viatra.query.patternlanguage.emf.jvmmodel.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra.query.runtime.IExtensions
import org.eclipse.viatra.query.runtime.api.impl.BaseGeneratedPatternGroup
import org.eclipse.viatra.query.runtime.extensibility.SingletonExtensionFactory
import org.eclipse.viatra.query.patternlanguage.emf.helper.PatternLanguageHelper

class GenerateQuerySpecificationExtension {

    @Inject extension EMFPatternLanguageJvmModelInferrerUtil
    @Inject extension ExtensionGenerator exGen

    def extensionContribution(PatternModel model) {
        val groupClass = model.findInferredClass(typeof(BaseGeneratedPatternGroup))
        if (model.patterns.empty || groupClass === null) {
            newImmutableList()
        } else {
            newImmutableList({
                contribExtension(groupClass.qualifiedName, IExtensions::QUERY_SPECIFICATION_EXTENSION_POINT_ID) [
                    contribElement(it, "group") [
                        contribAttribute(it, "id", groupClass.qualifiedName)
                        contribAttribute(it, "group",
                            typeof(SingletonExtensionFactory).canonicalName + ":" + groupClass.qualifiedName)
                        model.patterns.filter[public].filterNull.map[
                            PatternLanguageHelper.getFullyQualifiedName(it)
                        ].forEach[ fqn |
                            contribElement(it, "query-specification") [
                                contribAttribute(it, "fqn", fqn)
                            ]
                        ]
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
