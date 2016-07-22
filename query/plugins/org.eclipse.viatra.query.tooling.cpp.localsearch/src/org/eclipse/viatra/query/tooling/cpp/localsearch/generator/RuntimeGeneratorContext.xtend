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

import com.google.common.base.CaseFormat
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.MatchGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.QueryGroupGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.runtime.MatchingFrameGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.runtime.RuntimeMatcherGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.runtime.RuntimeQuerySpecificationGenerator
import java.util.List
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.QueryDescriptor

/**
 * @author Robert Doczi
 */
class RuntimeGeneratorContext extends LocalsearchGeneratorOutputProvider {

	override initializeGenerators(QueryDescriptor query) {
		val List<IGenerator> generators = newArrayList


		query.patterns.forEach [ name, patterns |
			val frameGenMap = newHashMap
			val patternName = CaseFormat::LOWER_CAMEL.to(CaseFormat::UPPER_CAMEL, name)
			patterns.forEach[
				patternBodies.forEach[ patternBody |
					val matchingFrameGenerator = new MatchingFrameGenerator(query.name, patternName, patternBody.index, patternBody.matchingFrame)
					frameGenMap.put(patternBody, matchingFrameGenerator)
					generators += matchingFrameGenerator
				]
			]

			// TODO: WARNING! Incredible Hack Inc! works, but ugly...
			val matchGen = new MatchGenerator(query.name, patternName, patterns.head.patternBodies.head.matchingFrame)
			generators += matchGen
			
			val querySpec = new RuntimeQuerySpecificationGenerator(query.name, patterns.toSet, frameGenMap)
			generators += querySpec
			
			val matcherGen = new RuntimeMatcherGenerator(query.name, patternName, patterns.toSet, frameGenMap, matchGen, querySpec)
			generators += matcherGen
		]
		
		val queryGroupGenerator = new QueryGroupGenerator(query)
		generators += queryGroupGenerator

		generators.forEach[initialize]

		return generators

	}

}