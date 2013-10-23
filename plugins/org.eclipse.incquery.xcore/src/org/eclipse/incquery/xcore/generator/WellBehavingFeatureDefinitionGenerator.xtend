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
package org.eclipse.incquery.xcore.generator

import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.incquery.tooling.core.generator.ExtensionGenerator

class WellBehavingFeatureDefinitionGenerator {

	private static final String EXTENSION_POINT = "org.eclipse.incquery.runtime.base.wellbehaving.derived.features";
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
			Pair::of("",EXTENSION_POINT)
		)
	}

}
