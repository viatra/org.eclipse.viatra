/*******************************************************************************
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Istvan David - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.cep.vepl.ui.builder

import com.google.common.base.Joiner
import com.google.common.collect.ImmutableList
import com.google.inject.Singleton
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern
import org.eclipse.viatra.cep.vepl.vepl.EventModel
import org.eclipse.viatra.cep.vepl.vepl.ModelElement
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern
import org.eclipse.viatra.cep.vepl.vepl.Rule

import static org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider.*

@Singleton
class PackageExportHelper {

	def getExportablePackages(EventModel eventModel) {
		val basePackageName = eventModel.name.toString

		val packages = ImmutableList.builder.add(basePackageName).add(
			Joiner.on('.').join(basePackageName, EVENTCLASS_PACKAGE_NAME_ELEMENT)).add(
			Joiner.on('.').join(basePackageName, ATOMIC_PATTERN_PACKAGE_NAME_ELEMENT)).add(
			Joiner.on('.').join(basePackageName, QUERYRESULT_PATTERN_PACKAGE_NAME_ELEMENT)).add(
			Joiner.on('.').join(basePackageName, COMPLEX_PATTERN_PACKAGE_NAME_ELEMENT)).add(
			Joiner.on('.').join(basePackageName, RULES_PACKAGE_NAME_ELEMENT)).add(
			Joiner.on('.').join(basePackageName, JOBS_PACKAGE_NAME_ELEMENT)).add(
			Joiner.on('.').join(basePackageName, MAPPING_PACKAGE_NAME_ELEMENT)).build

		return packages
	}

	def static packageShouldBeExported(ModelElement modelElement) {
		(modelElement instanceof AtomicEventPattern) || (modelElement instanceof ComplexEventPattern) ||
			(modelElement instanceof QueryResultChangeEventPattern) || (modelElement instanceof Rule)
	}
}
