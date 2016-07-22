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
package org.eclipse.viatra.query.tooling.cpp.localsearch.generator.runtime

import com.google.common.collect.Maps
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.NameUtils
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.QuerySpecificationGenerator
import java.util.List
import java.util.Map
import java.util.Set
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.Include
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternBodyDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.DependentSearchOperationDescriptor

/**
 * @author Robert Doczi
 */
class RuntimeQuerySpecificationGenerator extends QuerySpecificationGenerator {
	
	val Map<PatternDescriptor, Map<PatternBodyDescriptor, List<RuntimeSearchOperationGenerator>>> searchOperations
	val Map<PatternBodyDescriptor, MatchingFrameGenerator> frameGenerators
	
	new(String queryName, Set<PatternDescriptor> patternGroup, Map<PatternBodyDescriptor, MatchingFrameGenerator> frameGenerators) {
		super(queryName, patternGroup)
		
		this.searchOperations = Maps::asMap(patternGroup)[pattern |
			Maps::asMap(pattern.patternBodies) [patternBody|
				patternBody.searchOperations.map[op |
					new RuntimeSearchOperationGenerator(queryName, op, frameGenerators.get(patternBody))
				]
			]
		]
		this.frameGenerators = frameGenerators
	}
	
	override initialize() {
		super.initialize
		includes += frameGenerators.values.map[include]
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
	
	override generatePlan(PatternDescriptor pattern, PatternBodyDescriptor patternBody) '''
		«val bodyNum = patternBody.index»
		«val frame = frameGenerators.get(patternBody)»
		static ::Viatra::Query::Plan::SearchPlan<«frame.frameName»> get_plan_«NameUtils::getPlanName(pattern)»__«bodyNum»(const ModelRoot* model) {
			using namespace ::Viatra::Query::Operations::Check;
			using namespace ::Viatra::Query::Operations::Extend;
		
			::Viatra::Query::Plan::SearchPlan<«frame.frameName»> sp;
			«val setupCode = new StringBuilder»
			«val opCodes = searchOperations.get(pattern).get(patternBody).map[compile(setupCode)]»
			««« evaluate so setup code gets calculated 
			«opCodes.forEach[]»
			
			«setupCode.toString»
			
			«FOR op : opCodes»
				sp.add_operation(«op»);
			«ENDFOR»
			
			return sp;
		}
	'''
	
		
}
