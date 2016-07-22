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
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.MatchingFrameDescriptor

/**
 * @author Robert Doczi
 */
class MatchGenerator extends ViatraQueryHeaderGenerator {

	val String queryName
	val String patternName
	@Accessors(PUBLIC_GETTER) val MatchingFrameDescriptor matchingFrame

	new(String queryName, String patternName, MatchingFrameDescriptor matchingFrame) {
		super(#{queryName}, '''«patternName.toFirstUpper»Match''')
		this.queryName = queryName
		this.patternName = patternName
		this.matchingFrame = matchingFrame

	}

	override initialize() {
		includes += matchingFrame.allTypes.map[strictType].map[
			switch it {
				EClass: Include::fromEClass(it)
				EDataType: if(it.name.toLowerCase.contains("string")) new Include("string", true)
				default: null
			}
		].filterNull
	}

	override compileInner() '''		
		struct «unitName» {
			
			«fields(matchingFrame.parameters)»
			
			«equals(matchingFrame.parameters)»
			
		};		
	'''
	
	override compileOuter() '''
		«hash(matchingFrame.parameters)»
	'''
	
	def getMatchName() {
		return unitName
	}
	
	private def fields(Iterable<PParameter> parameters) {
		'''
		«FOR parameter : parameters»
			«val type = matchingFrame.getVariableStrictType(matchingFrame.getVariableFromParameter(parameter))»
			«NameUtils::toTypeName(type)» «parameter.name»;
		«ENDFOR»
		'''
	}
	
	private def equals(Iterable<PParameter> parameters) {
		'''
		bool operator==(const «unitName»& other) const {
			return 
				«FOR variable : parameters SEPARATOR "&&"»
					«variable.toEquals»
				«ENDFOR»
			;
		}
		'''
	}
	
	private def toEquals(PParameter variable) {
		'''«variable.name» == other.«variable.name»'''
	}
	
	private def hash(Iterable<PParameter> parameters) {
		'''
		namespace std {
		
		template<> struct hash<«qualifiedName»> {
			unsigned operator()(const «qualifiedName»& match) const {
				return 
					«FOR parameter : parameters SEPARATOR '^'»
						«parameter.toHash»
					«ENDFOR»
				;
			}
		};
				
		}
		'''
	}
	
	private def toHash(PParameter variable) {
		'''std::hash<decltype(match.«variable.name»)>()(match.«variable.name»)'''
	}

}