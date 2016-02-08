/*******************************************************************************
 * Copyright (c) 2010-2013, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.integration.xcore.generator

import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.viatra.query.tooling.core.generator.ExtensionGenerator

/**
 * Generator for the IncQuery derived feature definitions. The pattern based derived features in the IncQuery & Xcore metamodel 
 * will be backed by a well-behaving derived feature in runtime.
 * 
 * @author Tamas Szabo (itemis AG)
 * 
 */
public class WellBehavingFeatureDefinitionGenerator {

	private static final String EXTENSION_POINT = "org.eclipse.viatra.query.runtime.base.wellbehaving.derived.features";
	private static final String EXTENSION_ELEMENT = "wellbehaving-derived-feature";

	def static generateExtension(EStructuralFeature feature, ExtensionGenerator exGen) {
		exGen.contribExtension("extension.derived." + feature.name, EXTENSION_POINT) [
			exGen.contribElement(it, EXTENSION_ELEMENT) [
				exGen.contribAttribute(it, "package-nsUri", feature.EContainingClass.EPackage.nsURI)
				exGen.contribAttribute(it, "classifier-name", feature.EContainingClass.name)
				exGen.contribAttribute(it, "feature-name", feature.name)
			]
		]
	}

	def static getRemovableExtensionIdentifiers() {
		newArrayList(
			Pair::of("", EXTENSION_POINT)
		)
	}

}
