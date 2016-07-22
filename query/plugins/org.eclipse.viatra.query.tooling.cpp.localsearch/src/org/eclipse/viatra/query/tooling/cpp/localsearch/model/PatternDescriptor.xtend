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
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PParameter
import org.eclipse.viatra.query.runtime.matchers.psystem.queries.PQuery

import static com.google.common.base.Preconditions.*

/**
 * This class represents a 
 * 
 * @author Robert Doczi
 */
class PatternDescriptor {

	val PQuery query
	val Set<PParameter> boundParameters
	
	val Set<PatternBodyDescriptor> bodies
	
	new(PQuery query, Set<PatternBodyDescriptor> bodies) {
		this(query, bodies, #{})
	}
	
	new(PQuery query, Set<PatternBodyDescriptor> bodies, Set<PParameter> boundParameters) {
		checkNotNull(query)
		checkNotNull(bodies)
		checkNotNull(boundParameters)	
		checkArgument(!bodies.empty)	
		
		this.query = query

		this.bodies = bodies
		this.boundParameters = boundParameters
	}

	def getPatternBodies() {
		bodies.unmodifiableView
	}

	def getName() {
		query.fullyQualifiedName.substring(query.fullyQualifiedName.lastIndexOf('.')+1)
	}
	
	def getBoundParameters() {
		boundParameters.unmodifiableView
	}
	
	def boolean isBound() {
		!boundParameters.empty
	}
	
	override toString() '''
		pattern <«name»> («paramList») «FOR body : bodies SEPARATOR " or "» {
			«body»
		} «ENDFOR»
		
	'''
	
	private def paramList() {
		val paramNames = newArrayList
		for(i : 0..<query.parameters.size) {
			val param = query.parameterNames.get(i)
			if(boundParameters.map[name].findFirst[it == query.parameters.get(i).name] != null)
				paramNames += param + " (B)"
			else 
				paramNames += param
		}
		
		paramNames.join(", ")
	}
	
}
