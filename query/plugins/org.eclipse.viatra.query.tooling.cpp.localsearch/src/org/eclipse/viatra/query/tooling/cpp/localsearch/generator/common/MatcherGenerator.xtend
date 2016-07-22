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
package org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common

import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.ViatraQueryHeaderGenerator
import java.util.Set
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.viatra.query.runtime.matchers.psystem.PVariable
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternBodyDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.MatchingFrameDescriptor

/**
 * @author Robert Doczi
 */
abstract class MatcherGenerator extends ViatraQueryHeaderGenerator {
	
	protected val String name
	protected val Set<PatternDescriptor> patternGroup
	protected val MatchGenerator matchGenerator
	
	protected val QuerySpecificationGenerator querySpecification

	new(String queryName, String patternName, Set<PatternDescriptor> patternGroup, MatchGenerator matchGenerator, QuerySpecificationGenerator querySpecification) {
		super(#{queryName}, '''«patternName.toFirstUpper»Matcher''')
		this.name = patternName.toFirstUpper
		this.patternGroup = patternGroup
		this.matchGenerator = matchGenerator
		this.querySpecification = querySpecification
	}
	
	override initialize() {
		includes += matchGenerator.include
		includes += querySpecification.include
		
		includes += new Include("Viatra/Query/QueryEngine.h")
		includes += new Include("unordered_set", true)
		// TODO: this is only required for the using statement, however this should be moved from here as it is not generally necessary
		includes += new Include("Viatra/Query/Plan/SearchPlanExecutor.h")
	}
	
	override compileInner() '''
		template<class ModelRoot>
		class «unitName» {
		public:
			«unitName»(const ModelRoot* model, const ::Viatra::Query::Matcher::ISearchContext* context) 
				: _model(model), _context(context) {
			}
		
			«FOR pattern : patternGroup»
				«compileGetter(pattern)»
			«ENDFOR»
			
		private:
			const ModelRoot* _model;
			const ::Viatra::Query::Matcher::ISearchContext* _context;
		};
	'''
	
	private def compileGetter(PatternDescriptor pattern) '''
		std::unordered_set<«name»Match> matches(«getParamList(pattern)») const {
			««« TODO: Move using statements
			using ::Viatra::Query::Matcher::ISearchContext;
			using ::Viatra::Query::Plan::SearchPlan;
			using ::Viatra::Query::Plan::SearchPlanExecutor;
			using ::Viatra::Query::Matcher::ClassHelper;
		
			std::unordered_set<«name»Match> matches;
		
			«FOR patternBody : pattern.patternBodies»
				{
					«compilePlanExecution(pattern, patternBody)»
				}
				
			«ENDFOR»
		
			return matches;
		}
	'''
	
	protected abstract def String compilePlanExecution(PatternDescriptor pattern, PatternBodyDescriptor patternBody)
	
	protected def fillMatch(MatchingFrameDescriptor matchingFrame) '''
		«FOR parameter : matchingFrame.parameters»
			«val keyVariable = parameter.toPVariable(matchingFrame)»
			match.«parameter.name» = static_cast<«keyVariable.type(matchingFrame)»>(frame._«matchingFrame.getVariablePosition(keyVariable)»);
		«ENDFOR»
	'''
	
	private def getParamList(PatternDescriptor pattern) {
		val matchingFrame = pattern.patternBodies.head.matchingFrame
		pattern.boundParameters.map[
			val variable = it.toPVariable(matchingFrame);
			'''«variable.type(matchingFrame)» «it.name»'''
		].join(", ")
	}
	
	private def toTypeName(EClassifier clazz) {
		NameUtils::toTypeName(clazz)		
	}
	
	protected def type(PVariable variable, MatchingFrameDescriptor matchingFrame) {
		matchingFrame.getVariableStrictType(variable).toTypeName
	}
	
	protected def toPVariable(PParameter pParameter, MatchingFrameDescriptor matchingFrame) {
		matchingFrame.getVariableFromParameter(pParameter)
	}
}
