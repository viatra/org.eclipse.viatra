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
package org.eclipse.viatra.query.tooling.cpp.localsearch.model

import java.util.Set
import org.eclipse.emf.ecore.EClass
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * @author Robert Doczi
 */
class QueryDescriptor {

	@Accessors(PUBLIC_GETTER)
	val String name

	val Set<PatternDescriptor> patterns
	val Set<EClass> classes

	new(String name, Set<PatternDescriptor> patterns, Set<EClass> classes) {
		this.name = name

		this.patterns = patterns
		this.classes = classes
	}

	def getPatterns() {
		patterns.groupBy[it.name].unmodifiableView
	}

	def getClasses() {
		classes.unmodifiableView
	}
	
	override toString() '''
		Query<«name»>:
			«FOR pattern : patterns»
				«pattern»
			«ENDFOR»		
	'''
	
}