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
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternBodyDescriptor

/**
 * @author Robert Doczi
 */
abstract class QuerySpecificationGenerator extends ViatraQueryHeaderGenerator {
	
	protected val Set<PatternDescriptor> patternGroup
	protected val String queryName

	protected val String patternName
	protected val String querySpecificationName
	
	
	new(String queryName, Set<PatternDescriptor> patternGroup) {
		super(#{queryName.toFirstUpper}, '''«patternGroup.head.name.toFirstUpper»QuerySpecification''')
		this.patternGroup = patternGroup
		this.queryName = queryName.toFirstUpper
		
		this.patternName = patternGroup.head.name.toFirstUpper
		this.querySpecificationName = '''«patternName.toFirstUpper»QuerySpecification'''
	}
	
	override initialize() {
		includes += new Include('''Viatra/Query/«queryName»/«queryName»QueryGroup.h''')
		
		includes += new Include("Viatra/Query/Util/Optional.h")
		includes += new Include("Viatra/Query/Operations/AllOperations.h")
		includes += new Include("Viatra/Query/Plan/SearchPlan.h")
	}

	// TODO: Iterating over the bodies giving them indices makes the generated code nondeterministic
	override compileInner() '''
		template<class ModelRoot>
		class «patternName»Matcher;
		
		template<class ModelRoot>
		class «unitName» {
		public:
			using Matcher = «patternName»Matcher<ModelRoot>;
		
			using QueryGroup = «queryName»QueryGroup;
		
			«FOR pattern : patternGroup»
				«FOR body : pattern.patternBodies»
					«generatePlan(pattern, body)»
				«ENDFOR»
			«ENDFOR»
		
		};
	'''
	
	abstract def String generatePlan(PatternDescriptor pattern, PatternBodyDescriptor patternBody) 
	
}
