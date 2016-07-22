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

import java.util.List
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import org.eclipse.xtend.lib.annotations.Accessors

import static com.google.common.base.Preconditions.*

/**
 * @author Robert Doczi
 */
class PatternBodyDescriptor {
	
	@Accessors val PBody pBody
	@Accessors val MatchingFrameDescriptor matchingFrame
	val List<ISearchOperationDescriptor> searchOperations
	@Accessors val int index
	
	new(PBody pBody, int index, MatchingFrameDescriptor matchingFrame, List<ISearchOperationDescriptor> searchOperations) {
		checkNotNull(pBody)
		
		this.pBody = pBody
		this.matchingFrame = matchingFrame
		this.searchOperations = searchOperations
		this.index = index
	}
	
	def void addSearchOperation(ISearchOperationDescriptor searchOperation) {
		checkNotNull(searchOperation)
		
		searchOperations += searchOperation
	}

	def getSearchOperations() {
		return searchOperations.unmodifiableView
	}
	
	override toString() '''
		«FOR so : searchOperations»
			«so»
		«ENDFOR»
	'''
	
}