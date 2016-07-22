/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.cpp.localsearch.generator

import org.eclipse.viatra.query.tooling.cpp.localsearch.api.GeneratorOutputRecord
import org.eclipse.viatra.query.tooling.cpp.localsearch.api.ILocalsearchGeneratorOutputProvider
import java.util.Collection
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.QueryDescriptor

/**
 * @author Robert Doczi
 */
abstract class LocalsearchGeneratorOutputProvider implements ILocalsearchGeneratorOutputProvider {
	
	var QueryDescriptor query

	override initialize(QueryDescriptor query) {
		this.query = query
	}

	override getOutput() {
		val generators = initializeGenerators(query)
		val root = "Viatra/Query"

		return generators.map[
			new GeneratorOutputRecord('''«root»/«query.name.toFirstUpper»''', fileName, compile)
		].toList
	}

	def Collection<IGenerator> initializeGenerators(QueryDescriptor query)
	
}