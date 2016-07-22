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
package org.eclipse.viatra.query.tooling.cpp.localsearch.planner

import java.util.Map
import org.eclipse.viatra.query.runtime.matchers.psystem.PBody
import com.google.common.base.Optional
import org.eclipse.viatra.query.tooling.cpp.localsearch.model.MatchingFrameDescriptor

/**
 * @author Robert Doczi
 */
class MatchingFrameRegistry {
	
	Map<PBody, MatchingFrameDescriptor> frameMap = newHashMap;
	
	def getMatchingFrame(PBody pBody) {
		return Optional::fromNullable(frameMap.get(pBody))
	}
	
	def putMatchingFrame(PBody pBody, MatchingFrameDescriptor frameStub) {
		frameMap.put(pBody, frameStub)
	}
		
}