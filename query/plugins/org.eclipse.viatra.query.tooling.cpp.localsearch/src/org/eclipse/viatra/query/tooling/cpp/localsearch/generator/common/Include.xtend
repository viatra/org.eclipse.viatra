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

import org.eclipse.emf.ecore.EClass
import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.BaseGenerator
import org.eclipse.viatra.query.tooling.cpp.localsearch.util.generators.CppHelper
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.Data

/**
 * @author Robert Doczi
 */
@Data
class Include extends BaseGenerator {
	
	@Accessors(PUBLIC_GETTER)
	val boolean isExternal;
	
	private val String includePath
	private val String start;
	private val String end;
	
	new (String includePath) {
		this(includePath, false)
	}
	
	new (String includePath, boolean isExternal) {
		this.includePath = includePath
		this.isExternal = isExternal
		if(isExternal) {
			start = "<"
			end = ">"
		} else {
			start = "\""
			end = "\""
		}
	}
	
	override getFileName() {
		""	
	}
	
	override initialize() {
	}
	
	override compile() '''
		#include «start»«includePath»«end»
	'''
	
	static def fromEClass(EClass eClass) {
		return new Include(CppHelper::getIncludeHelper(eClass).toString)
	}
	
}