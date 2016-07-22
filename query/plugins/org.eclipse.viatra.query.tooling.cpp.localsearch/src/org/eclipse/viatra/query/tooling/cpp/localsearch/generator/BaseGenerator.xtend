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
import com.google.common.collect.Iterables
import com.google.common.collect.Ordering
import java.util.Set
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.Include
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.CppHelper
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.GuardHelper
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.NamespaceHelper
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * @author Robert Doczi
 */
class BaseGenerator implements IGenerator{
	
	override initialize() {
	}
	
	override compile() {		
	}
	
	override compile(StringBuilder setupCode) {
	}
	
	override getFileName() {
	}
	
}

class ViatraQueryHeaderGenerator extends BaseGenerator {
	
	val Iterable<String> fullNamespace
	@Accessors(PROTECTED_GETTER) val Set<Include> includes
	val GuardHelper guard
	
	@Accessors(PROTECTED_GETTER) val NamespaceHelper implementationNamespace
	protected val String unitName
	
	

	protected new(Set<String> namespace, String unitName) {
		this.fullNamespace = Iterables::concat(#["Viatra", "Query"], namespace.map[toFirstUpper])
		this.guard = CppHelper::getGuardHelper(
			Iterables::concat(fullNamespace, #{unitName.toFirstUpper})
				.map[CaseFormat::UPPER_CAMEL.to(CaseFormat::UPPER_UNDERSCORE, it)]
				.join("__")
		)
		this.implementationNamespace = NamespaceHelper::getCustomHelper(fullNamespace)
		this.unitName = unitName.toFirstUpper
		this.includes = newTreeSet(Ordering.natural.onResultOf[includePath])
	}
	
	override getFileName() '''«unitName».h'''
	
	final def addInclude(Include include) {
		includes += include;
	}
	
	final def compileIncludes() '''
		«FOR include : includes.filter[isExternal]»
			«include.compile»
		«ENDFOR»
				
		«FOR include : includes.filter[!isExternal]»
			«include.compile»
		«ENDFOR»
	'''
	
	final override compile() '''
		«guard.start»
		
		«FOR include : includes.filter[isExternal]»
			«include.compile»
		«ENDFOR»
				
		«FOR include : includes.filter[!isExternal]»
			«include.compile»
		«ENDFOR»
		
		«FOR namespaceFragment : implementationNamespace»
			namespace «namespaceFragment» {
		«ENDFOR»
		
		«compileInner»
		
		«FOR namespaceFragment : implementationNamespace.toList.reverseView»
			} /* namespace «namespaceFragment» */
		«ENDFOR»
		
		«compileOuter»
		
		«guard.end»
	'''
	
	def compileInner() ''''''
	def compileOuter() ''''''
	
	def getInclude() {
		new Include('''«implementationNamespace.toString('/')»/«fileName»''')
	}

	def getQualifiedName() '''::«implementationNamespace.toString("::")»::«unitName»'''
}