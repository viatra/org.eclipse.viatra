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

import org.eclipse.viatra.query.tooling.cpp.localsearch.generator.common.QuerySpecificationGenerator
import java.util.Set
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternDescriptor
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.PatternBodyDescriptor

/**
 * @author Robert Doczi
 */
class IteratorQuerySpecificationGenerator extends QuerySpecificationGenerator {
	
	new(String queryName, Set<PatternDescriptor> patternGroup) {
		super(queryName, patternGroup)
	}
	
	override generatePlan(PatternDescriptor pattern, PatternBodyDescriptor patternBody) ''''''
	
		
}
