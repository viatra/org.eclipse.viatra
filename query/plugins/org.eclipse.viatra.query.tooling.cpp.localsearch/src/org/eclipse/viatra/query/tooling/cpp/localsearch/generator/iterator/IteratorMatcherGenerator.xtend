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
package org.eclipse.viatra.query.tooling.cpp.localsearch.generator.iterator

import com.google.common.collect.Maps
import java.util.Map
import java.util.Set
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.Include
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.MatchGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.MatcherGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.NameUtils
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.QuerySpecificationGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.DependentSearchOperationDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternBodyDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternDescriptor

/**
 * @author Robert Doczi
 */
class IteratorMatcherGenerator extends MatcherGenerator {
	
	val Map<PatternDescriptor, Map<PatternBodyDescriptor, IteratorSearchOperationGenerator>> searchOperations
	
	new(String queryName, String patternName, Set<PatternDescriptor> patternGroup, MatchGenerator matchGenerator, QuerySpecificationGenerator querySpecification) {
		super(queryName, patternName, patternGroup, matchGenerator, querySpecification)
		this.searchOperations = Maps::asMap(patternGroup)[pattern |
			Maps::asMap(pattern.patternBodies) [patternBody|
				val sog = new IteratorSearchOperationGenerator(patternBody.searchOperations, matchGenerator)
				sog.initialize
				return sog
			]
		]
	}
	
	override initialize() {
		super.initialize
		includes += new Include("Viatra/Query/Util/IsNull.h")
		includes += new Include("type_traits", true)
		
		// TODO: this does not work with if there are multiple query files, somehow the related matcher generator needs to be accessed and its include path should be used
		searchOperations.keySet
			.map[it.patternBodies]
			.flatten
			.map[it.searchOperations]
			.flatten
			.filter(DependentSearchOperationDescriptor)
			.map[it.dependencies]
			.flatten
			.forEach[
				val matcherName = '''«it.referredQuery.fullyQualifiedName.substring(it.referredQuery.fullyQualifiedName.lastIndexOf('.')+1).toFirstUpper»Matcher'''
				includes += new Include('''«implementationNamespace.toString("/")»/«matcherName».h''')
			]
	}
	
	override protected compilePlanExecution(PatternDescriptor pattern, PatternBodyDescriptor patternBody) '''
		auto _classHelper = &_context->get_class_helper();
		
		«assignParamsToVariables(pattern)»
		
		«val sog = searchOperations.get(pattern).get(patternBody)»
		«sog.matchFoundHandler = ['''matches.insert(«it»);''']»
		
		«val setupCode = new StringBuilder»
		«val executionCode = sog.compile(setupCode)»
		
		«setupCode.toString»
		
		«executionCode»
	'''
	
	def assignParamsToVariables(PatternDescriptor pattern) {
		val matchingFrame = pattern.patternBodies.head.matchingFrame
		'''
		«FOR param : pattern.boundParameters»
		«val varName = NameUtils::getPurgedName(param.toPVariable(matchingFrame))»
		«IF varName != param.name»
			auto «NameUtils::getPurgedName(param.toPVariable(matchingFrame))» = «param.name»;
		«ENDIF»
		«ENDFOR»
		'''
	}
	
	
	
}